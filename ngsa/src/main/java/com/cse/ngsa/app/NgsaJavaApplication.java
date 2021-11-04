package com.cse.ngsa.app;

import com.cse.ngsa.app.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
  private static final Logger logger =   LogManager.getLogger(NgsaJavaApplication.class);

  /**
  * main.
  */
  public static void main(String[] args) {

    try {
      SpringApplication.run(NgsaJavaApplication.class, args);
      CommonUtils.handleCliOptions(args);
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      System.exit(1);
    }
  }
}
