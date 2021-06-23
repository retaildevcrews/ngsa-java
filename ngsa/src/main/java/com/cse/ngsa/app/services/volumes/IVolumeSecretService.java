package com.cse.ngsa.app.services.volumes;

public interface IVolumeSecretService {
  Secrets getAllSecretsFromVolume(String volume);

  String getSecretFromFile(String volume, String key);

}
