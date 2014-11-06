package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;


/**
 * Interface defining a RecordEvaluator builder.
 * Builders are defined as inner class in RecordEvaluatorIF implementations.
 * 
 * @author cgendreau
 */
@RecordEvaluatorBuilderKey
public interface RecordEvaluatorBuilder {


  /**
   * Build a concrete instance of RecordEvaluator.
   * 
   * @return
   * @throws IllegalStateException
   */
  RecordEvaluator build() throws IllegalStateException;
}
