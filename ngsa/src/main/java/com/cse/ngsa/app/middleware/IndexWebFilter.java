package com.cse.ngsa.app.middleware;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class IndexWebFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String uriPath = exchange.getRequest().getURI().getPath();
    if (uriPath.equals("/") || uriPath.equals("")) {
      return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().path("/index.html").build()).build());
    }

    return chain.filter(exchange);
  }
}
