package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.RecordEvaluator;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;

/**
 * ValidationCriterion represents a criterion that can produce {@link ValidationResult} based on records.
 *
 * @author cgendreau
 */
public interface ValidationCriterion extends RecordEvaluator<ValidationResult> {

  String getCriterionKey();

  @Override
  Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext);

}
