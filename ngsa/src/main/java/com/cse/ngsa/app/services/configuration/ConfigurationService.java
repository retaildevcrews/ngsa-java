package com.cse.ngsa.app.services.configuration;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.services.volumes.CosmosConfigs;
import com.cse.ngsa.app.services.volumes.IVolumeCosmosConfigService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService implements IConfigurationService {

  private static final Logger logger =   LogManager.getLogger(ConfigurationService.class);

  private IVolumeCosmosConfigService volumeCosmosConfigService;

  CosmosConfigs configEntries;

  public CosmosConfigs getConfigEntries() {
    return configEntries;
  }

  /**
   * ConfigurationService.
   */
  @SuppressFBWarnings("DM_EXIT")
  @Autowired
  public ConfigurationService(IVolumeCosmosConfigService vcosConfService, Environment environment) throws Exception {
    try {
      if (vcosConfService == null) {
        logger.error("volumeCosmosConfigService is null");
        System.exit(-1);
      }
      volumeCosmosConfigService = vcosConfService;
      configEntries = volumeCosmosConfigService.getAllCosmosConfigsFromVolume(environment.getProperty(Constants.SECRETS_VOLUME_ARGUMENT));
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      throw ex;
    }
  }
}
