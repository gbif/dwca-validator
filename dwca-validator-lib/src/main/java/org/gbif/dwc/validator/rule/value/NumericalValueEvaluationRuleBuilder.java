package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.EvaluationRule;
import org.gbif.dwc.validator.rule.configuration.NumericalValueEvaluationRuleConfiguration;

import com.google.common.base.Preconditions;

/**
 * Builder for NumericalValueEvaluationRule object.
 * 
 * @author cgendreau
 */
public class NumericalValueEvaluationRuleBuilder {

  private final NumericalValueEvaluationRuleConfiguration configuration;

  /**
   * Private constructor, use builder() method.
   */
  private NumericalValueEvaluationRuleBuilder() {
    configuration = new NumericalValueEvaluationRuleConfiguration();
  }

  public NumericalValueEvaluationRuleBuilder(NumericalValueEvaluationRuleConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Creates a default NumericalValueEvaluationRuleBuilder.
   * 
   * @return
   */
  public static NumericalValueEvaluationRuleBuilder builder() {
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
  public EvaluationRule<String> build() {
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
