package com.cse.ngsa.app.dao;

import com.azure.cosmos.CosmosAsyncClient;
import com.cse.ngsa.app.models.FeaturedMovie;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class FeaturedMovieDao extends BaseCosmosDbDao {
  private static String featuredMovieQuery =
      "select m.movieId, m.weight from m where m.type = 'Featured' ";
  @Autowired MoviesDao moviesDao;

  public FeaturedMovieDao(IConfigurationService configService) {
    super(configService);
  }

  /**
   * getFeaturedMovie.
   *
   * @return flux containing list of featured movies.
   */
  @SuppressWarnings ("squid:S1612")  // suppress warning to move lambda to function
  public Flux<String> getFeaturedMovie() {

    return this.context
            .getBean(CosmosAsyncClient.class)
            .getDatabase(this.cosmosDatabase)
            .getContainer(this.cosmosContainer)
            .queryItems(featuredMovieQuery, this.requestOptions, FeaturedMovie.class)
            .byPage()
            .flatMap(
                flatFeedResponse -> Flux.fromIterable(flatFeedResponse.getResults()))
            .sort(Comparator.comparing(FeaturedMovie::getWeight))
            .map(featuredMovie -> featuredMovie.getMovieId());
  }
}
