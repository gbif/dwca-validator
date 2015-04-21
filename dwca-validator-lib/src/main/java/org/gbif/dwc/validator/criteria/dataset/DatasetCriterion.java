package org.gbif.dwc.validator.criteria.dataset;

import org.gbif.dwc.validator.criteria.ValidationCriterion;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwca.record.Record;

import java.io.Closeable;

import com.google.common.base.Optional;

/**
 * A DatasetCriteria record data at the record level and generates result after iteration over all records.
 *
 * @author cgendreau
 */
public abstract class DatasetCriterion implements ValidationCriterion, Closeable {

  /**
   * Always return Optional.absent() but ensures onRecord() is called.
   */
  @Override
  public final Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext) {
    onRecord(record, evaluationContext);
    return Optional.absent();
  }

  // should throw error when failed
  public abstract void onRecord(Record record, EvaluationContext evaluationContext);

}
