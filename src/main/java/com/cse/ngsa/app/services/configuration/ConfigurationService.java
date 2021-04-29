package com.cse.ngsa.app.services.configuration;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.services.volumes.IVolumeSecretService;
import com.cse.ngsa.app.services.volumes.Secrets;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService implements IConfigurationService {
  private static final Logger logger =   LogManager.getLogger(ConfigurationService.class);

  private IVolumeSecretService volumeSecretService;

  Secrets configEntries;

  public Secrets getConfigEntries() {
    return configEntries;
  }

  /**
   * ConfigurationService.
   */
  @SuppressFBWarnings("DM_EXIT")
  @Autowired
  public ConfigurationService(IVolumeSecretService vsService) throws Exception {
    try {
      if (vsService == null) {
        logger.error("volumeSecretService is null");
        System.exit(-1);
      }
      volumeSecretService = vsService;
      configEntries = volumeSecretService.getAllSecretsFromVolume(Constants.SECRETS_VOLUME);

    } catch (Exception ex) {
      logger.error(ex.getMessage());
      throw ex;
    }
  }
}