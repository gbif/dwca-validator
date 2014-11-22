package org.gbif.dwc.validator.result.accumulator.csv;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.accumulator.AbstractThresholdAccumulatorSupport;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Threshold based accumulator saving ValidationResult to CSV file.
 * 
 * @author cgendreau
 */
class CSVValidationResultAccumulator extends AbstractThresholdAccumulatorSupport<ValidationResult> {

  private CSVPrinter validationCsvPrinter;
  private final String validationResultFilePath;

  CSVValidationResultAccumulator(String validationResultFilePath) {
    this(validationResultFilePath, DEFAULT_THRESHOLD);
  }

  CSVValidationResultAccumulator(String validationResultFilePath, int threshold) {
    super(threshold);
    this.validationResultFilePath = validationResultFilePath;
  }

  private void openCsvPrinter() throws IOException {
    validationCsvPrinter = new CSVPrinter(new FileWriter(validationResultFilePath), CSVFormat.DEFAULT);
    printValidationResultHeaders();
  }

  /**
   * Print headers of the validation result CSV file
   */
  private void printValidationResultHeaders() throws IOException {
    validationCsvPrinter.printRecord(ValidatorConfig.getLocalizedString("result.header.id"),
      ValidatorConfig.getLocalizedString("result.header.evaluator"),
      ValidatorConfig.getLocalizedString("result.header.context"),
      ValidatorConfig.getLocalizedString("result.header.context_details"),
      ValidatorConfig.getLocalizedString("result.header.type"),
      ValidatorConfig.getLocalizedString("result.header.result"),
      ValidatorConfig.getLocalizedString("result.header.explanation"));
  }

  @Override
  protected void write(ValidationResult result) throws IOException {
    if (validationCsvPrinter == null) {
      openCsvPrinter();
    }

    validationCsvPrinter.printRecord(result.getId(), result.getEvaluatorKey(), result.getEvaluationContext(),
      result.getResults());
  }

  @Override
  protected void closeWriter() throws IOException {
    if (validationCsvPrinter != null) {
      validationCsvPrinter.flush();
      validationCsvPrinter.close();
    }
  }

}
