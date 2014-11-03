package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.result.validation.ValidationResult;

/**
 * Accumulate results from various validations.
 * Implementations must the thread-safe after initialization.
 * In other words, once created, the object can be shared among different threads to
 * accumulate results.
 * 
 * @author cgendreau
 */
public interface ResultAccumulatorIF {

  /**
   * Append ValidationResult to current results.
   * 
   * @param result
   * @return
   */
  boolean accumulate(ValidationResult result);

  /**
   * Fallback method, append result to current results.
   * 
   * @param result
   * @return result were successfully appended
   */
// boolean accumulate(EvaluationResultIF<?> result);

  /**
   * Close the accumulator and its underlying structure.
   */
  void close();

  /**
   * Get the total of result accumulated by this accumulator.
   * 
   * @return count
   */
  int getCount();

}
