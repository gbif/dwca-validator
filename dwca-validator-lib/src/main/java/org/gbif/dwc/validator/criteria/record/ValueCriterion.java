package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.ValueCriterionConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.transformation.ValueTransformation;
import org.gbif.dwc.validator.transformation.ValueTransformationResult;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Criterion used to check the value of a term.
 * The operation used is provided using a predicate.
 *
 * @author cgendreau
 * @param <T>
 */
@RecordCriterionKey(key = "valueCriterion")
class ValueCriterion<T> extends RecordCriterion {

  private final String key = ValueCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final Term term;
  private final Result level;

  private final ValueTransformation<T> transformation;
  private final Predicate<T> predicate;

  ValueCriterion(ValueCriterionConfiguration<T> configuration) {
    this.level = configuration.getLevel();
    this.term = configuration.getTerm();
    this.predicate = configuration.getPredicate();
    this.transformation = configuration.getTransformation();
  }

  @Override
  public String getCriterionKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext) {

    List<ValidationResultElement> elementList = new ArrayList<ValidationResultElement>();
    ValueTransformationResult<T> valueToCompareResult = transformation.transform(record);

    if (valueToCompareResult.isTransformed()) {
      if (!predicate.apply(valueToCompareResult.getData())) {
        elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
          ValidatorConfig.getLocalizedString("criterion.value_criterion.not_valid", valueToCompareResult.getData(),
            term)));
      }
    } else {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        valueToCompareResult.getExplanation()));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType(), elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType()));
  }

}
