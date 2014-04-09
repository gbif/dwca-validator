package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;

/**
 * The builder lets you link all the RecordEvaluatorIF and then build the ChainableRecordEvaluator chain.
 * 
 * @author cgendreau
 */
public interface ChainableRecordEvaluatorBuilderIF {

  /**
   * Creates a ChainableRecordEvaluator chain from the previously linked RecordEvaluatorIF
   * 
   * @return head of the chain
   */
  public ChainableRecordEvaluator build();

  public ChainableRecordEvaluatorBuilderIF linkTo(RecordEvaluatorIF recordEvaluator);

}
