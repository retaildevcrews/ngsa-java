package com.cse.ngsa.app.services.keyvault;

public interface IEnvironmentReader {
  String getAuthType();

  String getKeyVaultName();
}
