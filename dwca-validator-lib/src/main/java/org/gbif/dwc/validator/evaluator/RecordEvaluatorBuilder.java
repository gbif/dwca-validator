package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;


/**
 * Interface defining a RecordEvaluator builder.
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
