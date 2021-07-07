package com.cse.ngsa.app.middleware;

import com.cse.ngsa.app.utils.CorrelationVectorExtensions;
import com.cse.ngsa.app.utils.CpuMonitor;
import com.cse.ngsa.app.utils.QueryUtils;
import com.microsoft.correlationvector.CorrelationVector;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Arrays;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

  public RequestLogger(MeterRegistry registry) {
    promRegistry = registry;
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

      // In general, no need to check if serverWebExchabge have MS-CV attribute
      // But a good practice to check for it anyway
      CorrelationVector cv;
      if (serverWebExchange.getAttributes().containsKey(CorrelationVector.HEADER_NAME)) {
        cv = (CorrelationVector)serverWebExchange.getAttribute(CorrelationVector.HEADER_NAME);
      } else {
        // This should never happen!
        LogManager.getRootLogger().error("MS-CV Attribute not found. Creating a new one");
        cv = CorrelationVectorExtensions.extend(serverWebExchange);
      }
      logData.put("CVector", cv.getValue());
      logData.put("CVectorBase", cv.getBaseVector());
      
      // Get category and mode from Request
      String[] categoryAndMode = QueryUtils.getCategoryAndMode(serverWebExchange.getRequest());
      String mode = categoryAndMode[2];

      if (mode.equals("Direct")
          || mode.equals("Query")
          || mode.equals("Delete")
          || mode.equals("Upsert")) {
        String[] promTags = {"code", QueryUtils.getPrometheusCode(statusCode),
            "cosmos", "True", // Hardcoding True since we only implemented Cosmos
            "region", "dev",
            "zone", "dev",
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
      logData.put("Zone", "dev"); // TODO: Update all props below
      logData.put("Region", "dev");
      logData.put("CosmosName", "in-memory");
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
