package org.gbif.dwc.validator.result;



/**
 * Interface of an evaluation result.
 * 
 * @author cgendreau
 */
public interface EvaluationResult {

  String getId();

  EvaluationContext getEvaluationContext();

  // void accept(ResultAccumulator visitor) throws ResultAccumulationException;

}
