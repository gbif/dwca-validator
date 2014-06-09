package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.StatefulRecordEvaluatorIF;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

/**
 * Wrapper class to allow RecordEvaluatorIF to be chainable.
 * The class is immutable but the RecordEvaluatorIF immutability can not be enforced.
 * Some are, some are not and some are almost immutable.
 * 
 * @author cgendreau
 */
public class ChainableRecordEvaluator {

  private final RecordEvaluatorIF recordEvaluator;
  private final ChainableRecordEvaluator nextElement;

  public ChainableRecordEvaluator(RecordEvaluatorIF recordEvaluator, ChainableRecordEvaluator nextElement) {
    this.recordEvaluator = recordEvaluator;
    this.nextElement = nextElement;
  }

  /**
   * Cleanup every StatefulRecordEvaluatorIF in the chain.
   */
  public void cleanup() {
    if (recordEvaluator instanceof StatefulRecordEvaluatorIF) {
      ((StatefulRecordEvaluatorIF) recordEvaluator).cleanup();
    }
    if (nextElement != null) {
      nextElement.cleanup();
    }
  }

  /**
   * Do validation and call next element in the chain (if there is one).
   * 
   * @param record
   * @param resultAccumulator
   */
  public void doEval(Record record, ResultAccumulatorIF resultAccumulator) {
    recordEvaluator.handleEval(record, resultAccumulator);
    if (nextElement != null) {
      nextElement.doEval(record, resultAccumulator);
    }
  }

  /**
   * Do postIterate and call next element in the chain (if there is one).
   * 
   * @param resultAccumulator
   */
  public void postIterate(ResultAccumulatorIF resultAccumulator) {
    recordEvaluator.handlePostIterate(resultAccumulator);
    if (nextElement != null) {
      nextElement.postIterate(resultAccumulator);
    }
  }
}
