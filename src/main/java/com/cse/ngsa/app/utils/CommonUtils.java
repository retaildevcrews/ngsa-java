package com.cse.ngsa.app.utils;

import com.cse.ngsa.app.Constants;
import com.cse.ngsa.app.config.BuildConfig;
import com.cse.ngsa.app.services.volumes.IVolumeSecretService;
import com.cse.ngsa.app.services.volumes.Secrets;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.text.MessageFormat;
import java.util.Arrays;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

/**
 * CommonUtils.
 */
@Component
public class CommonUtils {

  private CommonUtils() {
    // disable constructor for utility class
  }

  /**
   * handleCliLogLevelOption.
   *
   * @param args the log level in string form.
   */
  public static void handleCliLogLevelOption(String[] args) {
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
          Configurator.setLevel("com.cse.ngsa",
              level);
        }
      });
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
                                              IVolumeSecretService volumeSecretService,
                                              BuildConfig buildConfig) {
    if (applicationArguments != null) {
      SimpleCommandLinePropertySource commandLinePropertySource =
          new SimpleCommandLinePropertySource(applicationArguments.getSourceArgs());
      Arrays.stream(commandLinePropertySource.getPropertyNames()).forEach(s -> {
        if (s.equals("dry-run") || s.equals("d")) {
          printDryRunParameters(volumeSecretService, buildConfig);
          System.exit(0);
        }
      });
    }
  }

  @SuppressFBWarnings({"NP_UNWRITTEN_FIELD", "UWF_UNWRITTEN_FIELD"})
  @SuppressWarnings ("squid:S106") // System.out needed to print usage
  static void printDryRunParameters(IVolumeSecretService volumeSecretService,
                                    BuildConfig buildConfig) {
    System.out.println(MessageFormat.format("Version                    {0}",
        buildConfig.getBuildVersion()));

    Secrets sec = volumeSecretService.getAllSecretsFromVolume(Constants.SECRETS_VOLUME);

    System.out.println(MessageFormat.format("Cosmos Server              {0}",
            sec.getCosmosUrl()));

    String cosmosKey = sec.getCosmosKey();

    System.out.println(MessageFormat.format("Cosmos Key                 {0}",
        cosmosKey == null || cosmosKey.isEmpty() ? "(not set)".length() : cosmosKey.length()));

    System.out.println(MessageFormat.format("Cosmos Database            {0}",
            sec.getCosmosDatabase()));

    System.out.println(MessageFormat.format("Cosmos Collection          {0}",
            sec.getCosmosCollection()));
  }

  /**
   * prints the command line help.
   */
  @SuppressWarnings ("squid:S106") // System.out needed to print usage
  public static void printCmdLineHelp() {
    System.out.println("\r\nUsage:\r\n"
        + "   mvn clean spring-boot:run \r\n "
        + "\t-Dspring-boot.run.arguments=\" --help \r\n"
        + "\t\t--service.name\r\n"
        + "\t\t--cpu.target.load\r\n"
        + "\t\t--cpu.max.load\r\n"
        + "\t\t--dry-run\r\n"
        + "\t\t--log-level=<trace|info|warn|error|fatal>\"");
  }

  /**
   * Checks if a String is not null, not empty and not whitespace.
   */
  public static boolean isNullWhiteSpace(final String string) {
    return string == null || string.isEmpty() || string.trim().isEmpty();
  }

}
