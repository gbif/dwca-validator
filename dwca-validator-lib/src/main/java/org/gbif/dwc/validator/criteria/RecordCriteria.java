package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;

/**
 * Criteria interface for Record level.
 * 
 * @author cgendreau
 */
public interface RecordCriteria {

  String getCriteriaKey();

  /**
   * @param record
   * @param evaluationContext
   * @return returns Optional.absent() when the criteria can not be evaluated (e.g. rowTypeRestriction)
   */
  Optional<ValidationResult> validate(Record record, EvaluationContext evaluationContext);

}
