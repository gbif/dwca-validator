package org.gbif.dwc.validator.result.accumulator.csv;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.accumulator.AbstractThresholdAccumulatorSupport;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Threshold based accumulator saving AggregationResult to CSV file.
 * 
 * @author cgendreau
 */
class CSVAggregationResultAccumulator extends AbstractThresholdAccumulatorSupport<AggregationResult<?>> {

  private final String aggregationResultFilePath;
  private CSVPrinter aggregationCsvPrinter;

  public CSVAggregationResultAccumulator(String aggregationResultFilePath) {
    this(aggregationResultFilePath, DEFAULT_THRESHOLD);
  }

  public CSVAggregationResultAccumulator(String aggregationResultFilePath, int threshold) {
    super(threshold);
    this.aggregationResultFilePath = aggregationResultFilePath;
  }


  private void openAggregationCsvPrinter() throws IOException {
    aggregationCsvPrinter = new CSVPrinter(new FileWriter(aggregationResultFilePath), CSVFormat.DEFAULT);
    printAggregationResultHeaders();
  }

  private void printAggregationResultHeaders() throws IOException {
    aggregationCsvPrinter.printRecord(ValidatorConfig.getLocalizedString("result.header.id"),
      ValidatorConfig.getLocalizedString("result.header.evaluator"),
      ValidatorConfig.getLocalizedString("result.header.context"),
      ValidatorConfig.getLocalizedString("result.header.result"));
  }

  @Override
  protected void write(AggregationResult<?> result) throws IOException {
    if (aggregationCsvPrinter == null) {
      openAggregationCsvPrinter();
    }

    aggregationCsvPrinter.printRecord(result.getId(), result.getEvaluatorKey(), result.getEvaluationContext(),
      result.getResult());
  }

  @Override
  protected void closeWriter() throws IOException {
    if (aggregationCsvPrinter != null) {
      aggregationCsvPrinter.flush();
      aggregationCsvPrinter.close();
    }
  }

}
