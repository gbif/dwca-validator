package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationRuleResult;
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

  private EvaluationRuleResult createNonNumericalEvaluationRuleResult(String value) {
    return new EvaluationRuleResult(EvaluationRuleResult.RuleResult.FAILED, ValidatorConfig.getLocalizedString(
      "rule.non_numerical", value));
  }

  private EvaluationRuleResult createNumericalOutOfBoundsEvaluationRuleResult(String value) {
    return new EvaluationRuleResult(EvaluationRuleResult.RuleResult.FAILED, ValidatorConfig.getLocalizedString(
      "rule.numerical_out_of_bounds", value, lowerBound, upperBound));
  }

  /**
   * Include the parsed object in the EvaluationRuleResult object.
   * 
   * @param value
   * @return
   */
  private EvaluationRuleResult createPassedEvaluationRuleResult(Double value) {
    return new EvaluationRuleResult(EvaluationRuleResult.RuleResult.PASSED, "", value);
  }

  @Override
  public EvaluationRuleResult evaluate(String str) {

    if (StringUtils.isBlank(str)) {
      return EvaluationRuleResult.SKIPPED;
    }

    Double value = null;
    try {
      value = Double.parseDouble(str);
      if (lowerBound != null && upperBound != null) {
        if (value.doubleValue() < lowerBound.doubleValue() || value.doubleValue() > upperBound.doubleValue()) {
          return createNumericalOutOfBoundsEvaluationRuleResult(str);
        }
      }
    } catch (NumberFormatException nfEx) {
      return createNonNumericalEvaluationRuleResult(str);
    }
    return createPassedEvaluationRuleResult(value);
  }
}
