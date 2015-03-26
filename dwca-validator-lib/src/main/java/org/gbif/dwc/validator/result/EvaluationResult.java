package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.exception.ResultAccumulationException;


/**
 * Interface of an evaluation result.
 * 
 * @author cgendreau
 */
public interface EvaluationResult {

  String getId();

  EvaluationContext getEvaluationContext();

  /**
   * Allows the concrete result class to call the most specific method on the ResultAccumulator.
   * Also permits composed object to generate more than one EvaluationResult object.
   * 
   * @param visitor
   * @throws ResultAccumulationException
   */
  void accept(ResultAccumulator visitor) throws ResultAccumulationException;

}
