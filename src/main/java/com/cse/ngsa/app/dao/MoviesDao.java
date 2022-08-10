package com.cse.ngsa.app.dao;

import static com.azure.spring.data.cosmos.exception.CosmosExceptionUtils.findAPIExceptionHandler;

import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.models.Movie;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesDao extends BaseCosmosDbDao implements IDao {

  @Autowired GenresDao genresDao;

  private static String movieSelect =
      "select m.id, m.partitionKey, m.movieId, m.type, m.textSearch, m.title, m.year, m.runtime,"
          + "m.rating, m.votes, m.totalScore, m.genres, m.roles from m where m.type = @type  ";

  private static String movieContains = "and contains(m.textSearch, @query) ";
  private static String movieOrderBy = " order by m.textSearch, m.movieId ";
  private static String movieOffset = " offset @offset limit @limit ";

  /** MoviesDao. */
  public MoviesDao(IConfigurationService configService) {
    super(configService);
  }

  /** getMovieByIdSingleRead. */
  public Mono<Movie> getMovieById(String movieId) {
    return getContainer()
            .readItem(movieId, Movie.computePartitionKey(movieId), Movie.class)
            .flatMap(
                cosmosItemResponse ->
                    Mono.justOrEmpty(cosmosItemResponse.getItem()))
            .onErrorResume(throwable -> findAPIExceptionHandler("Failed to find item", throwable,
                                          this.responseDiagnosticsProcessor));
  }

  /** upsertMovieById. */
  public Mono<Movie> upsertMovieById(String movieId) {

    return getMovieById(movieId).flatMap(movie -> {
      String id = movieId.replace("tt", "zz");
      movie.setId(id);
      movie.setMovieId(id);
      movie.setType("Movie-Dupe");
      return getContainer().upsertItem(movie).flatMap(
          cosmosItemResponse ->
                      Mono.just(cosmosItemResponse.getItem()))
              .onErrorResume(throwable ->
                      findAPIExceptionHandler("Failed to upsert item", throwable, 
                        this.responseDiagnosticsProcessor));
    });
  }

  /** deleteMovieById. */
  public Mono<ResponseEntity<Object>> deleteMovieById(String movieId) {
    return getContainer()
            .deleteItem(movieId, Movie.computePartitionKey(movieId))
            .flatMap(
              cosmosItemResponse ->
                      Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()))
            .onErrorResume(e -> {
              if (e.getMessage().contains("Resource Not Found")) {
                return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
              }
              return Mono.error(
                      new ResponseStatusException(
              HttpStatus.INTERNAL_SERVER_ERROR, Constants.MOVIE_CONTROLLER_EXCEPTION));
            });
  }

  /**
   * This method is responsible for checking for expected values in the queryParams dictionary
   * validating them, building the query, and then passing to the base getAll() implementation.
   *
   * @param queryParams dictionary my contain "q", "year", "ratingSelect", and "actorSelect"
   * @param pageNumber used to specify which page of the paginated results to return
   * @param pageSize used to specify the number of results per page
   * @return Flux/<T/> is returned to contains results for the specific entity type
   */
  public Flux<?> getAll(Map<String, Object> queryParams, Integer pageNumber, Integer pageSize) {

    StringBuilder formedQuery = new StringBuilder(movieSelect);

    final SqlQuerySpec movieQuerySpec = new SqlQuerySpec();

    List<SqlParameter> sqlParameterList = new ArrayList<>();
    sqlParameterList.add(new SqlParameter("@type", Constants.MOVIE_DOCUMENT_TYPE));
    sqlParameterList.add(new SqlParameter("@offset", pageNumber));
    sqlParameterList.add(new SqlParameter("@limit", pageSize));

    if (queryParams.containsKey("q")) {
      String query = queryParams.get("q").toString();
      formedQuery.append(movieContains);
      sqlParameterList.add(new SqlParameter("@query", query));
    }

    if (queryParams.containsKey("year")) {
      Integer year = (Integer) queryParams.get("year");
      if (year > 0) {
        formedQuery.append(" and m.year = @year");
        sqlParameterList.add(new SqlParameter("@year", year));
      }
    }

    if (queryParams.containsKey("ratingSelect")) {
      Double rating = (Double) queryParams.get("ratingSelect");
      if (rating > 0.0) {
        formedQuery.append(" and m.rating >= @rating");
        sqlParameterList.add(new SqlParameter("@rating", rating));
      }
    }

    if (queryParams.containsKey("actorSelect")) {
      String actorId = queryParams.get("actorSelect").toString();
      if (!StringUtils.isEmpty(actorId)) {
        formedQuery.append(" and array_contains(m.roles, { actorId: @actorId }, true) ");
        sqlParameterList.add(new SqlParameter("@actorId", actorId));
      }
    }

    // special genre call to support webflux chain
    if (queryParams.containsKey("genre")) {
      String genre = queryParams.get("genre").toString();
      if (!StringUtils.isEmpty(genre)) {
        return filterByGenre(genre, formedQuery, sqlParameterList);
      }
    }

    formedQuery.append(movieOrderBy).append(movieOffset);

    movieQuerySpec.setQueryText(formedQuery.toString());
    movieQuerySpec.setParameters(sqlParameterList);

    return super.getAll(Movie.class, movieQuerySpec);
  }

  /** filterByGenre. */
  public Flux<Movie> filterByGenre(
      String genreKey, StringBuilder formedQuery, List<SqlParameter> sqlParameterList) {
    return genresDao
        .getGenreByKey(genreKey)
        .collectList()
        .flatMapMany(
            selectedGenre -> {
              formedQuery.append(" and contains(m.genreSearch, @selectedGenre) ");
              sqlParameterList.add(new SqlParameter("@selectedGenre",
                  MessageFormat.format("|{0}|", selectedGenre.get(0))));
              formedQuery.append(movieOrderBy).append(movieOffset);

              final SqlQuerySpec genreQuerySpec = new SqlQuerySpec();

              genreQuerySpec.setQueryText(formedQuery.toString());
              genreQuerySpec.setParameters(sqlParameterList);

              return super.getAll(Movie.class, genreQuerySpec);
            });
  }
}
