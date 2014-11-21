package org.gbif.dwc.validator.evaluator.aggregation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.evaluator.StatefulRecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.IOException;

import com.google.common.base.Optional;

/**
 * Skeleton class for aggregation example.
 * 
 * @author cgendreau
 */
@RecordEvaluatorKey(key = "distinctCountEvaluator")
class DistinctCountEvaluator implements StatefulRecordEvaluator {

  private final String key = DistinctCountEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleEval(Record record, EvaluationContext evaluationContext) {
    // TODO record values
    return Optional.absent();
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    // TODO add results to resultAccumulator
  }

  @Override
  public void close() throws IOException {
    // TODO
  }

}
