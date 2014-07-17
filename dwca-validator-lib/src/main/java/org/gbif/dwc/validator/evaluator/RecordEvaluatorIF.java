package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

/**
 * Interface defining a RecordEvaluator.
 * 
 * @author cgendreau
 */
public interface RecordEvaluatorIF {

  void handleEval(Record record, ResultAccumulatorIF resultAccumulator);

  /**
   * Called after rows iteration.
   * 
   * @param resultAccumulator
   */
  void handlePostIterate(ResultAccumulatorIF resultAccumulator);
}
