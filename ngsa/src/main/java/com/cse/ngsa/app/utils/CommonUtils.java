package com.cse.ngsa.app.utils;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.config.BuildConfig;
import com.cse.ngsa.app.services.volumes.CosmosConfigs;
import com.cse.ngsa.app.services.volumes.IVolumeCosmosConfigService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

/**
 * CommonUtils.
 */
@Component
public class CommonUtils {
  
  @Autowired
  BuildConfig buildConfig;

  static Environment environment;

  private CommonUtils(Environment environment) {
    // disable constructor for utility class
    CommonUtils.environment = environment;
  }

  /**
   * handleCliOptions.
   *
   * @param args the log level in string form.
   */
  public static void handleCliOptions(String[] args) {
    if (args != null) {
      SimpleCommandLinePropertySource commandLinePropertySource =
          new SimpleCommandLinePropertySource(args);
      Arrays.stream(commandLinePropertySource.getPropertyNames()).forEach(s -> {
        if (s.equals("log-level") || s.equals("l")) {
          Level level = setLogLevel(commandLinePropertySource.getProperty(s));
          if (level == null) {
            printCmdLineHelp();
            System.exit(-1);
          }
          Configurator.setLevel(LogManager.getRootLogger().getName(), level);
        } else if (s.equals("help") || s.equals("h")) {
          printCmdLineHelp();
          System.exit(0);
        } else if (s.equals("version") || s.equals("v")) {
          System.exit(0);
        }
      });
    } 
  }

  /**
   * prints the application version.
   */
  @SuppressWarnings("PMD.UnusedPrivateMethod") // Invoked when bean is initialized
  @PostConstruct
  private void printCmdLineVersion() {
    if (buildConfig != null) {
      String version = buildConfig.getBuildVersion();
      System.out.printf("\r\nApplication Version:\r\n \t%s \n\n", version);
    }
  }

  private static Level setLogLevel(String logLevel) {
    switch (logLevel) {
      case "trace":
        return Level.TRACE;
      case "debug":
        return Level.DEBUG;
      case "info":
        return Level.INFO;
      case "warn":
        return Level.WARN;
      case "error":
        return Level.ERROR;
      case "fatal":
        return Level.FATAL;
      default:
        return null;
    }
  }


  /**
   * validate cli dry run option.
   */
  public static void validateCliDryRunOption(ApplicationArguments applicationArguments,
                                              IVolumeCosmosConfigService volumeCosmosConfigService,
                                              BuildConfig buildConfig) {
    if (applicationArguments != null) {
      SimpleCommandLinePropertySource commandLinePropertySource =
          new SimpleCommandLinePropertySource(applicationArguments.getSourceArgs());
      Arrays.stream(commandLinePropertySource.getPropertyNames()).forEach(s -> {
        if (s.equals("dry-run") || s.equals("d")) {
          printDryRunParameters(volumeCosmosConfigService, buildConfig);
          System.exit(0);
        }
      });
    }
  }

  @SuppressFBWarnings({"NP_UNWRITTEN_FIELD", "UWF_UNWRITTEN_FIELD"})
  @SuppressWarnings ("squid:S106") // System.out needed to print usage
  static void printDryRunParameters(IVolumeCosmosConfigService volumeCosmosConfigService,
                                    BuildConfig buildConfig) {
    System.out.println(MessageFormat.format("Version                    {0}",
        buildConfig.getBuildVersion()));
    
    CosmosConfigs cosConf = volumeCosmosConfigService.getAllCosmosConfigsFromVolume(environment.getProperty(Constants.SECRETS_VOLUME_ARGUMENT));

    System.out.println(MessageFormat.format("Cosmos Server              {0}",
            cosConf.getCosmosUrl()));

    String cosmosKey = cosConf.getCosmosKey();

    System.out.println(MessageFormat.format("Cosmos Key                 {0}",
        cosmosKey == null || cosmosKey.isEmpty() ? "(not set)".length() : cosmosKey.length()));

    System.out.println(MessageFormat.format("Cosmos Database            {0}",
            cosConf.getCosmosDatabase()));

    System.out.println(MessageFormat.format("Cosmos Collection          {0}",
            cosConf.getCosmosCollection()));
  }

  /**
   * prints the command line help.
   */
  @SuppressWarnings ("squid:S106") // System.out needed to print usage
  public static void printCmdLineHelp() {
    System.out.println("\r\nUsage:\r\n"
        + "\tmvn clean spring-boot:run -Dspring-boot.run.arguments=[options] \r\n"
        + "\r\n"
        + "\texport [env var]=<value> \r\n"
        + "\tmvn clean spring-boot:run \r\n"
        + "\r\nOptions: \r\n"
        + "\t--help                                    \t\t Show help and usage information\r\n"
        + "\t--version                                 \t\t Show version information\r\n"
        + "\t--dry-run                                 \t\t Validates configuration\r\n"
        + "\t--log-level=<trace|info|warn|error|fatal> \t\t Log Level [default: Error]\r\n"
        + "\t--burst-header                            "
        + "\t\t Enable bursting metrics (string) [default: false]\r\n"
        + "\t--burst-service=<true|false>              "
        + "\t\t Service name for bursting metrics (string) [default: ngsa-java]\r\n"
        + "\t--burst-target                            "
        + "\t\t Target level for bursting metrics (int) [default: 60]\r\n"
        + "\t--burst-max                               "
        + "\t\t Max level for bursting metrics (int) [default: 80]\r\n"
        + "\t--prometheus=<true|false>                 "
        + "\t\t Enable prometheus metrics [default: false]\r\n"
        + "\t--zone                                    "
        + "\t\t Zone for log (string) [default: dev]\r\n"
        + "\t--region                                  "
        + "\t\t Region for log (string) [default: dev]\r\n"
        + "\t--secrets-volume                          "
        + "\t\t Secrets Volume Path from project root directory (string) [default: secrets]\r\n"
        + "\r\nEnv vars: \r\n"
        + "\tBURST_HEADER=<true|false> \r\n"
        + "\tBURST_SERVICE \r\n"
        + "\tBURST_TARGET \r\n"
        + "\tBURST_MAX \r\n"
        + "\tPROMETHEUS=<true|false> \r\n"
        + "\tZONE \r\n"
        + "\tREGION \r\n"
        + "\tSECRETS_VOLUME \r\n");
  }

  /**
   * Checks if a String is not null, not empty and not whitespace.
   */
  public static boolean isNullWhiteSpace(final String string) {
    return string == null || string.isEmpty() || string.trim().isEmpty();
  }

  /**
   * Prepares burst header values.
   */
  public String getBurstHeaderValue() {
    long cpuLoad = Math.round(ManagementFactory.getPlatformMXBean(
        com.sun.management.OperatingSystemMXBean.class).getProcessCpuLoad() * 100L);

    String burstHeaderValue = String.format(
        "service=%s, current-load=%s, target-load=%s, max-load=%s ",
        environment.getProperty("burst-service"),
        cpuLoad,
        environment.getProperty("burst-target"),
        environment.getProperty("burst-max"));
    
    return burstHeaderValue;
  }

}
