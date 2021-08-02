package com.cse.ngsa.app.services.configuration;

import com.cse.ngsa.app.services.volumes.CosmosConfigs;

public interface IConfigurationService {

  CosmosConfigs getConfigEntries();
}
