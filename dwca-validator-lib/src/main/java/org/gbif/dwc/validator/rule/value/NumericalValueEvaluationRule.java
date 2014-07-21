package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple numerical value evaluation rule.
 * Check if the provided String is a number with optional decimals and optional minus sign.
 * Evaluation is using Double.parseDouble.
 * 
 * @author cgendreau
 */
public class NumericalValueEvaluationRule implements EvaluationRuleIF<String> {

  /**
   * Builder used to customized, if needed, the NumericalValueEvaluationRule.
   * Also ensure usage of immutable object.
   * 
   * @author cgendreau
   */
  public static class NumericalValueEvaluationRuleBuilder {

    private Number lowerBound;
    private Number upperBound;

    private NumericalValueEvaluationRuleBuilder() {
    }

    /**
     * Creates a default NumericalValueEvaluationRuleBuilder.
     * 
     * @return
     */
    public static NumericalValueEvaluationRuleBuilder create() {
      return new NumericalValueEvaluationRuleBuilder();
    }

    /**
     * Set a lower and upper inclusive bounds for the evaluation.
     * 
     * @param lowerBound not null lower inclusive bound
     * @param upperBound not null upper inclusive bound
     * @throws IllegalArgumentException is lowerBound or upperBound is null or if lowerBound is greater than upperBound.
     */
    public NumericalValueEvaluationRuleBuilder boundedBy(Number lowerBound, Number upperBound) {
      if (lowerBound == null || upperBound == null || (lowerBound.doubleValue() > upperBound.doubleValue())) {
        throw new IllegalArgumentException();
      }

      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      return this;
    }

    /**
     * Build an immutable NumericalValueEvaluationRule instance
     * 
     * @return immutable NumericalValueEvaluationRule
     */
    public NumericalValueEvaluationRule build() {
      return new NumericalValueEvaluationRule(lowerBound, upperBound);
    }
  }

  private final Number lowerBound;
  private final Number upperBound;

  /**
   * NumericalValueEvaluationRule are created using the builder NumericalValueEvaluationRuleBuilder.
   * 
   * @param minBound lower bound or null
   * @param maxBound upper bound or null
   */
  private NumericalValueEvaluationRule(Number lowerBound, Number upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  /**
   * Simple alias of NumericalValueEvaluationRule.create() for code readability so we can use
   * NumericalValueEvaluationRule.createRule() instead of
   * NumericalValueEvaluationRule.NumericalValueEvaluationRuleBuilder.create()
   * 
   * @return default NumericalValueEvaluationRule
   */
  public static NumericalValueEvaluationRuleBuilder createRule() {
    return NumericalValueEvaluationRuleBuilder.create();
  }

  private ValidationResultElement createNonNumericalValidationResultElement(String value) {
    return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.WARNING,
      ArchiveValidatorConfig.getLocalizedString("rule.non_numerical", value));
  }

  private ValidationResultElement createNumericalOutOfBoundsValidationResultElement(String value) {
    return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_BOUNDS, Result.WARNING,
      ArchiveValidatorConfig.getLocalizedString("rule.numerical_out_of_bounds", value, lowerBound, upperBound));
  }

  @Override
  public ValidationResultElement evaluate(String str) {
    if (StringUtils.isNotBlank(str)) {
      try {
        Double value = Double.parseDouble(str);
        if (lowerBound != null && upperBound != null) {
          if (value.doubleValue() < lowerBound.doubleValue() || value.doubleValue() > upperBound.doubleValue()) {
            return createNumericalOutOfBoundsValidationResultElement(str);
          }
        }
      } catch (NumberFormatException nfEx) {
        return createNonNumericalValidationResultElement(str);
      }
    }
    return null;
  }
}
