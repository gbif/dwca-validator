package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.terms.Term;
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
import org.gbif.dwca.record.Record;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@RecordCriterionKey(key = "minMaxCriterion")
class MinMaxCriterion extends RecordCriterion {

  private final String key = MinMaxCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final Term rowTypeRestriction;
  private final Result level;

  private final ValueTransformation<Number> minValueTransformation;
  private final ValueTransformation<Number> maxValueTransformation;
  private final Term minTerm;
  private final Term maxTerm;
  private final boolean enforceTwoTermsUse;

  MinMaxCriterion(MinMaxCriterionConfiguration configuration) {
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.level = configuration.getLevel();
    this.minValueTransformation = configuration.getMinValueTransformation();
    this.maxValueTransformation = configuration.getMaxValueTransformation();
    // we trust the builder
    this.minTerm = minValueTransformation.getTerms().get(0);
    this.maxTerm = maxValueTransformation.getTerms().get(0);
    this.enforceTwoTermsUse = configuration.isEnforceTwoTermsUse();
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

    // TODO we should probably avoid creating list when there is no result
    List<ValidationResultElement> elementList = Lists.newArrayList();

    ValueTransformationResult<Number> minValueParsingResult = minValueTransformation.transform(record);
    ValueTransformationResult<Number> maxValueParsingResult = maxValueTransformation.transform(record);

    // if both values are skipped, skip also the criterion
    if (minValueParsingResult.isSkipped() && maxValueParsingResult.isSkipped()) {
      return Optional.absent();
    }

    // ensure we can extract numbers for those fields if provided
    if (minValueParsingResult.isNotTransformed()) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        minValueParsingResult.getExplanation()));
    }
    if (maxValueParsingResult.isNotTransformed()) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        maxValueParsingResult.getExplanation()));
    }

    // if min or max was skipped and we enforce the use of the 2 terms, add an validation result
    if ((minValueParsingResult.isSkipped() || maxValueParsingResult.isSkipped()) && enforceTwoTermsUse) {
      Term guiltyTerm = minValueParsingResult.isSkipped() ? minTerm : maxTerm;
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("criterion.min_max_criterion.min_or_max_missing", guiltyTerm)));
    }

    Number minValue = minValueParsingResult.getData();
    Number maxValue = maxValueParsingResult.getData();

    if ((minValue != null && maxValue != null)
      && minValueParsingResult.getData().doubleValue() > maxValueParsingResult.getData().doubleValue()) {

      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("criterion.min_max_criterion.min_greater_than_max",
          minValueParsingResult.getOriginalValue(), minTerm, maxValueParsingResult.getOriginalValue(), maxTerm)));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName(),
        elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName()));
  }
}
