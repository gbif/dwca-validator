package org.gbif.dwc.validator.result;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory ResultAccumulatorIF for testing purpose only.
 * 
 * @author cgendreau
 */
public class InMemoryResultAccumulator implements ResultAccumulatorIF {

  private final List<ValidationResult> validationResultsList;

  public InMemoryResultAccumulator() {
    validationResultsList = new ArrayList<ValidationResult>();
  }

  @Override
  public boolean accumulate(ValidationResult result) {
    return validationResultsList.add(result);
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
