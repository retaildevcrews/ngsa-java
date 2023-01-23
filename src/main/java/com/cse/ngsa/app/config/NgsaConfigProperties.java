package com.cse.ngsa.app.config;

import com.cse.ngsa.app.Constants;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "")
@Validated
public class NgsaConfigProperties {
  @Valid
  @NotBlank
  @Getter @Setter
  private String region;

  @Valid
  @NotBlank
  @Getter @Setter
  private String zone;

  @Valid
  @Getter @Setter
  private String urlPrefix;
  @Valid
  @NotBlank
  @Getter @Setter
  private String urlPrefixValue;

  @Valid
  @NotBlank
  @Getter @Setter
  private String secretsVolume;

  @Valid
  @NotBlank
  @Pattern(regexp = "^" + Constants.COSMOS_AUTH_TYPE_MI + "$|^" + Constants.COSMOS_AUTH_TYPE_SECRETS
      + "$", message = "CosmosAuthType should be '"
          + Constants.COSMOS_AUTH_TYPE_MI + "'' or '" + Constants.COSMOS_AUTH_TYPE_SECRETS + "'")
  @Getter @Setter
  private String cosmosAuthType;

  @Valid
  @NotBlank
  @Pattern(regexp = "^TRACE$|^trace$|^DEBUG$|^debug$|^INFO$|^info$|^WARN$|^warn$|^ERROR$|^error$|^FATAL$|^fatal$|^OFF$|^off$",
          message = "Log levels must be any one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF")
  @Getter @Setter
  private String requestLogLevel;

  // standard getters and setters
  @Override
  public String toString() {
    return String.format("Region: %s, Zone: %s", this.region, this.zone);
  }
}
