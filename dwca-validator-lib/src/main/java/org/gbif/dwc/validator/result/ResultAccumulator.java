package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;
import org.gbif.dwc.validator.result.validation.ValidationResult;

/**
 * Accumulate results from various validations.
 * Implementations must the thread-safe after initialization.
 * In other words, once created, the object can be shared among different threads to
 * accumulate results.
 * 
 * @author cgendreau
 */
public interface ResultAccumulator {


  // boolean accumulate(EvaluationResult result) throws ResultAccumulationException;

  /**
   * Append ValidationResult to current results.
   * 
   * @param validation result
   * @return
   * @throws ResultAccumulationException
   */
  boolean accumulate(ValidationResult result) throws ResultAccumulationException;

  /**
   * Append AggregationResult to current results.
   * 
   * @param aggregation result
   * @return
   * @throws ResultAccumulationException
   */
  boolean accumulate(AggregationResult<?> result) throws ResultAccumulationException;


  /**
   * Close the accumulator and its underlying structure.
   */
  void close() throws ResultAccumulationException;

  /**
   * Get the total of ValidationResult accumulated by this accumulator.
   * 
   * @return count
   */
  int getValidationResultCount();

  /**
   * Get the total of AggregationResult accumulated by this accumulator.
   * 
   * @return count
   */
  int getAggregationResultCount();

}
