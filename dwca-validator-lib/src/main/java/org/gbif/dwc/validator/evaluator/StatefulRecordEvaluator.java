package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.validator.exception.EvaluationException;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulator;

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
   * @throws EvaluationException
   * @throws ResultAccumulationException
   */
  void handlePostIterate(ResultAccumulator resultAccumulator) throws EvaluationException, ResultAccumulationException;

}
