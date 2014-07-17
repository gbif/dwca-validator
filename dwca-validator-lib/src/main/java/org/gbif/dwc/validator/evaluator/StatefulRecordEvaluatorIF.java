package org.gbif.dwc.validator.evaluator;

/**
 * Interface defining a RecordEvaluator where it's required to keep a state to perform an evaluation.
 * 
 * @author cgendreau
 */
public interface StatefulRecordEvaluatorIF extends RecordEvaluatorIF {

  /**
   * Free all resources
   */
  public void cleanup();

}
