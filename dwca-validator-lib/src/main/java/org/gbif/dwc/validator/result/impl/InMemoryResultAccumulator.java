package org.gbif.dwc.validator.result.impl;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory ResultAccumulatorIF, maximum number of accumulated results is defined
 * by MAX_RESULT.
 * 
 * @author cgendreau
 */
public class InMemoryResultAccumulator implements ResultAccumulatorIF {

  private static final int MAX_RESULT = 100;
  private final List<ValidationResult> validationResultsList;

  public InMemoryResultAccumulator() {
    validationResultsList = new ArrayList<ValidationResult>();
  }

  @Override
  public boolean accumulate(ValidationResult result) {
    if (validationResultsList.size() < MAX_RESULT) {
      return validationResultsList.add(result);
    }
    return false;
  }

  @Override
  public void close() {
    // noop
  }

  @Override
  public int getCount() {
    return validationResultsList.size();
  }

  public List<ValidationResult> getValidationResultsList() {
    return validationResultsList;
  }

}
