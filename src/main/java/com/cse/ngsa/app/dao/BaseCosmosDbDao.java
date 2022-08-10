package com.cse.ngsa.app.dao;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.implementation.InternalObjectNode;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.spring.data.cosmos.core.ResponseDiagnostics;
import com.azure.spring.data.cosmos.core.ResponseDiagnosticsProcessor;
import com.azure.spring.data.cosmos.core.convert.ObjectMapperFactory;
import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Flux;

public class BaseCosmosDbDao {

  private static final Logger logger = LogManager.getLogger(BaseCosmosDbDao.class);

  @Autowired ApplicationContext context;

  protected IConfigurationService configurationService;
  protected String cosmosContainer = "";
  protected String cosmosDatabase = "";
  protected final CosmosQueryRequestOptions requestOptions = new CosmosQueryRequestOptions();
  protected ResponseDiagnosticsProcessor responseDiagnosticsProcessor = new ResponseDiagnosticsProcessorImpl();

  /** BaseCosmosDbDao. */
  @Autowired
  public BaseCosmosDbDao(IConfigurationService configService) {
    configurationService = configService;
    cosmosContainer =
        configurationService.getConfigEntries().getCosmosCollection();
    cosmosDatabase = configurationService.getConfigEntries().getCosmosDatabase();

    requestOptions.setMaxDegreeOfParallelism(Constants.MAX_DEGREE_PARALLELISM);
  }

  /** getContainer. */
  public CosmosAsyncContainer getContainer() {
    return context
            .getBean(CosmosAsyncClient.class)
            .getDatabase(this.cosmosDatabase)
            .getContainer(this.cosmosContainer);
  }

  /**
   * Common template method used to execute and retrieve queries for the given type using the passed
   * in query.
   *
   * @param classType used for the object mapper to map results into object
   * @param sqlQuerySpec is passed in from the specific data access object and used to fetch
   *     matching records
   * @return Flux/<T/> is returned to contains results for the specific entity type
   */
  public <T> Flux<T> getAll(Class<T> classType, SqlQuerySpec sqlQuerySpec) {
    ObjectMapper objMapper = ObjectMapperFactory.getObjectMapper();
    Flux<FeedResponse<InternalObjectNode>> feedResponse =
        getContainer().queryItems(sqlQuerySpec, this.requestOptions, InternalObjectNode.class).byPage();

    return feedResponse
                .flatMap(
                    flatFeedResponse -> Flux.fromIterable(flatFeedResponse.getResults()))
                .flatMap(
                    internalObjectNode -> {
                      try {
                        return Flux.just(
                            objMapper.readValue(internalObjectNode.toJson(), classType));
                      } catch (JsonProcessingException e) {
                        logger.error(e);
                      } 
                      return Flux.empty();
                    });
  }

  private static class ResponseDiagnosticsProcessorImpl implements ResponseDiagnosticsProcessor {
    @Override
    public void processResponseDiagnostics(ResponseDiagnostics responseDiagnostics) {
      logger.info("Response Diagnostics: {}", responseDiagnostics);
    }
  }
}
