package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import org.apache.commons.lang3.StringUtils;

/**
 * Rule used to ensure a String is not blank (not null and not empty).
 * TODO Possible options:
 * -Maybe this rule could allow to be inverted to ensure a value is blank.
 * -Flag all non-alphanumerical as blank e.g. the string "" when a column is quoted twice.
 * 
 * @author cgendreau
 */
public class BlankValueEvaluationRule implements EvaluationRuleIF<String> {

  /**
   * Builder for BlankValueEvaluationRuleBuilder object.
   * Also ensure usage of immutable object.
   * 
   * @author cgendreau
   */
  public static class BlankValueEvaluationRuleBuilder {

    private BlankValueEvaluationRuleBuilder() {
    }

    /**
     * Creates a default BlankValueEvaluationRuleBuilder that rejects all blank values.
     * 
     * @return
     */
    public static BlankValueEvaluationRuleBuilder create() {
      return new BlankValueEvaluationRuleBuilder();
    }

    /**
     * Build an immutable BlankValueEvaluationRule instance
     * 
     * @return immutable BlankValueEvaluationRule
     */
    public BlankValueEvaluationRule build() {
      return new BlankValueEvaluationRule();
    }
  }

  @Override
  public ValidationResultElement evaluate(String str) {

    if (StringUtils.isBlank(str)) {
      return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.ERROR,
        ArchiveValidatorConfig.getLocalizedString("rule.blank_value"));
    }
    return ValidationResultElement.PASSED;
  }

}
