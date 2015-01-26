package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.TransformationBasedCriteriaConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.transformation.ValueTransformation;
import org.gbif.dwc.validator.transformation.ValueTransformationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * Criteria that only validates that the registered transformation(s) can be applied.
 * 
 * @author cgendreau
 */
@RecordCriterionKey(key = "transformationBasedCriteria")
class TransformationBasedCriteria implements RecordCriterionIF {

  private final String key = TransformationBasedCriteria.class.getAnnotation(RecordCriterionKey.class).key();

  private final String rowTypeRestriction;
  private final Result level;

  private final List<ValueTransformation<?>> transformations;

  TransformationBasedCriteria(TransformationBasedCriteriaConfiguration configuration) {
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.level = configuration.getLevel();
    this.transformations = Collections.unmodifiableList(configuration.getTransformations());
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

    ValueTransformationResult<?> parsingResult = null;
    for (ValueTransformation<?> currParser : transformations) {
      parsingResult = currParser.transform(record);
      if (parsingResult.isNotTransformed()) {
        elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
          parsingResult.getExplanation()));
      }
    }
    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType(), elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType()));
  }

}
