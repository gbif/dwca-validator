package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.RecordCriterion;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.BoundCriterionConfiguration;
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
import org.apache.commons.lang3.StringUtils;

@RecordCriterionKey(key = "boundCriterion")
class BoundCriterion implements RecordCriterion {

  private final String key = BoundCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final String rowTypeRestriction;
  private final Result level;

  private final Number lowerBound;
  private final Number upperBound;
  private final ValueTransformation<Number> valueTransformation;

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
  }

  @Override
  public String getCriteriaKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> validate(Record record, EvaluationContext evaluationContext) {

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(record.rowType())) {
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
          upperBound, parsingResult.getTerm())));
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType(), elementList));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType(), elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType()));
  }
}
