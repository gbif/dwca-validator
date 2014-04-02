package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.validator.evaluator.impl.UniquenessEvaluator;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator;

/**
 * Build evaluator chains.
 * TODO allow concrete ChainableRecordEvaluator customizations
 * TODO allow DI to set concrete ChainableRecordEvaluator
 * 
 * @author cgendreau
 */
public class EvaluatorChainBuilder {


  /**
   * Build a validation chain for DarwinCore core component.
   * 
   * @return
   */
  public static ChainableRecordEvaluator buildCoreChain() {
    return linkEvaluators(new ValueEvaluator(), new UniquenessEvaluator());
  }

  /**
   * Links ChainableRecordEvaluator together.
   * 
   * @param evaluators
   * @return head of the chain
   */
  private static ChainableRecordEvaluator linkEvaluators(ChainableRecordEvaluator... evaluators) {
    ChainableRecordEvaluator previousEvaluator = null;
    for (ChainableRecordEvaluator currEvaluator : evaluators) {
      if (previousEvaluator != null) {
        previousEvaluator.setNextElement(currEvaluator);
      }
      previousEvaluator = currEvaluator;
    }
    return evaluators[0];
  }
}
