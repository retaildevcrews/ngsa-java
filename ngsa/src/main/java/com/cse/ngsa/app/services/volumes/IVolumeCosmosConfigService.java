package com.cse.ngsa.app.services.volumes;

public interface IVolumeCosmosConfigService {
  CosmosConfigs getAllCosmosConfigsFromVolume(String volume);

  String getCosmosConfigFromFile(String volume, String key);

}
