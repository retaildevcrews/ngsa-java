package com.cse.ngsa.app.services.configuration;

import com.cse.ngsa.app.services.volumes.Secrets;

@SuppressWarnings ("checkstyle:AbbreviationAsWordInName")
public interface IConfigurationService {

  Secrets getConfigEntries();
}
