package org.gbif.dwc.validator.evaluator.chain;


/**
 * The EvaluationChainProviderIF allows to hide the technique used to link all the chain related dependencies and
 * configurations.
 * 
 * @author cgendreau
 */
public interface EvaluationChainProviderIF {

  /**
   * Return the validation chain that should be used against the core.
   * 
   * @return head of the validation chain
   */
  public ChainableRecordEvaluator getCoreChain();

}
