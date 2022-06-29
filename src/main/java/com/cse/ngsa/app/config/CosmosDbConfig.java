package com.cse.ngsa.app.config;

import com.azure.data.cosmos.ConnectionPolicy;
import com.azure.data.cosmos.ConsistencyLevel;
import com.azure.data.cosmos.RetryOptions;
import com.azure.data.cosmos.internal.RequestOptions;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import com.microsoft.azure.spring.data.cosmosdb.config.AbstractCosmosConfiguration;
import com.microsoft.azure.spring.data.cosmosdb.config.CosmosDBConfig;
import com.microsoft.azure.spring.data.cosmosdb.repository.config.EnableCosmosRepositories;
import java.text.MessageFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCosmosRepositories(basePackages = "com.microsoft.azure.ngsa.app.*")
public class CosmosDbConfig extends AbstractCosmosConfiguration {

  private static final Logger logger = LogManager.getLogger(CosmosDbConfig.class);

  protected IConfigurationService configurationService;

  protected final RequestOptions requestOptions = new RequestOptions();

  /**
   * CosmosDBConfig.
   */
  @Autowired
  public CosmosDbConfig(IConfigurationService configService) {
    configurationService = configService;

    requestOptions.setConsistencyLevel(ConsistencyLevel.SESSION);
    requestOptions.setScriptLoggingEnabled(true);
  }

  /**
   * CosmosDBConfig.
   */
  @Bean
  @Primary
  public CosmosDBConfig buildCosmosDbConfig() {
    try {

      String uri = configurationService.getConfigEntries().getCosmosUrl();
      String key = configurationService.getConfigEntries().getCosmosKey();
      String dbName = configurationService.getConfigEntries()
          .getCosmosDatabase();

      ConnectionPolicy policy = new ConnectionPolicy();
      RetryOptions retryOptions = new RetryOptions();
      retryOptions.maxRetryWaitTimeInSeconds(60);
      policy.retryOptions(retryOptions);

      return CosmosDBConfig.builder(uri, key, dbName)
          .requestOptions(requestOptions).connectionPolicy(policy)
          .build();
    } catch (Exception ex) {
      logger.error(MessageFormat.format("buildCosmosDbConfig failed with error: {0}", 
          ex.getMessage()));

      throw ex;
    }
  }
}
