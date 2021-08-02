package com.cse.ngsa.app.utils;

import com.cse.ngsa.app.config.BuildConfig;
import com.cse.ngsa.app.services.volumes.IVolumeCosmosConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * DryRunProcessor.
 */
@Component
public class DryRunProcessor {

  @Autowired
  ApplicationArguments applicationArguments;

  @Autowired
  private BuildConfig buildConfig;

  @Autowired
  IVolumeCosmosConfigService volumeCosmosConfigService;


  private static final Logger logger = LogManager.getLogger(DryRunProcessor.class);

  /**
   * onApplicationEvent.
   */
  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    logger.info("Application Context has been fully started up");
    logger.info("All beans are now instantiated and ready to go!");
    CommonUtils.validateCliDryRunOption(applicationArguments,
            volumeCosmosConfigService, buildConfig);
  }

}
