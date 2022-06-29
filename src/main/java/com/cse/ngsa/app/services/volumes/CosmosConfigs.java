package com.cse.ngsa.app.services.volumes;

public class CosmosConfigs {

  private String volume;
  private String cosmosUrl;
  private String cosmosKey;
  private String cosmosDatabase;
  private String cosmosCollection;
  private String cosmosName;

  public String getCosmosName() {
    return cosmosName;
  }

  public void setCosmosName(String cosmosName) {
    this.cosmosName = cosmosName;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public String getCosmosUrl() {
    return cosmosUrl;
  }

  public void setCosmosUrl(String cosmosUrl) {
    this.cosmosUrl = cosmosUrl;
  }

  public String getCosmosKey() {
    return cosmosKey;
  }

  public void setCosmosKey(String cosmosKey) {
    this.cosmosKey = cosmosKey;
  }

  public String getCosmosDatabase() {
    return cosmosDatabase;
  }

  public void setCosmosDatabase(String cosmosDatabase) {
    this.cosmosDatabase = cosmosDatabase;
  }

  public String getCosmosCollection() {
    return cosmosCollection;
  }

  public void setCosmosCollection(String cosmosCollection) {
    this.cosmosCollection = cosmosCollection;
  }

}
