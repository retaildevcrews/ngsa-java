package com.cse.ngsa.app.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/api/benchmark", produces = MediaType.TEXT_PLAIN_VALUE)
@Api(tags = "Benchmark")
public class BenchmarkController {
  private static final Logger logger =   LogManager.getLogger(BenchmarkController.class);
  private static final int maxBenchSize = 1024 * 1024;
  /* Java 1.5+
  private final String repeatedBenchString = 
          new String(new char[1024 * 1024 / 16]).replace("\0", "0123456789ABCDEF");
  */
  private final String repeatedBenchString =
                "0123456789ABCDEF".repeat(1024 * 1024 / 16); /*Java 11*/

  /** getBenchmark. */
  @GetMapping(
      value = "/{size}",
      produces = MediaType.TEXT_PLAIN_VALUE)
  public Flux<String> getBenchmark(
      @ApiParam(value = "The size of the benchmark data ( 0 < size <= 1MB )",
                example = "214", required = true)
      @PathVariable("size")
      int benchmarkSize,
      ServerHttpRequest request
  ) {

    try {
      if (benchmarkSize < 1) {
        String err = "Invalid Size. Size must be > 0";
        logger.error(err);

        return Flux.error(new ResponseStatusException(
          HttpStatus.BAD_REQUEST, String.format("Benchmark Error: %s", err)));

      } else if (benchmarkSize > maxBenchSize) {
        String err = "Invalid Size. Size must be <= 1024 * 1024 (1 MB)";
        logger.error(err);

        return Flux.error(new ResponseStatusException(
          HttpStatus.BAD_REQUEST, String.format("Benchmark Error: %s", err)));
      }
      return Flux.just(repeatedBenchString.substring(0, benchmarkSize));
    } catch (Exception ex) {
      logger.error("Error received in BenchmarkController", ex);

      return Flux.error(new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, "benchmark Error"));
    }
  }
}
