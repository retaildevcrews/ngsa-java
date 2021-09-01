package com.cse.ngsa.app.controllers;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.config.BuildConfig;
import com.cse.ngsa.app.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


@RestController
public class VersionController {
  private static final Logger logger =   LogManager.getLogger(VersionController.class);

  @Autowired
  ApplicationContext context;

  @Autowired
  CommonUtils commonUtils;

  @Autowired 
  Environment environment;

  /**
   * Returns the application build version.
   *
   * @param response ServerHttpResponse passed into the alternate version handler by Spring
   * @return Mono{@literal <}Map{@literal <}String, 
   *      String{@literal <}{@literal <} container the build number
  */
  @GetMapping(name = "Ngsa Version Controller",
      value = "/version",
      produces = MediaType.TEXT_PLAIN_VALUE)
  public Mono<String> version(ServerHttpResponse response) {
    try {
      response.setStatusCode(HttpStatus.OK);
      if (environment.getProperty(Constants.BURST_HEADER_ARGUMENT).equalsIgnoreCase("true")) {
        response.getHeaders().add(Constants.BURST_HEADER_KEY, commonUtils.getBurstHeaderValue());
      }
      String version = context.getBean(BuildConfig.class).getBuildVersion();
      
      return Mono.just(version);
    } catch (Exception ex) {
      logger.warn("Error received in VersionController", ex);
      return Mono.error(new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, "version Error"));
    }
  }
}
