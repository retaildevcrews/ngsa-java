package com.cse.ngsa.app.services.volumes;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.utils.CommonUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class VolumeCosmosConfigService implements IVolumeCosmosConfigService {
  private static final Logger logger = LogManager.getLogger(VolumeCosmosConfigService.class);
  private static final Pattern cosmosNamePat = 
      Pattern.compile("^https:\\/\\/(.+)\\.documents\\.azure\\.com.*");

  /**
   * Get all the cosmos configs from a given volume.
   */
  public CosmosConfigs getAllCosmosConfigsFromVolume(String volume) {
    CosmosConfigs cosConf = new CosmosConfigs();

    cosConf.setVolume(volume);
    cosConf.setCosmosCollection(getCosmosConfigFromFile(volume, Constants.COSMOS_COLLECTION_KEYNAME));
    cosConf.setCosmosDatabase(getCosmosConfigFromFile(volume, Constants.COSMOS_DATABASE_KEYNAME));
    cosConf.setCosmosKey(getCosmosConfigFromFile(volume, Constants.COSMOS_KEY_KEYNAME));
    cosConf.setCosmosUrl(getCosmosConfigFromFile(volume, Constants.COSMOS_URL_KEYNAME));

    Matcher m = cosmosNamePat.matcher(cosConf.getCosmosUrl());
    if (m.matches() && m.groupCount() > 0) {
      // group 0 --> total match
      // group 1 --> first group match, we only have one group
      cosConf.setCosmosName(m.group(1));
    }
    try {
      validateCosmosConfigs(volume, cosConf);
    } catch (Exception ex) {
      logger.error(ex.getMessage());
    }

    return cosConf;

  }

  /**
   * Get the cosmos config value from a file.
   */
  public String getCosmosConfigFromFile(String volume, String key) {
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
   * Checks for the cosmos config values to not contain errors.
   */
  private void validateCosmosConfigs(String volume, CosmosConfigs cosConfigs) {

    if (cosConfigs == null) {
      logger.error("Unable to read cosmos configs from volume: " +  volume);
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(cosConfigs.getCosmosCollection())) {
      logger.error("CosmosCollection cannot be empty");
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(cosConfigs.getCosmosDatabase())) {
      logger.error("CosmosDatabase cannot be empty");
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(cosConfigs.getCosmosKey())) {
      logger.error("CosmosKey cannot be empty");
      System.exit(-1);
    }

    if (CommonUtils.isNullWhiteSpace(cosConfigs.getCosmosUrl())) {
      logger.error("CosmosUrl cannot be empty");
      System.exit(-1);
    }
    
    String cosmosUrl = cosConfigs.getCosmosUrl().toLowerCase(Locale.ROOT);
    
    Matcher m = cosmosNamePat.matcher(cosConfigs.getCosmosUrl());
    if (!m.matches() || m.groupCount() != 1) {
      // group 0 --> total match
      // group 1 --> first group match, we only have one group
      logger.error("Invalid value for CosmosUrl: " + cosmosUrl);
      System.exit(-1);
    }
    
    if (cosConfigs.getCosmosKey().length() < 64) {
      logger.error("Invalid value for CosmosKey");
      System.exit(-1);
    }

  }
}
