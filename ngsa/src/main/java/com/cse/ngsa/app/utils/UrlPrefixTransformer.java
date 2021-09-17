package com.cse.ngsa.app.utils;

import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.resource.ResourceTransformer;
import org.springframework.web.reactive.resource.ResourceTransformerChain;
import org.springframework.web.reactive.resource.TransformedResource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reads the resource content and replaces all {{url-prefix}} with specified
 * url-prefix url-prefix can be specified in cli as --url-prefix=VALUE or in
 * application.properties
 */
@Component
public class UrlPrefixTransformer implements ResourceTransformer {

  // TODO: Validation
  @Value("${url-prefix:}")
  private String urlPrefix;

  @Value("${url-prefix-value:{{url-prefix}}}")
  private String urlPrefixValue;

  @Override
  public Mono<Resource> transform(ServerWebExchange exchange, Resource resource,
        ResourceTransformerChain transformerChain) {
    System.out.println(String.format("UrlPrefix: %s, prefix Val: %s", urlPrefix, urlPrefixValue));
    String rsrcStr;
    try {
      rsrcStr = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
      rsrcStr = rsrcStr.replace(urlPrefixValue, urlPrefix);
    } catch (Exception e) {
      e.printStackTrace();
      // Mostly for IOException
      // TODO: What to do when this rare exception occurs
      rsrcStr = "";
    }
    return Mono.just(new TransformedResource(resource, rsrcStr.getBytes()));
  }
}
