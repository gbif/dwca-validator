package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;

/**
 * ValidationCriterion represents a criterion that can produce validation result(s).
 * 
 * @author cgendreau
 */
public interface ValidationCriterion {

  String getCriterionKey();

  Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext);

  /**
   * This function use a ResultAccumulator to avoid returning a massive object in case all records failed.
   * 
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException;

}
