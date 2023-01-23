package com.cse.ngsa.app.config;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.ThrottlingRetryOptions;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.models.NgsaConfig;
import com.cse.ngsa.app.services.configuration.IConfigurationService;
import java.text.MessageFormat;
import java.time.Duration;
import kotlin.NotImplementedError;
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
  public CosmosClientBuilder buildCosmosDbConfig(NgsaConfig ngsaConfig) {
    try {

      String uri = configurationService.getConfigEntries().getCosmosUrl();

      ThrottlingRetryOptions throttlingRetryOptions = new ThrottlingRetryOptions();
      throttlingRetryOptions.setMaxRetryWaitTime(Duration.ofSeconds(60));

      CosmosClientBuilder builder = new CosmosClientBuilder().endpoint(uri);
      String cosmosAuthType = ngsaConfig.ngsaConfigProperties().getCosmosAuthType();
      if (cosmosAuthType.equals(Constants.COSMOS_AUTH_TYPE_MI)) {
        builder = builder.credential(new DefaultAzureCredentialBuilder().build());
      } else if (cosmosAuthType.equals(Constants.COSMOS_AUTH_TYPE_SECRETS)) {
        String key = configurationService.getConfigEntries().getCosmosKey();
        builder = builder.key(key);
      } else {
        throw new NotImplementedError("Other Cosmos Auth types are not implemented");
      }

      return builder
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
