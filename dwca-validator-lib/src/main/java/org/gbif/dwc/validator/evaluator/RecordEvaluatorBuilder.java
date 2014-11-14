package org.gbif.dwc.validator.evaluator;

/**
 * Interface defining a RecordEvaluator builder.
 * 
 * @author cgendreau
 */
public interface RecordEvaluatorBuilder {


  /**
   * Build a concrete instance of RecordEvaluator.
   * 
   * @return
   * @throws IllegalStateException
   */
  RecordEvaluator build() throws IllegalStateException;
}
