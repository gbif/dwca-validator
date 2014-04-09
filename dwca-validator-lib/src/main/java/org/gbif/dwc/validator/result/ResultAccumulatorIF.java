package org.gbif.dwc.validator.result;

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
   * Append result to current results.
   * 
   * @param result
   * @return result were successfully appended
   */
  public boolean accumulate(ValidationResult result);

  /**
   * Close the accumulator and its underlying structure.
   */
  public void close();

  /**
   * Get the total of result accumulated by this accumulator.
   * 
   * @return count
   */
  public int getCount();

}
