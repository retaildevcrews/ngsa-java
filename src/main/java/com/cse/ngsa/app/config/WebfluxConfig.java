package com.cse.ngsa.app.config;

import com.cse.ngsa.app.utils.UrlPrefixTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebfluxConfig implements WebFluxConfigurer {

  /**
   * This transformer will replace all {{url-prefix}} token in index.html and swagger.json
   */
  @Autowired
  UrlPrefixTransformer transformer;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    registry.addResourceHandler("/index.html**")
        .addResourceLocations("classpath:/static/")
        .resourceChain(false)
        .addTransformer(transformer); // Adding urlPrefix transformer
    registry.addResourceHandler("/swagger.json**")
        .addResourceLocations("classpath:/static/")
        .resourceChain(false)
        .addTransformer(transformer); // Adding urlPrefix transformer

  }
}
