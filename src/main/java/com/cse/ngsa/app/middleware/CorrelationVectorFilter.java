package com.cse.ngsa.app.middleware;

import com.cse.ngsa.app.utils.CorrelationVectorExtensions;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(1)
public class CorrelationVectorFilter implements WebFilter {
  @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, 
      WebFilterChain webFilterChain) {

        // Check for Correlation Vector in request header
        // Create one if doesn't exist
        var cv = CorrelationVectorExtensions.extend(serverWebExchange);
        // And save them as attributes
        CorrelationVectorExtensions.putCVIntoServerWebExchangeAttribute(cv, 
          serverWebExchange);

        // Call the next filter
        return webFilterChain.filter(serverWebExchange);
    } 
}
