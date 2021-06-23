package com.cse.ngsa.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebfluxConfig implements WebFluxConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    registry.addResourceHandler("/swagger-ui.html**")
        .addResourceLocations("classpath:/static/");
    registry.addResourceHandler("/swagger/ngsa.json**")
        .addResourceLocations("classpath:/static/swagger/");

  }
}