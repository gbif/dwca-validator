package org.gbif.dwc.validator.evaluator;


/**
 * Interface defining a RecordEvaluator builder.
 * Builders are defined as inner class in RecordEvaluatorIF implementations.
 * 
 * @author cgendreau
 */
public interface RecordEvaluatorBuilder {

  /**
   * Build a concrete instance of RecordEvaluatorIF.
   * 
   * @return
   */
  RecordEvaluator build();
}
