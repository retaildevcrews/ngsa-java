package com.cse.ngsa.app.services.volumes;

@SuppressWarnings ("checkstyle:AbbreviationAsWordInName")
public interface IVolumeSecretService {
  Secrets getAllSecretsFromVolume(String volume);

  String getSecretFromFile(String volume, String key);

}
