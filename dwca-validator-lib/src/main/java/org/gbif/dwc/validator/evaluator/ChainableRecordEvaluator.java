package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

/**
 * Abstract class for chainable evaluators.
 * 
 * @author cgendreau
 */
public abstract class ChainableRecordEvaluator {

  private ChainableRecordEvaluator nextElement;

  /**
   * Do validation and call nest element in the chain (if there is one).
   * 
   * @param record
   * @param resultAccumulator
   */
  public void doEval(Record record, ResultAccumulatorIF resultAccumulator) {
    handleEval(record, resultAccumulator);
    if (nextElement != null) {
      nextElement.doEval(record, resultAccumulator);
    }
  }

  protected abstract void handleEval(Record record, ResultAccumulatorIF resultAccumulator);

  protected abstract void postIterate();

  void setNextElement(ChainableRecordEvaluator nextElement) {
    this.nextElement = nextElement;
  }
}
