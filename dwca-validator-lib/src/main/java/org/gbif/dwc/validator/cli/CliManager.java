package org.gbif.dwc.validator.cli;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Manager to handle command line arguments provided by the user.
 * 
 * @author cgendreau
 */
public class CliManager {

  private static final Options cmdLineOptions;

  static final String CLI_SOURCE = "s";
  static final String CLI_OUT = "o";
  static final String CLI_CONFIG = "c";
  static final String CLI_OUTPUT_FORMAT = "of";

  static {
    cmdLineOptions = new Options();
    cmdLineOptions.addOption(CLI_SOURCE, true, "Path or URL pointing to the DarwinCore Archive file");
    cmdLineOptions.addOption(CLI_OUT, true, "Output folder (optional)");
    cmdLineOptions.addOption(CLI_OUTPUT_FORMAT, true, "Output format: csv or json (optional)");
    cmdLineOptions.addOption(CLI_CONFIG, true, "Path of a configuration file (optional)");
  }

  /**
   * Function to parse the command line arguments into Map.
   * 
   * @param args
   * @return parsed command line arguments or empty map, never null.
   */
  public static Map<String, String> parseCommandLine(String[] args) {
    Map<String, String> cmdValues = new HashMap<String, String>();
    CommandLineParser parser = new PosixParser();
    CommandLine cmdLine;
    try {
      cmdLine = parser.parse(cmdLineOptions, args);
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      return null;
    }
    cmdValues.put(CLI_SOURCE, cmdLine.getOptionValue(CLI_SOURCE));
    cmdValues.put(CLI_OUT, cmdLine.getOptionValue(CLI_OUT));
    cmdValues.put(CLI_OUTPUT_FORMAT, cmdLine.getOptionValue(CLI_OUTPUT_FORMAT));
    cmdValues.put(CLI_CONFIG, cmdLine.getOptionValue(CLI_CONFIG));
    return cmdValues;
  }

  /**
   * Print the "usage" to the standard output.
   */
  public static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("dwca-validator", cmdLineOptions);
  }

}
