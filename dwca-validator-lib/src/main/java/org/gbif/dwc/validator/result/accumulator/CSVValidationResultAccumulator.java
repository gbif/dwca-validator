package org.gbif.dwc.validator.result.accumulator;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

/**
 * Simple ResultAccumulator implementation to save results in CSV file.
 * Allow headers to be internationalized.
 * 
 * @author cgendreau
 */
public class CSVValidationResultAccumulator extends AbstractTresholdResultAccumulator {

  private CSVPrinter validationCsvPrinter;
  private CSVPrinter aggregationCsvPrinter;
  private final String validationResultFilePath;
  private final String aggregationResultFilePath;

  /**
   * Only Validation results will be recorded.
   * 
   * @param validationResultFilePath
   */
  public CSVValidationResultAccumulator(String validationResultFilePath) {
    this(validationResultFilePath, null);
  }

  public CSVValidationResultAccumulator(String validationResultFilePath, String aggregationResultFilePath) {
    super(StringUtils.isNotBlank(validationResultFilePath), StringUtils.isNotBlank(aggregationResultFilePath));
    this.validationResultFilePath = validationResultFilePath;
    this.aggregationResultFilePath = aggregationResultFilePath;
  }

  protected void openValidationCsvPrinter() throws IOException {
    validationCsvPrinter = new CSVPrinter(new FileWriter(validationResultFilePath), CSVFormat.DEFAULT);
    printValidationResultHeaders();
  }

  protected void openAggregationCsvPrinter() throws IOException {
    aggregationCsvPrinter = new CSVPrinter(new FileWriter(aggregationResultFilePath), CSVFormat.DEFAULT);
    printAggregationResultHeaders();
  }

  @Override
  protected void closeWriter() throws IOException {

    if (validationCsvPrinter != null) {
      validationCsvPrinter.flush();
      validationCsvPrinter.close();
    }
    if (aggregationCsvPrinter != null) {
      aggregationCsvPrinter.flush();
      aggregationCsvPrinter.close();
    }
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

  private void printAggregationResultHeaders() throws IOException {
    aggregationCsvPrinter.printRecord(ValidatorConfig.getLocalizedString("result.header.id"),
      ValidatorConfig.getLocalizedString("result.header.evaluator"),
      ValidatorConfig.getLocalizedString("result.header.context"),
      ValidatorConfig.getLocalizedString("result.header.result"));
  }

  @Override
  protected void write(ValidationResult currentResult) throws IOException {

    if (validationCsvPrinter == null) {
      openValidationCsvPrinter();
    }

    for (ValidationResultElement vre : currentResult.getResults()) {
      validationCsvPrinter.printRecord(currentResult.getId(), currentResult.getEvaluatorKey(),
        currentResult.getEvaluationContext(), currentResult.getEvaluationContextDetails(),
        ValidatorConfig.getLocalizedString(vre.getType().getDescriptionKey()), vre.getResult(), vre.getExplanation());
    }
  }


  @Override
  protected void write(AggregationResult<?> currentResult) throws IOException {
    if (validationResultFilePath == null) {
      return;
    }

    if (aggregationCsvPrinter == null) {
      openAggregationCsvPrinter();
    }

    aggregationCsvPrinter.printRecord(currentResult.getId(), currentResult.getEvaluatorKey(),
      currentResult.getEvaluationContext(), currentResult.getResult());
  }


  @Override
  protected void write(EvaluationResult evaluationResult) throws IOException {

  }

}
