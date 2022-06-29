package com.cse.ngsa.app.middleware;

import com.cse.ngsa.app.models.NgsaConfig;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import com.cse.ngsa.app.utils.CpuMonitor;
import com.cse.ngsa.app.utils.QueryUtils;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestLogger implements WebFilter {

  private static final Logger logger =   LogManager.getLogger(RequestLogger.class);

  MeterRegistry promRegistry;
  @Autowired CpuMonitor cpuMonitor;
  @Autowired NgsaConfig ngsaConfig;
  @Autowired private IConfigurationService cfgSvc;
  @Value("${region:dev}") private String ngsaRegion;
  @Value("${zone:dev}") private String ngsaZone;
  @Value("${request-log-level:INFO}") private String ngsaRequestLogger;

  public RequestLogger(MeterRegistry registry) {
    promRegistry = registry;
  }

  // Suppressing since its invoked when bean is initialized
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  @PostConstruct
  private void setLoggingLevel() {

    // Setting logger level for this class only
    Configurator.setLevel(this.getClass().getTypeName(),
        Level.getLevel(ngsaRequestLogger.toUpperCase()));
  }

  /**
   * filter gathers the request and response metadata and logs
   *   the results to console.
  */
  @Override
  public Mono<Void> filter(ServerWebExchange serverWebExchange, 
      WebFilterChain webFilterChain) {
    // get request metadata
    String requestAddress = getRequestAddress(
        serverWebExchange.getRequest().getRemoteAddress());
    String pathQueryString = getPathQueryString(serverWebExchange.getRequest());

    // set start time
    long startTime = System.currentTimeMillis();

    // process next handler
    return webFilterChain.filter(serverWebExchange).doFinally(signalType -> {
      int statusCode = serverWebExchange.getResponse().getStatusCode().value();

      // don't log favicon.ico 404s
      if (pathQueryString.startsWith("/favicon.ico")) {
        return;
      }

      // don't log if log level >= warn but response code < 400
      if (logger.getLevel().isMoreSpecificThan(Level.WARN) && statusCode < 400) {
        return;
      }

      JSONObject logData = new JSONObject();
      var currentRequest = serverWebExchange.getRequest();
      String userAgent = currentRequest.getHeaders()
          .getOrDefault("User-Agent", Arrays.asList("")).get(0);
      // compute request duration and get status code
      long duration = System.currentTimeMillis() - startTime;
      logData.put("Date", Instant.now().toString());
      logData.put("LogName", "Ngsa.RequestLog");
      logData.put("StatusCode", statusCode);
      logData.put("TTFB", duration); // Essentially ttfb in Java's case is the same as duraion
      logData.put("Duration", duration);
      logData.put("Verb", currentRequest.getMethod().toString());
      logData.put("Path", pathQueryString);
      InetSocketAddress host = currentRequest.getHeaders().getHost();
      logData.put("Host", host == null ? "" : host.toString());
      logData.put("ClientIP", requestAddress);
      logData.put("UserAgent", userAgent);

      SpanContext spanContext = Span.current().getSpanContext();
      logData.put("TraceID", spanContext.getTraceId());
      logData.put("SpanID", spanContext.getSpanId());

      // Get category and mode from Request
      String[] categoryAndMode = QueryUtils.getCategoryAndMode(serverWebExchange.getRequest());
      String mode = categoryAndMode[2];

      if (mode.equals("Direct")
          || mode.equals("Query")
          || mode.equals("Delete")
          || mode.equals("Upsert")) {
        String[] promTags = {"code", QueryUtils.getPrometheusCode(statusCode),
            "cosmos", "True", // Hardcoding True since we only implemented Cosmos
            "region", ngsaRegion,
            "zone", ngsaZone,
            "mode", mode};
        // Using .getProcessCPULoad() direclty makes process_cpu_usage unusable
        // Not sure why
        Gauge.builder("NgsaCpuPercent", cpuMonitor, x -> x.getCpuUsagePercent())
            .description("CPU Percent Used")
            .register(promRegistry);
        DistributionSummary
            .builder("NgsaAppDuration")
            .description("Histogram of NGSA App request duration")
            .tags(promTags)
            .register(promRegistry) // it won't not register everytime
            .record(duration);
        DistributionSummary
            .builder("NgsaAppSummary")
            .description("Summary of NGSA App request duration")
            .tags(promTags)
            .register(promRegistry) // it won't not register everytime
            .record(duration);
      }

      logData.put("Category", categoryAndMode[0]);
      logData.put("SubCategory", categoryAndMode[1]);
      logData.put("Mode", mode);
      logData.put("Zone", ngsaZone);
      logData.put("Region", ngsaRegion);
      logData.put("CosmosName", cfgSvc.getConfigEntries().getCosmosName());
      // log results to console
      logger.info(logData.toString());
    });
  }

  /** getRequestAddress returns the request IP address if it exists. */
  private String getRequestAddress(InetSocketAddress requestAddress) {
    if (requestAddress != null) {
      return requestAddress.getHostString();
    }
    return "";
  }

  /** getPathQueryString returns the path and query string if it exists. */
  private String getPathQueryString(ServerHttpRequest request) {
    String pathQueryString = request.getURI().getPath();
    String query = request.getURI().getQuery();
    if (query != null) {
      pathQueryString += "?" + query;
    }

    return pathQueryString;
  }
}
