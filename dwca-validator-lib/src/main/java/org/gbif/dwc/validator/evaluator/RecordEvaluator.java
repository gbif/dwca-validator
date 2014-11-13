package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;

/**
 * Interface defining a RecordEvaluator.
 * 
 * @author cgendreau
 */
public interface RecordEvaluator {

  /**
   * Returns the key to use to uniquely identify the Evaluator implementation.
   */
  String getKey();

  /**
   * Handle evaluation of a specific Record.
   * 
   * @param record
   * @param evaluationContext core or extension
   * @return ValidationResult if one is produced otherwise Optional.absent()
   */
  Optional<ValidationResult> handleEval(Record record, EvaluationContext evaluationContext);

}
