package com.cse.ngsa.app.dao;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.models.Genre;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GenresDao extends BaseCosmosDbDao {
  private static final Logger logger =   LogManager.getLogger(GenresDao.class);

  private static String genreQuery =
      "select m.genre from m where m.type = 'Genre' order by m.genre ";

  private static String genreQueryById =
      "select m.genre from m where m.type = @id and m.id = @type";

  public GenresDao(IConfigurationService configService) {
    super(configService);
  }

  /**
   * Get list of Genres.
   *
   * @return list of Genres.
   */
  public Mono<List<String>> getGenres() {
    if (logger.isInfoEnabled()) {
      logger.info(MessageFormat.format("genreQuery {0}", genreQuery));
    }

    SqlQuerySpec sqsGenreQuery = new SqlQuerySpec(genreQuery);

    return this.context
            .getBean(CosmosAsyncClient.class)
            .getDatabase(this.cosmosDatabase)
            .getContainer(this.cosmosContainer)
            .queryItems(sqsGenreQuery, this.requestOptions, Genre.class)
            .byPage()
            .flatMap(
                flatFeedResponse -> Flux.fromIterable(flatFeedResponse.getResults()))
            .map(internalObjectNode -> internalObjectNode.getGenre())
            .collectList();
  }

  /** getGenreByKey. */
  @SuppressWarnings ("squid:S1612")  // suppress warning to move lambda to function
  public Flux<String> getGenreByKey(String genreKey) {
    List<SqlParameter> sqlParameterList = new ArrayList<>();
    sqlParameterList.add(new SqlParameter("@id", Constants.GENRE_DOCUMENT_TYPE));
    sqlParameterList.add(new SqlParameter("@type", genreKey.toLowerCase()));

    SqlQuerySpec sqsGenreQueryById = new SqlQuerySpec(genreQueryById, sqlParameterList);

    return getContainer()
            .queryItems(sqsGenreQueryById, this.requestOptions, Genre.class)
            .byPage()
            .flatMap(
                cosmosItemResponse -> Flux.fromIterable(cosmosItemResponse.getResults()))
            .map(selectedGenre -> selectedGenre.getGenre());
  }
}
