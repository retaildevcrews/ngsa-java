package com.cse.ngsa.app.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.resource.ResourceTransformer;
import org.springframework.web.reactive.resource.ResourceTransformerChain;
import org.springframework.web.reactive.resource.TransformedResource;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reads the resource content and replaces all {{url-prefix}} with specified
 * url-prefix url-prefix can be specified in cli as --url-prefix=VALUE or in
 * application.properties
 */
@Component
public class UrlPrefixTransformer implements ResourceTransformer {

  @Value("${url-prefix:}")
  private String urlPrefix;

  @Value("${url-prefix-value:{{url-prefix}}}")
  private String urlPrefixValue;

  @Override
  public Mono<Resource> transform(ServerWebExchange exchange, Resource resource,
        ResourceTransformerChain transformerChain) {

    String rsrcStr;
    try {
      if (!resource.exists()) {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "error: index.html not found"));
      }
      rsrcStr = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
      rsrcStr = rsrcStr.replace(urlPrefixValue, urlPrefix);
    } catch (IOException ex) {
      // IOException - In case of file reading exception
      return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "error: permission denied for index.html"));
    } catch (Exception e) {
      // Other Exceptions
      return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error: " + e.getMessage()));
    }
    return Mono.just(new TransformedResource(resource, rsrcStr.getBytes()));
  }
}
