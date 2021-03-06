package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.BoundCriterionConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.transformation.ValueTransformation;
import org.gbif.dwc.validator.transformation.ValueTransformationResult;
import org.gbif.dwca.record.Record;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

@RecordCriterionKey(key = "boundCriterion")
class BoundCriterion extends RecordCriterion {

  private final String key = BoundCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final Term rowTypeRestriction;
  private final Result level;

  private final Number lowerBound;
  private final Number upperBound;
  private final ValueTransformation<Number> valueTransformation;
  private final Term term;

  /**
   * Package protected constructor, use BoundCriteriaBuilder.
   *
   * @param boundCriteriaConfiguration
   */
  BoundCriterion(BoundCriterionConfiguration boundCriteriaConfiguration) {
    this.level = boundCriteriaConfiguration.getLevel();
    this.rowTypeRestriction = boundCriteriaConfiguration.getRowTypeRestriction();
    this.lowerBound = boundCriteriaConfiguration.getLowerBound();
    this.upperBound = boundCriteriaConfiguration.getUpperBound();
    this.valueTransformation = boundCriteriaConfiguration.getValueTransformation();
    // we trust the builder
    this.term = valueTransformation.getTerms().get(0);
  }

  @Override
  public String getCriterionKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext) {

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (rowTypeRestriction != null && !rowTypeRestriction.equals(record.rowType())) {
      return Optional.absent();
    }

    List<ValidationResultElement> elementList = new ArrayList<ValidationResultElement>();
    ValueTransformationResult<Number> parsingResult = valueTransformation.transform(record);

    // ensure we can extract numbers for those fields
    if (parsingResult.isNotTransformed()) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level, parsingResult
        .getExplanation()));
    }

    Number parsedValue = parsingResult.getData();

    if (parsedValue != null
      && (parsedValue.doubleValue() < lowerBound.doubleValue() || parsedValue.doubleValue() > upperBound.doubleValue())) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("criterion.bound_criterion.out_of_bounds", parsedValue, lowerBound,
          upperBound, term)));
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName(),
        elementList));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName(),
        elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName()));
  }
}
