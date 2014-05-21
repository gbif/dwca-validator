package org.gbif.dwc.validator.cli;

import org.gbif.dwc.validator.evaluator.chain.DefaultEvaluationChainProvider;
import org.gbif.dwc.validator.handler.ArchiveContentHandler;
import org.gbif.dwc.validator.handler.ArchiveStructureHandler;
import org.gbif.dwc.validator.impl.ArchiveValidator;
import org.gbif.dwc.validator.result.ValidationResult;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Main class used to run the library in command line.
 * 
 * @author cgendreau
 */
public class ValidatorMain {

  public ValidatorMain(String[] args) {

    Map<String, String> cliArgs = CliManager.parseCommandLine(args);

    String sourceFilePath = cliArgs.get(CliManager.CLI_SOURCE);

    // ensure source file was provided
    if (StringUtils.isBlank(sourceFilePath)) {
      CliManager.printHelp();
      return;
    }

    // ensure the source file exists
    if (!new File(sourceFilePath).exists()) {
      System.out.println("The file " + sourceFilePath + " could not be found.");
      return;
    }

    ArchiveValidator archiveValidator = new ArchiveValidator();
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    File testDwcFolder = new File("validator-dwca-" + System.currentTimeMillis());
    testDwcFolder.mkdir();
    archiveValidator.setWorkingFolder(testDwcFolder.getAbsolutePath());
    archiveValidator.setStructureHandler(new ArchiveStructureHandler());
    archiveValidator.setContentHandler(new ArchiveContentHandler(new DefaultEvaluationChainProvider()));

    // run validation
    archiveValidator.validateArchive(new File(sourceFilePath), resultAccumulator);

    // Print results
    if (resultAccumulator.getCount() > 0) {
      System.out.println("The Dwc-A file looks invalid according to current default validation chain:");
      for (ValidationResult vr : resultAccumulator.getValidationResultsList()) {
        System.out.println(vr);
      }
    } else {
      System.out.println("The Dwc-A file looks valid according to current default validation chain.");
    }

  }

  public static void main(String[] args) {
    new ValidatorMain(args);
  }

}
