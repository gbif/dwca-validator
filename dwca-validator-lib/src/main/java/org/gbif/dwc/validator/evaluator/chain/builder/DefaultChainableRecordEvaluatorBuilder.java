package org.gbif.dwc.validator.evaluator.chain.builder;

import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Default ChainableRecordEvaluatorBuilderIF implementation.
 * 
 * @author cgendreau
 */
public class DefaultChainableRecordEvaluatorBuilder implements ChainableRecordEvaluatorBuilderIF {

  private final List<RecordEvaluator> recordEvaluatorList;

  private DefaultChainableRecordEvaluatorBuilder(RecordEvaluator recordEvaluator,
    List<RecordEvaluator> recordEvaluatorList) {
    this.recordEvaluatorList = new ArrayList<RecordEvaluator>(recordEvaluatorList);
    this.recordEvaluatorList.add(recordEvaluator);
  }

  /**
   * Create a builder from the provided head RecordEvaluatorIF
   * 
   * @param head
   * @return
   */
  public static DefaultChainableRecordEvaluatorBuilder create(RecordEvaluator head) {
    return new DefaultChainableRecordEvaluatorBuilder(head, new ArrayList<RecordEvaluator>());
  }

  @Override
  public ChainableRecordEvaluator build() {
    ChainableRecordEvaluator current = null;
    // iterate from last element to the first to be able to provide the next ChainableRecordEvaluator
    for (int i = recordEvaluatorList.size() - 1; i >= 0; i--) {
      current = new ChainableRecordEvaluator(recordEvaluatorList.get(i), current);
    }
    return current;
  }

  @Override
  public ChainableRecordEvaluatorBuilderIF linkTo(RecordEvaluator recordEvaluator) {
    return new DefaultChainableRecordEvaluatorBuilder(recordEvaluator, this.recordEvaluatorList);
  }
}
