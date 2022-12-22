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
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableCosmosRepositories(basePackages = "com.microsoft.azure.ngsa.app.*")
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "azure-config")
public class CosmosDbConfig extends AbstractCosmosConfiguration {

  private static final Logger logger = LogManager.getLogger(CosmosConfig.class);

  protected IConfigurationService configurationService;

  @Getter
  @Setter
  private String azureTenantId;

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
        DefaultAzureCredentialBuilder credBuilder = new DefaultAzureCredentialBuilder();

        // Add the tenant id if specified
        if (!azureTenantId.isBlank()) {
          credBuilder.tenantId(azureTenantId);
        }
        builder = builder.credential(credBuilder.build());
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
