package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

/**
 * Interface defining a RecordEvaluator.
 * 
 * @author cgendreau
 */
public interface RecordEvaluatorIF {

  /**
   * Returns the key to use to uniquely identify the Evaluator implementation.
   */
  public String getKey();

  public void handleEval(Record record, ResultAccumulatorIF resultAccumulator);

  /**
   * Called after rows iteration.
   * 
   * @param resultAccumulator
   */
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator);
}
