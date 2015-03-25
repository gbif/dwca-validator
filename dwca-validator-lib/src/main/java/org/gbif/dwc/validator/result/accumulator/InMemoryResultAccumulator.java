package org.gbif.dwc.validator.result.accumulator;

import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory ResultAccumulatorIF, maximum number of accumulated results is defined
 * by MAX_RESULT.
 *
 * @author cgendreau
 */
public class InMemoryResultAccumulator implements ResultAccumulator {

  public static final int MAX_RESULT = 100;
  private final List<ValidationResult> validationResultList;
  private final List<AggregationResult<?>> aggregationResultList;

  public InMemoryResultAccumulator() {
    validationResultList = new ArrayList<ValidationResult>();
    aggregationResultList = new ArrayList<AggregationResult<?>>();
  }

  @Override
  public boolean accumulate(ValidationResult result) {
    // Do not record passed result
    if (result.passed()) {
      return true;
    }
    if (validationResultList.size() < MAX_RESULT) {
      return validationResultList.add(result);
    }
    return false;
  }

  @Override
  public boolean accumulate(AggregationResult<?> result) {
    if (aggregationResultList.size() < MAX_RESULT) {
      return aggregationResultList.add(result);
    }
    return false;
  }

  @Override
  public void close() {
    // noop
  }

  @Override
  public int getValidationResultCount() {
    return validationResultList.size();
  }

  @Override
  public int getAggregationResultCount() {
    return aggregationResultList.size();
  }

  public List<ValidationResult> getValidationResultList() {
    return validationResultList;
  }

  public List<AggregationResult<?>> getAggregationResultList() {
    return aggregationResultList;
  }

}
