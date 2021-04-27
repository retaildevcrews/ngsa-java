package com.cse.ngsa.app.models;

import com.cse.ngsa.app.config.BuildConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NgsaConfig {
  @Bean
  public BuildConfig buildConfig() {
    return new BuildConfig();
  }
}