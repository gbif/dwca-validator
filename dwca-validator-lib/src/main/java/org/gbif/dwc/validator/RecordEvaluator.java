package org.gbif.dwc.validator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ResultAccumulator;

import com.google.common.base.Optional;

/**
 * RecordEvaluator represents an evaluation that can produce result(s) based on {@link Record}
 *
 * @author cgendreau
 */
public interface RecordEvaluator<T extends EvaluationResult> {

  /**
   * Handle a {@link Record} for a specific {@link EvaluationContext}.
   *
   * @param record
   * @param evaluationContext
   * @return
   */
  Optional<T> handleRecord(Record record, EvaluationContext evaluationContext);

  /**
   * This function accepts a ResultAccumulator to avoid returning a massive object in case all records failed.
   *
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException;

}
