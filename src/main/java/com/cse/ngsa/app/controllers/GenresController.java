package com.cse.ngsa.app.controllers;

import com.cse.ngsa.app.dao.GenresDao;
import com.cse.ngsa.app.utils.ParameterValidator;
import io.swagger.annotations.Api;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/** GenresController. */
@RestController
@RequestMapping(path = "/api/genres", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Genres")
public class GenresController {
  private static final Logger logger =   LogManager.getLogger(GenresController.class);

  @Autowired
  GenresDao genresDao;
  @Autowired ParameterValidator validator;

  /** getAllGenres. */
  @GetMapping(
      value = "",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<List<String>> getAllGenres() {
    try {
      return genresDao.getGenres();
    } catch (Exception ex) {

      logger.warn("Error received in GenresController", ex);
      return Mono.error(new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, "genres Error"));
    }
  }
}
