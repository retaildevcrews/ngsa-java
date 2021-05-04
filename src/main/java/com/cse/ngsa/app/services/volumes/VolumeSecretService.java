package com.cse.ngsa.app.services.volumes;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.services.configuration.ConfigurationService;
import com.cse.ngsa.app.utils.CommonUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class VolumeSecretService implements IVolumeSecretService {
  private static final Logger logger =   LogManager.getLogger(ConfigurationService.class);

  /**
   * Get all the secrets from a given volume.
   */
  public Secrets getAllSecretsFromVolume(String volume) {
    Secrets sec = new Secrets();

    sec.setVolume(volume);
    sec.setCosmosCollection(getSecretFromFile(volume, Constants.COSMOS_COLLECTION_KEYNAME));
    sec.setCosmosDatabase(getSecretFromFile(volume, Constants.COSMOS_DATABASE_KEYNAME));
    sec.setCosmosKey(getSecretFromFile(volume,Constants.COSMOS_KEY_KEYNAME));
    sec.setCosmosUrl(getSecretFromFile(volume,Constants.COSMOS_URL_KEYNAME));

    try {
      validateSecrets(volume, sec);
    } catch (Exception ex) {
      logger.error(ex.getMessage());
    }

    return sec;

  }

  /**
   * Get the secret value from a file.
   */
  public String getSecretFromFile(String volume, String key) {
    String val = "";

    Path filePath = Path.of(volume + "/" + key);

    if (Files.exists(filePath)) {
      try {
        val = new String(Files.readAllBytes(filePath));
      } catch (IOException ex) {
        logger.error(ex.getMessage());
      }

    }
    return val;
  }

  /**
   * Checks for the secret values to not contain errors.
   */
  private void validateSecrets(String volume, Secrets secrets) {

    if (secrets == null) {
      logger.error("Unable to read secrets from volume: " +  volume);
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(secrets.getCosmosCollection())) {
      logger.error("CosmosCollection cannot be empty");
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(secrets.getCosmosDatabase())) {
      logger.error("CosmosDatabase cannot be empty");
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(secrets.getCosmosKey())) {
      logger.error("CosmosKey cannot be empty");
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(secrets.getCosmosUrl())) {
      logger.error("CosmosUrl cannot be empty");
      System.exit(-1);
    }

    String cosmosUrl = secrets.getCosmosUrl().toLowerCase(Locale.ROOT);

    if (!cosmosUrl.startsWith("https://") || !cosmosUrl.contains(".documents.azure.com")) {
      logger.error("Invalid value for CosmosUrl: " + cosmosUrl);
      System.exit(-1);
    }

    if (secrets.getCosmosKey().length() < 64) {
      logger.error("Invalid value for CosmosKey");
      System.exit(-1);
    }

  }
}