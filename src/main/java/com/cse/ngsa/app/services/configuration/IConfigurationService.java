package com.cse.ngsa.app.services.configuration;

import com.cse.ngsa.app.services.volumes.Secrets;


public interface IConfigurationService {

  Secrets getConfigEntries();
}
