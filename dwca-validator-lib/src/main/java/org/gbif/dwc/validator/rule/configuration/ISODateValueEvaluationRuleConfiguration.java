package org.gbif.dwc.validator.rule.configuration;

import org.gbif.dwc.validator.rule.annotation.EvaluationRuleConfigurationKey;

/**
 * Container object holding ISODateValueEvaluationRule configurations.
 * 
 * @author cgendreau
 */
@EvaluationRuleConfigurationKey
public class ISODateValueEvaluationRuleConfiguration {

  private boolean allowPartialDate = false;
  private boolean allowMissingLeadingZeros = false;

  public boolean isAllowMissingLeadingZeros() {
    return allowMissingLeadingZeros;
  }

  public boolean isAllowPartialDate() {
    return allowPartialDate;
  }

  public void setAllowMissingLeadingZeros(boolean allowMissingLeadingZeros) {
    this.allowMissingLeadingZeros = allowMissingLeadingZeros;
  }

  public void setAllowPartialDate(boolean allowPartialDate) {
    this.allowPartialDate = allowPartialDate;
  }
}
