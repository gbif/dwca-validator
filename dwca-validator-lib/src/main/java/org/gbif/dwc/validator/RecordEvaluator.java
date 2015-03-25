package org.gbif.dwc.validator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ResultAccumulator;

import com.google.common.base.Optional;

/**
 * RecordEvaluator represents an evaluation that can produce result(s) based on Record.
 *
 * @author cgendreau
 */
public interface RecordEvaluator<T extends EvaluationResult> {

  Optional<T> handleRecord(Record record, EvaluationContext evaluationContext);

  /**
   * This function accepts a ResultAccumulator to avoid returning a massive object in case all records failed.
   *
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException;

}
