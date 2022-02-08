package com.cse.ngsa.app.config;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
  @Size(min = 2, max = 14)
  @Getter @Setter
  private String burstService;

  @Min(5)
  @Max(100)
  @Getter @Setter
  @Valid
  private int burstTarget;

  @Min(10)
  @Max(100)
  @Getter @Setter
  @Valid
  private int burstMax;

  @Valid
  @Getter @Setter
  private Boolean burstHeader;

  @Valid
  @NotBlank
  @Getter @Setter
  private String secretsVolume;

  @Valid
  @NotBlank
  @Pattern(regexp = "^TRACE$|^trace$|^DEBUG$|^debug$|^INFO$|^info$|^WARN$|^warn$|^ERROR$|^error$|^FATAL$|^fatal$|^OFF$|^off$",
          message = "Log levels must be any one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF")
  @Getter @Setter
  private String requestLogLevel;

  // standard getters and setters
  @Override
  public String toString() {
    return String.format("Region: %s, Zone: %s, BurstTarget: %s, BurstService: %s", this.region, this.zone, this.burstTarget, this.burstService);
  }
}
