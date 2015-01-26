package org.gbif.dwc.validator.result.accumulator.csv;

import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import org.apache.commons.lang3.StringUtils;

/**
 * ResultAccumulator implementation saving results to CSV file(s).
 * 
 * @author cgendreau
 */
public class CSVResultAccumulator implements ResultAccumulator {

  protected static final int DEFAULT_THRESHOLD = 1000;

  private CSVValidationResultAccumulator csvValidationResultAccumulator;
  private CSVAggregationResultAccumulator csvAggregationResultAccumulator;

  public CSVResultAccumulator(String validationResultFilePath) {
    this(validationResultFilePath, null);
  }

  public CSVResultAccumulator(String validationResultFilePath, String aggregationResultFilePath) {
    if (StringUtils.isNotBlank(validationResultFilePath)) {
      csvValidationResultAccumulator = new CSVValidationResultAccumulator(validationResultFilePath);
    }

    if (StringUtils.isNotBlank(aggregationResultFilePath)) {
      csvAggregationResultAccumulator = new CSVAggregationResultAccumulator(aggregationResultFilePath);
    }
  }

  @Override
  public boolean accumulate(ValidationResult result) throws ResultAccumulationException {
    // Do not record passed result
    if (result.passed()) {
      return true;
    }
    if (csvValidationResultAccumulator == null) {
      throw new ResultAccumulationException("This ResultAccumulator was not configured to record ValidationResult");
    }
    return csvValidationResultAccumulator.accumulate(result);
  }

  @Override
  public boolean accumulate(AggregationResult<?> result) throws ResultAccumulationException {
    if (csvAggregationResultAccumulator == null) {
      throw new ResultAccumulationException("This ResultAccumulator was not configured to record AggregationResult");
    }
    return csvAggregationResultAccumulator.accumulate(result);
  }

  @Override
  public void close() throws ResultAccumulationException {
    if (csvValidationResultAccumulator != null) {
      csvValidationResultAccumulator.close();
    }
    if (csvAggregationResultAccumulator != null) {
      csvAggregationResultAccumulator.close();
    }
  }

  @Override
  public int getValidationResultCount() {
    if (csvValidationResultAccumulator != null) {
      return csvValidationResultAccumulator.getCount();
    }
    return 0;
  }

  @Override
  public int getAggregationResultCount() {
    if (csvAggregationResultAccumulator != null) {
      return csvAggregationResultAccumulator.getCount();
    }
    return 0;
  }

}
