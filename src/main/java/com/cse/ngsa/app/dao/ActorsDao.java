package com.cse.ngsa.app.dao;

import static com.azure.spring.data.cosmos.exception.CosmosExceptionUtils.findAPIExceptionHandler;

import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.models.Actor;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ActorsDao extends BaseCosmosDbDao implements IDao {
  private static final Logger logger = LogManager.getLogger(ActorsDao.class);

  private static String actorSelect =
      "select m.id, m.partitionKey, m.actorId, m.type, "
          + "m.name, m.birthYear, m.deathYear, m.profession, "
          + "m.textSearch, m.movies from m where m.type = @type ";

  private static String actorContains = "and contains(m.textSearch, @query) ";
  private static String actorOrderBy = " order by m.textSearch, m.actorId ";
  private static String actorOffset = " offset @offset limit @limit ";

  /** ActorsDao. */
  public ActorsDao(IConfigurationService configService) {
    super(configService);
  }

  /** getActorByIdSingleRead. */
  public Mono<Actor> getActorById(String actorId) {
    if (logger.isInfoEnabled()) {
      logger.info(MessageFormat.format("Call to getActorById ({0})", actorId));
    }
    
    return getContainer()
            .readItem(actorId, Actor.computePartitionKey(actorId), Actor.class)
            .flatMap(
                cosmosItemResponse -> 
                    Mono.justOrEmpty(cosmosItemResponse.getItem()))
            .onErrorResume(throwable -> findAPIExceptionHandler("Failed to find item", throwable,
                                          this.responseDiagnosticsProcessor));
  }

  /**
   * This method is responsible for checking for expected values in the queryParams dictionary
   * validating them, building the query, and then passing to the base getAll() implementation.
   *
   * @param queryParams for actors this is a single query value stored in the key "q"
   * @param pageNumber used to specify which page of the paginated results to return
   * @param pageSize used to specify the number of results per page
   * @return Flux/<T/> is returned to contains results for the specific entity type
   */
  public Flux<?> getAll(Map<String, Object> queryParams, Integer pageNumber, Integer pageSize) {
    String query = null;

    if (queryParams.containsKey("q")) {
      query = queryParams.get("q").toString();
    }

    final SqlQuerySpec actorQuerySpec = new SqlQuerySpec();
    List<SqlParameter> sqlParameterList = new ArrayList<>();
    sqlParameterList.add(new SqlParameter("@type", Constants.ACTOR_DOCUMENT_TYPE));
    sqlParameterList.add(new SqlParameter("@offset", pageNumber));
    sqlParameterList.add(new SqlParameter("@limit", pageSize));

    StringBuilder queryBuilder = new StringBuilder(actorSelect);
    if (query != null) {
      queryBuilder.append(actorContains);
      sqlParameterList.add(new SqlParameter("@query", query));
    }
    queryBuilder.append(actorOrderBy).append(actorOffset);

    actorQuerySpec.setQueryText(queryBuilder.toString());
    actorQuerySpec.setParameters(sqlParameterList);

    return super.getAll(Actor.class, actorQuerySpec);
  }
}
