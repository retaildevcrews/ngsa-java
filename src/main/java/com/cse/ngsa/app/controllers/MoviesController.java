package com.cse.ngsa.app.controllers;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.dao.MoviesDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/** MovieController. */
@RestController
@RequestMapping(path = "/api/movies",
    produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Movies")
public class MoviesController extends Controller {

  @Autowired MoviesDao moviesDao;

  private static final Logger logger = LogManager.getLogger(MoviesController.class);
  private static final String INVALID_MOVIE_MSG = "Invalid Movie ID parameter {0}";

  /** getMovie. */
  @GetMapping(value = "/{id}")
  // to suppress wrapping the logger.error() in a conditional and lambda to function
  @SuppressWarnings({"squid:S2629", "squid:S1612"})  
  public Object getMovie(
      @ApiParam(value = "The ID of the movie to look for", example = "tt0000002", required = true)
          @PathVariable("id")
          String movieId,
      ServerHttpRequest request) {

    if (logger.isInfoEnabled()) {
      logger.info(MessageFormat.format("getMovie (movieId={0})", movieId));
    }

    if (Boolean.TRUE.equals(validator.isValidMovieId(movieId))) {
      return moviesDao
          .getMovieById(movieId)
          .switchIfEmpty(
              Mono.defer(
                  () ->
                      Mono.error(
                          new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie Not Found"))));
    } else {

      logger.warn(MessageFormat.format(INVALID_MOVIE_MSG, movieId));

      String invalidResponse = super.invalidParameterResponses
          .invalidMovieDirectReadResponse(request.getURI().getPath());

      return ResponseEntity.badRequest()
          .contentType(MediaType.APPLICATION_PROBLEM_JSON)
          .body(invalidResponse);
    }
  }

  /** upsertMovie. */
  @PutMapping(value = "/{id}")
  // to suppress wrapping the logger.error() in a conditional and lambda to function
  @SuppressWarnings({"squid:S2629", "squid:S1612"})
  public Object upsertMovie(
          @PathVariable("id")
                  String movieId,
          ServerHttpRequest request) {

    if (logger.isInfoEnabled()) {
      logger.info(MessageFormat.format("upsertMovie (movieId={0})", movieId));
    }

    String movieIdOrig = movieId.replace("zz", "tt");
    if (Boolean.TRUE.equals(validator.isValidMovieId(movieIdOrig) && movieId.startsWith("zz"))) {
      return moviesDao.upsertMovieById(movieIdOrig);
    } else {
      logger.warn(MessageFormat.format(INVALID_MOVIE_MSG, movieId));
      String invalidResponse = super.invalidParameterResponses
              .invalidMovieDeleteResponse(request.getURI().getPath());
      return ResponseEntity.badRequest()
              .contentType(MediaType.APPLICATION_PROBLEM_JSON)
              .body(invalidResponse);
    }
  }

  /** deleteMovie. */
  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  // to suppress wrapping the logger.error() in a conditional and lambda to function
  @SuppressWarnings({"squid:S2629", "squid:S1612"})
  public Object deleteMovie(
          @PathVariable("id")
                  String movieId,
          ServerHttpRequest request) {

    if (logger.isInfoEnabled()) {
      logger.info(MessageFormat.format("deleteMovie (movieId={0})", movieId));
    }
    if (Boolean.TRUE.equals(validator.isValidMovieId(movieId.replace("zz", "tt")))
            && movieId.startsWith("zz")) {
      return moviesDao.deleteMovieById(movieId);
    } else {
      logger.warn(MessageFormat.format(INVALID_MOVIE_MSG, movieId));
      String invalidResponse = super.invalidParameterResponses
                  .invalidMovieDeleteResponse(request.getURI().getPath());
      return ResponseEntity.badRequest()
                  .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                  .body(invalidResponse);
    }
  }

  /** getAllMovies. */
  @GetMapping(value = "")
  public Object getAllMovies(
      @ApiParam(value = "(query) (optional) The term used to search Movie name") @RequestParam("q")
          final Optional<String> query,
      @ApiParam(value = "(optional) Movies of a genre (Action)") @RequestParam
          final Optional<String> genre,
      @ApiParam(value = "(optional) Get movies by year (2005)", defaultValue = "0") @RequestParam
          final Optional<String> year,
      @ApiParam(value = "(optional) Get movies with a rating >= rating (8.5)", defaultValue = "0")
          @RequestParam
          final Optional<String> rating,
      @ApiParam(value = "(optional) Get movies with a rating >= rating (8.5)", defaultValue = "0")
          @RequestParam
          final Optional<String> actorId,
      @ApiParam(value = "(optional) Get movies by Actor Id (nm0000704)") @RequestParam
          Optional<String> pageNumber,
      @ApiParam(value = "page size (1000 max)", defaultValue = "100") @RequestParam
          Optional<String> pageSize,
      ServerHttpRequest request) {

    try {
      if (logger.isInfoEnabled()) {
        logger.info(
            MessageFormat.format(
                "getAllMovies (query={0}, genre={1}, year={2}, rating={3}, "
                    + " actorId={4}, pageNumber={5}, pageSize={6})",
                query, genre, year, rating, actorId, pageNumber, pageSize));
      }
      URI uri = request.getURI();
      String instance = uri.getPath() + "?" + uri.getQuery();
      return getAll(query, genre, year, rating, actorId, pageNumber, pageSize, moviesDao, instance);
    } catch (Exception ex) {
      logger.warn(MessageFormat.format("MovieControllerException {0}", ex.getMessage()));
      return Flux.error(
          new ResponseStatusException(
              HttpStatus.INTERNAL_SERVER_ERROR, Constants.MOVIE_CONTROLLER_EXCEPTION));
    }
  }
}
