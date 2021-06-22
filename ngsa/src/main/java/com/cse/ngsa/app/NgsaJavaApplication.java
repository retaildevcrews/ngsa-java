package com.cse.ngsa.app;

import com.cse.ngsa.app.utils.CommonUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebFlux
@EnableSwagger2
@ComponentScan("com.cse.ngsa")
public class NgsaJavaApplication {

  /**
  * main.
  */
  public static void main(String[] args) {
    
    CommonUtils.handleCliLogLevelOption(args);
    SpringApplication.run(NgsaJavaApplication.class, args);
  }
}
