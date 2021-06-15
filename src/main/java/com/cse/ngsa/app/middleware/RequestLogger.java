package com.cse.ngsa.app.middleware;

import com.cse.ngsa.app.utils.QueryUtils;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Arrays;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestLogger implements WebFilter {

  private static final Logger logger =   LogManager.getLogger(RequestLogger.class);
 
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
      logData.put("Host", currentRequest.getHeaders().getHost().toString());
      logData.put("ClientIP", requestAddress);
      logData.put("UserAgent", userAgent);
      logData.put("CVector", "PLACEHOLDER-CV-VALUE");
      logData.put("CVectorBase", "PLACEHOLDER-CV-BASE-VALUE");
      String[] categoryAndMode = QueryUtils.getCategoryAndMode(serverWebExchange.getRequest());
      logData.put("Category", categoryAndMode[0]);
      logData.put("SubCategory", categoryAndMode[1]);
      logData.put("Mode", categoryAndMode[2]);
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
