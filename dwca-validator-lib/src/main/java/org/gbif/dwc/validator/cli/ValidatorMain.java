package org.gbif.dwc.validator.cli;

import org.gbif.dwc.validator.Evaluators;
import org.gbif.dwc.validator.FileEvaluator;
import org.gbif.dwc.validator.chain.EvaluatorChain;
import org.gbif.dwc.validator.config.FileBasedValidationChainLoader;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.accumulator.csv.CSVResultAccumulator;
import org.gbif.dwc.validator.result.accumulator.json.JSONResultAccumulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class used to run the library from command line.
 * Only zipped archive can be validated for now.
 * 
 * @author cgendreau
 */
public class ValidatorMain {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorMain.class);
  private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SS");
  private static final String RESULT_FILENAME = "ValidationResults_";

  public ValidatorMain(String[] args) {

    Map<String, String> cliArgs = CliManager.parseCommandLine(args);
    String sourceFileLocation = cliArgs.get(CliManager.CLI_SOURCE);
    String resultFolderLocation = cliArgs.get(CliManager.CLI_OUT);
    String resultOutputFormat =
      StringUtils.defaultIfBlank(cliArgs.get(CliManager.CLI_OUTPUT_FORMAT), ValidatorConfig.CSV_OUTPUT_FORMAT);
    String configurationFile = cliArgs.get(CliManager.CLI_CONFIG);

    String sourceIdentifier = Long.toString(System.currentTimeMillis());

    // TODO probably load from configuration file
    ValidatorConfig validatorConfig = ValidatorConfig.getInstance();

    // ensure source file was provided and resultOutputFormat is a valid format
    if (StringUtils.isBlank(sourceFileLocation) || !isValidResultOutputFormat(resultOutputFormat)) {
      CliManager.printHelp();
      return;
    }

    // handle output folder/file location
    File outputFile = handleValidationResultFile(resultFolderLocation, resultOutputFormat);
    if (outputFile == null) {
      CliManager.printHelp();
      return;
    }

    // ensure working folder exists
    if (!validatorConfig.getWorkingFolder().exists() && !validatorConfig.getWorkingFolder().mkdirs()) {
      System.out.println("Error, can not create temporary folder in "
        + validatorConfig.getWorkingFolder().getAbsolutePath());
      return;
    }

    // build validation chain
    File tmpFolder = new File(validatorConfig.getWorkingFolder(), "validator-dwca-" + sourceIdentifier);
    tmpFolder.mkdir();
    FileEvaluator archiveValidator;
    if (StringUtils.isNotBlank(configurationFile)) {
      archiveValidator =
        Evaluators.buildFromValidationChain(tmpFolder, handleConfigurationFile(new File(configurationFile)));
      if (archiveValidator == null) {
        return;
      }
    } else {
      archiveValidator = Evaluators.defaultChain(tmpFolder).build();
    }

    if (isURL(sourceFileLocation)) {
      System.out.println("Downloading file from: " + sourceFileLocation);
      File dFile = new File(tmpFolder, tmpFolder.getName() + ".zip");
      try {
        downloadFile(new URL(sourceFileLocation), dFile);
        // sourceFileLocation is now the downloaded file
        sourceFileLocation = dFile.getAbsolutePath();
        System.out.println("Download completed");
      } catch (MalformedURLException e) {
        LOGGER.error("Issue source file URL: " + sourceFileLocation, e);
      }
    }

    // ensure the source file exists
    if (!new File(sourceFileLocation).exists()) {
      System.out.println("The file " + sourceFileLocation + " could not be found.");
      return;
    }


    ResultAccumulator resultAccumulator = handleResultAccumulator(outputFile, resultOutputFormat);

    long startTime = System.currentTimeMillis();
    System.out.println("Starting validation ... ");
    // run validation
    archiveValidator.evaluateFile(new File(sourceFileLocation), resultAccumulator);

    System.out.println("Validation took: " + (System.currentTimeMillis() - startTime) + " ms");

    try {
      resultAccumulator.close();
    } catch (ResultAccumulationException e) {
      LOGGER.error("Closing result accumulator", e);
    }

    if (resultAccumulator.getValidationResultCount() > 0) {
      System.out.println("The Dwc-A file is not valid according to current validation chain:");
      System.out.println("Results available in " + outputFile.getAbsolutePath());
    } else {
      System.out.println("The Dwc-A file looks valid according to current validation chain.");
    }

    // cleanup
    FileUtils.deleteQuietly(tmpFolder);
  }

  public static void main(String[] args) {
    new ValidatorMain(args);
  }

  /**
   * Download a file from a URL and save it locally.
   * 
   * @param url
   * @param destinationFile
   * @return
   */
  private boolean downloadFile(URL url, File destinationFile) {
    OutputStream os = null;
    InputStream is = null;
    boolean success = false;

    try {
      os = new FileOutputStream(destinationFile);
      is = url.openStream();

      // Download the file
      IOUtils.copy(is, os);
      success = true;
    } catch (FileNotFoundException e) {
      LOGGER.error("Issue while downloading " + url, e);
    } catch (IOException e) {
      LOGGER.error("Issue while downloading " + url, e);
    } finally {
      IOUtils.closeQuietly(os);
      IOUtils.closeQuietly(is);
    }
    return success;
  }

  /**
   * Check if the provided source is a URL or not.
   * 
   * @param source
   * @return
   */
  private boolean isURL(String source) {
    UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    return urlValidator.isValid(source);
  }

  /**
   * Checks if the provided resultOutputFormat is a valid format.
   * 
   * @param resultOutputFormat
   * @return
   */
  private boolean isValidResultOutputFormat(String resultOutputFormat) {
    return resultOutputFormat.equalsIgnoreCase(ValidatorConfig.CSV_OUTPUT_FORMAT)
      || resultOutputFormat.equalsIgnoreCase(ValidatorConfig.JSON_OUTPUT_FORMAT);
  }

  /**
   * Load the EvaluatorChain from a configuration file.
   * 
   * @param configurationFile
   * @return
   */
  private EvaluatorChain handleConfigurationFile(File configurationFile) {
    if (!configurationFile.exists()) {
      LOGGER.error("Can not find the configuration file from " + configurationFile.getAbsolutePath());
      return null;
    }

    FileBasedValidationChainLoader fbValidationChainLoader = new FileBasedValidationChainLoader();
    try {
      return fbValidationChainLoader.buildValidationChainFromYamlFile(configurationFile);
    } catch (IOException ioEx) {
      LOGGER.error("Issue while loading validation chain from configuration file.", ioEx);
    }
    return null;
  }

  /**
   * Get a configured ResultAccumulator based on the result output format.
   * 
   * @param outputFile File where the result will be printed
   * @param resultOutputFormat format to use to output result
   * @return configured ResultAccumulator or null if no ResultAccumulator match the output format.
   */
  private ResultAccumulator handleResultAccumulator(File outputFile, String resultOutputFormat) {
    if (resultOutputFormat.equalsIgnoreCase(ValidatorConfig.CSV_OUTPUT_FORMAT)) {
      return new CSVResultAccumulator(outputFile.getAbsolutePath());
    } else if (resultOutputFormat.equalsIgnoreCase(ValidatorConfig.JSON_OUTPUT_FORMAT)) {
      return new JSONResultAccumulator(outputFile.getAbsolutePath());
    }
    return null;
  }

  /**
   * Handle the name and location of validation result file.
   * 
   * @param resultFolderLocation if null, the current folder will be used
   * @param extension extension to use for the validation result file.
   * @return the file
   */
  private File handleValidationResultFile(String resultFolderLocation, String extension) {
    File outputFolder = new File("");
    if (StringUtils.isNotBlank(resultFolderLocation)) {
      File resultFolder = new File(resultFolderLocation);
      if (resultFolder.exists() && !resultFolder.isDirectory()) {
        return null;
      }
      outputFolder = resultFolder;
    }
    return new File(outputFolder.getAbsoluteFile(), RESULT_FILENAME + DF.format(Calendar.getInstance().getTime()) + "."
      + extension);
  }
}
