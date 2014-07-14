package org.gbif.dwc.validator.cli;

import org.gbif.dwc.validator.evaluator.chain.DefaultEvaluationChainProvider;
import org.gbif.dwc.validator.handler.ArchiveContentHandler;
import org.gbif.dwc.validator.handler.ArchiveStructureHandler;
import org.gbif.dwc.validator.impl.ArchiveValidator;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

  public ValidatorMain(String[] args) {

    Map<String, String> cliArgs = CliManager.parseCommandLine(args);
    String sourceFileLocation = cliArgs.get(CliManager.CLI_SOURCE);

    // ensure source file was provided
    if (StringUtils.isBlank(sourceFileLocation)) {
      CliManager.printHelp();
      return;
    }

    String sourceIdentifier = Long.toString(System.currentTimeMillis());
    File workingFolder = new File("validator-dwca-" + sourceIdentifier);
    workingFolder.mkdir();

    if (isURL(sourceFileLocation)) {
      System.out.println("Downloading file from: " + sourceFileLocation);
      File dFile = new File(workingFolder, workingFolder + ".zip");
      try {
        downloadFile(new URL(sourceFileLocation), dFile);
        // sourceFileLocation is now the downloaded file
        sourceFileLocation = dFile.getAbsolutePath();
      } catch (MalformedURLException e) {
        LOGGER.error("Issue source file URL: " + sourceFileLocation, e);
      }
    }

    // ensure the source file exists
    if (!new File(sourceFileLocation).exists()) {
      System.out.println("The file " + sourceFileLocation + " could not be found.");
      return;
    }

    ArchiveValidator archiveValidator = new ArchiveValidator();
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    archiveValidator.setWorkingFolder(workingFolder.getAbsolutePath());
    archiveValidator.setStructureHandler(new ArchiveStructureHandler());
    archiveValidator.setContentHandler(new ArchiveContentHandler(new DefaultEvaluationChainProvider()));

    long startTime = System.currentTimeMillis();
    // run validation
    archiveValidator.validateArchive(new File(sourceFileLocation), resultAccumulator);

    System.out.println("Validation took: " + (System.currentTimeMillis() - startTime) + " ms");

    // Print results
    CliReportPrinter.printReport(resultAccumulator);

    // cleanup
    FileUtils.deleteQuietly(workingFolder);
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
}
