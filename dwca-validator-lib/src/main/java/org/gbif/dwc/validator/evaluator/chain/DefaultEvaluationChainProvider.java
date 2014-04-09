package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.validator.evaluator.impl.UniquenessEvaluator;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator;

/**
 * Default implementation of EvaluationChainProviderIF using the default builder.
 * 
 * @author cgendreau
 */
public class DefaultEvaluationChainProvider implements EvaluationChainProviderIF {

  @Override
  public ChainableRecordEvaluator getCoreChain() {
    return DefaultChainableRecordEvaluatorBuilder.create(new ValueEvaluator()).linkTo(new UniquenessEvaluator())
      .build();
  }

}
