package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.validator.RecordEvaluator;
import org.gbif.dwc.validator.result.validation.ValidationResult;

/**
 * ValidationCriterion represents a criterion that can produce {@link ValidationResult} based on records.
 *
 * @author cgendreau
 */
public interface ValidationCriterion extends RecordEvaluator<ValidationResult> {

  String getCriterionKey();

}
