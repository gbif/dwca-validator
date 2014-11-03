package org.gbif.dwc.validator.result.impl;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory ResultAccumulatorIF, maximum number of accumulated results is defined
 * by MAX_RESULT.
 * 
 * @author cgendreau
 */
public class InMemoryResultAccumulator implements ResultAccumulatorIF {

  public static final int MAX_RESULT = 100;
  private final List<ValidationResult> validationResultList;

  public InMemoryResultAccumulator() {
    validationResultList = new ArrayList<ValidationResult>();
  }

  @Override
  public boolean accumulate(ValidationResult result) {
    if (validationResultList.size() < MAX_RESULT) {
      return validationResultList.add(result);
    }
    return false;
  }

  @Override
  public void close() {
    // noop
  }

  @Override
  public int getCount() {
    return validationResultList.size();
  }

  public List<ValidationResult> getValidationResultList() {
    return validationResultList;
  }

}
