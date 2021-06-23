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
          Configurator.setLevel("com.cse.ngsa",
              level);
        }
        if (s.equals("help") || s.equals("h")) {
          printCmdLineHelp();
          System.exit(0);
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
        + "\tmvn clean spring-boot:run -Dspring-boot.run.arguments=[options] \r\n"
        + "\r\nOptions: \r\n"
        + "\t--help                                    \t\t Show help and usage information\r\n"
        + "\t--cpu.target.load                         "
        + "\t\t Target level for bursting metrics (int) [default: 60]\r\n"
        + "\t--cpu.max.load                            "
        + "\t\t Max level for bursting metrics (int) [default: 80]\r\n"
        + "\t--dry-run                                 \t\t Validates configuration\r\n"
        + "\t--log-level=<trace|info|warn|error|fatal> \t\t Log Level [default: Error]\"");
  }

  /**
   * Checks if a String is not null, not empty and not whitespace.
   */
  public static boolean isNullWhiteSpace(final String string) {
    return string == null || string.isEmpty() || string.trim().isEmpty();
  }

}
