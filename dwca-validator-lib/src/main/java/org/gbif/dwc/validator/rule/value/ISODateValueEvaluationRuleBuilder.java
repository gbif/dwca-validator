package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.EvaluationRuleBuilder;
import org.gbif.dwc.validator.rule.EvaluationRule;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleBuilderKey;
import org.gbif.dwc.validator.rule.configuration.ISODateValueEvaluationRuleConfiguration;

/**
 * Builder for ISODateValueEvaluationRule object.
 * 
 * @author cgendreau
 */
@EvaluationRuleBuilderKey("ISODateValueEvaluationRule")
public class ISODateValueEvaluationRuleBuilder implements EvaluationRuleBuilder {

  private final ISODateValueEvaluationRuleConfiguration configuration;

  /**
   * Private constructor, use builder() method.
   */
  private ISODateValueEvaluationRuleBuilder() {
    this.configuration = new ISODateValueEvaluationRuleConfiguration();
  }

  public ISODateValueEvaluationRuleBuilder(ISODateValueEvaluationRuleConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Creates a default ISODateValueEvaluationRuleBuilder.
   * 
   * @return
   */
  public static ISODateValueEvaluationRuleBuilder builder() {
    return new ISODateValueEvaluationRuleBuilder();
  }

  /**
   * Allow ISO dates with no leading zeros(e.g. 2014-9-4).
   * 
   * @return
   */
  public ISODateValueEvaluationRuleBuilder allowMissingLeadingZeros() {
    configuration.setAllowMissingLeadingZeros(true);
    return this;
  }

  /**
   * Allow partial ISO dates (e.g. 2014 or 2014-08).
   * 
   * @return
   */
  public ISODateValueEvaluationRuleBuilder allowPartialDate() {
    configuration.setAllowPartialDate(true);
    return this;
  }

  /**
   * Build an immutable ISODateValueEvaluationRule instance
   * 
   * @return immutable ISODateValueEvaluationRule
   */
  @Override
  public EvaluationRule<String> build() {
    return new ISODateValueEvaluationRule(configuration);
  }
}
