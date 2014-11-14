package org.gbif.dwc.validator.rule.configuration;

import org.gbif.dwc.validator.rule.annotation.EvaluationRuleConfigurationKey;

/**
 * Container object holding NumericalValueEvaluationRule configurations.
 * 
 * @author cgendreau
 */
@EvaluationRuleConfigurationKey
public class NumericalValueEvaluationRuleConfiguration {

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
