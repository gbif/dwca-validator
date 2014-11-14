package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.type.UndefinedValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRule;
import org.gbif.dwc.validator.rule.configuration.NumericalValueEvaluationRuleConfiguration;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple numerical value evaluation rule that checks if the provided String is a number.
 * Optionally, bounds can also be checked.
 * Evaluation is using Double.parseDouble.
 * NumericalValueEvaluationRule objects are immutable.
 * 
 * @author cgendreau
 */
class NumericalValueEvaluationRule implements EvaluationRule<String> {

  private final Number lowerBound;
  private final Number upperBound;

  /**
   * NumericalValueEvaluationRule are created using the builder NumericalValueEvaluationRuleBuilder.
   * 
   * @param minBound lower bound or null
   * @param maxBound upper bound or null
   */
  NumericalValueEvaluationRule(NumericalValueEvaluationRuleConfiguration configuration) {
    this.lowerBound = configuration.getLowerBound();
    this.upperBound = configuration.getUpperBound();
  }

  private ValidationResultElement createNonNumericalValidationResultElement(String value) {
    return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.WARNING,
      ValidatorConfig.getLocalizedString("rule.non_numerical", value));
  }

  private ValidationResultElement createNumericalOutOfBoundsValidationResultElement(String value) {
    return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_BOUNDS, Result.WARNING,
      ValidatorConfig.getLocalizedString("rule.numerical_out_of_bounds", value, lowerBound, upperBound));
  }

  /**
   * The returned ValidationTypeIF will be UNDEFINED.
   * The idea is to avoid returning multiple ValidationResultElement on success.
   * This should be interpreted as all included ValidationTypeIF passed.
   * 
   * @param value
   * @return
   */
  private ValidationResultElement createSuccessValidationResultElement(Double value) {
    return new ValidationResultElement(UndefinedValidationType.UNDEFINED, Result.PASSED, "", value);
  }

  @Override
  public ValidationResultElement evaluate(String str) {

    if (StringUtils.isBlank(str)) {
      return ValidationResultElement.SKIPPED;
    }

    Double value = null;
    try {
      value = Double.parseDouble(str);
      if (lowerBound != null && upperBound != null) {
        if (value.doubleValue() < lowerBound.doubleValue() || value.doubleValue() > upperBound.doubleValue()) {
          return createNumericalOutOfBoundsValidationResultElement(str);
        }
      }
    } catch (NumberFormatException nfEx) {
      return createNonNumericalValidationResultElement(str);
    }
    return createSuccessValidationResultElement(value);
  }
}
