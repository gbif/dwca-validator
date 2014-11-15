package org.gbif.dwc.validator.result.accumulator;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Simple ResultAccumulator implementation to save results in CSV file.
 * Allow headers to be internationalized.
 * 
 * @author cgendreau
 */
public class CSVValidationResultAccumulator extends AbstractTresholdResultAccumulator {

  private CSVPrinter csvPrinter;
  private final String filePath;

  public CSVValidationResultAccumulator(String filePath) throws IOException {
    super();
    this.filePath = filePath;

    openWriter();
  }

  @Override
  protected void openWriter() throws IOException {
    csvPrinter = new CSVPrinter(new FileWriter(filePath), CSVFormat.DEFAULT);
    printHeaders();
  }

  @Override
  protected void closeWriter() throws IOException {
    csvPrinter.flush();
    csvPrinter.close();
  }

  /**
   * Print headers of the CSV file
   */
  private void printHeaders() throws IOException {
    csvPrinter.printRecord(ValidatorConfig.getLocalizedString("result.header.id"),
      ValidatorConfig.getLocalizedString("result.header.evaluator"),
      ValidatorConfig.getLocalizedString("result.header.context"),
      ValidatorConfig.getLocalizedString("result.header.context_details"),
      ValidatorConfig.getLocalizedString("result.header.type"),
      ValidatorConfig.getLocalizedString("result.header.result"),
      ValidatorConfig.getLocalizedString("result.header.explanation"));
  }

  @Override
  protected void write(ValidationResult currentResult) throws IOException {
    for (ValidationResultElement vre : currentResult.getResults()) {
      csvPrinter.printRecord(currentResult.getId(), currentResult.getEvaluatorKey(),
        currentResult.getEvaluationContext(), currentResult.getEvaluationContextDetails(),
        ValidatorConfig.getLocalizedString(vre.getType().getDescriptionKey()), vre.getResult(), vre.getExplanation());
    }
  }

}
