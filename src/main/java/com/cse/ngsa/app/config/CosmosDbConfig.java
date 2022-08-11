package com.cse.ngsa.app.config;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.ThrottlingRetryOptions;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import java.text.MessageFormat;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCosmosRepositories(basePackages = "com.microsoft.azure.ngsa.app.*")
public class CosmosDbConfig extends AbstractCosmosConfiguration {

  private static final Logger logger = LogManager.getLogger(CosmosConfig.class);

  protected IConfigurationService configurationService;

  /**
   * CosmosDBConfig.
   */
  @Autowired
  public CosmosDbConfig(IConfigurationService configService) {
    configurationService = configService;
  }

  /**
   * CosmosDBConfig.
   */
  @Bean
  @Primary
  public CosmosClientBuilder buildCosmosDbConfig() {
    try {

      String uri = configurationService.getConfigEntries().getCosmosUrl();
      String key = configurationService.getConfigEntries().getCosmosKey();

      ThrottlingRetryOptions throttlingRetryOptions = new ThrottlingRetryOptions();
      throttlingRetryOptions.setMaxRetryWaitTime(Duration.ofSeconds(60));

      return new CosmosClientBuilder()
          .endpoint(uri)
          .key(key)
          .consistencyLevel(ConsistencyLevel.SESSION)
          .throttlingRetryOptions(throttlingRetryOptions);


    } catch (Exception ex) {
      logger.error(MessageFormat.format("buildCosmosDbConfig failed with error: {0}", 
          ex.getMessage()));

      throw ex;
    }
  }

  /**
   * getDatabaseName.
   */
  protected String getDatabaseName() {
    return configurationService.getConfigEntries().getCosmosDatabase();
  }
}
