package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.type.UndefinedValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import com.google.common.base.Preconditions;
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
   * Container object holding NumericalValueEvaluationRule configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private Number lowerBound;
    private Number upperBound;

    public Number getLowerBound() {
      return lowerBound;
    }

    public Number getUpperBound() {
      return upperBound;
    }

    public void setLowerBound(Number lowerBound) {
      this.lowerBound = lowerBound;
    }

    public void setUpperBound(Number upperBound) {
      this.upperBound = upperBound;
    }
  }

  /**
   * Builder used to customized, if needed, the NumericalValueEvaluationRule.
   * Also ensure usage of immutable object.
   * 
   * @author cgendreau
   */
  public static class NumericalValueEvaluationRuleBuilder {

    private final Configuration configuration;

    private NumericalValueEvaluationRuleBuilder() {
      configuration = new Configuration();
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
     * @param lowerBound lower inclusive bound
     * @param upperBound upper inclusive bound
     */
    public NumericalValueEvaluationRuleBuilder boundedBy(Number lowerBound, Number upperBound) {
      configuration.setLowerBound(lowerBound);
      configuration.setUpperBound(upperBound);
      return this;
    }

    /**
     * Build an immutable NumericalValueEvaluationRule instance.
     * 
     * @return immutable NumericalValueEvaluationRule
     * @throws NullPointerException if the rule is bounded, the lowerBound and upperBound must not be null
     *         IllegalStateException if the rule is bounded, lower bound must not be greater than upperBound.
     */
    public NumericalValueEvaluationRule build() {
      // Preconditions only if we set bounds
      if (configuration.getLowerBound() != null || configuration.getUpperBound() != null) {
        Preconditions.checkNotNull(configuration.getLowerBound());
        Preconditions.checkNotNull(configuration.getUpperBound());
        Preconditions.checkState((configuration.getLowerBound().doubleValue() < configuration.getUpperBound()
          .doubleValue()), "lower and upper bounds are in wrong order");
      }
      return new NumericalValueEvaluationRule(configuration);
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
  private NumericalValueEvaluationRule(Configuration configuration) {
    this.lowerBound = configuration.getLowerBound();
    this.upperBound = configuration.getUpperBound();
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
