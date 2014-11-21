package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.validator.result.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

import java.io.Closeable;

/**
 * Interface defining a RecordEvaluator where it's required to keep a state to perform an evaluation.
 * 
 * @author cgendreau
 */
public interface StatefulRecordEvaluator extends RecordEvaluator, Closeable {

  /**
   * Called after rows iteration.
   * 
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  void handlePostIterate(ResultAccumulatorIF resultAccumulator) throws ResultAccumulationException;

}
