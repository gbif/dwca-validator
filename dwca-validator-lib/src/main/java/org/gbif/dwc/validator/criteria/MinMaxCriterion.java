package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.MinMaxCriterionConfiguration;
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

@RecordCriterionKey(key = "minMaxCriterion")
class MinMaxCriterion implements RecordCriteria {

  private final String key = MinMaxCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final String rowTypeRestriction;
  private final Result level;

  private final ValueTransformation<Number> minValueTransformation;
  private final ValueTransformation<Number> maxValueTransformation;

  MinMaxCriterion(MinMaxCriterionConfiguration configuration) {
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.level = configuration.getLevel();
    this.minValueTransformation = configuration.getMinValueTransformation();
    this.maxValueTransformation = configuration.getMaxValueTransformation();
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

    ValueTransformationResult<Number> minValueParsingResult = minValueTransformation.transform(record);
    ValueTransformationResult<Number> maxValueParsingResult = maxValueTransformation.transform(record);

    // ensure we can extract numbers for those fields
    if (minValueParsingResult.isNotTransformed()) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        minValueParsingResult.getExplanation()));
    }
    if (maxValueParsingResult.isNotTransformed()) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        maxValueParsingResult.getExplanation()));
    }

    Number minValue = minValueParsingResult.getData();
    Number maxValue = maxValueParsingResult.getData();

    if ((minValue != null && maxValue != null)
      && minValueParsingResult.getData().doubleValue() > maxValueParsingResult.getData().doubleValue()) {

      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("criterion.min_max_criterion.min_greater_than_max",
          minValueParsingResult.getOriginalValue(), minValueParsingResult.getTerm(),
          maxValueParsingResult.getOriginalValue(), maxValueParsingResult.getTerm())));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType(), elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType()));
  }
}
