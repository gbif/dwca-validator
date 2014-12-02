package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationRuleResult;
import org.gbif.dwc.validator.rule.EvaluationRule;
import org.gbif.dwc.validator.rule.configuration.InvalidCharacterEvaluationRuleConfiguration;

import com.google.common.base.CharMatcher;

/**
 * Rule used to ensure a String does not contain invalid characters.
 * Get instance using the builder InvalidCharacterEvaluationRuleBuilder
 * InvalidCharacterEvaluationRule objects are immutable.
 * 
 * @author cgendreau
 */
class InvalidCharacterEvaluationRule implements EvaluationRule<String> {

  private final CharMatcher charMatcher;
  private final Integer maximumLength;

  InvalidCharacterEvaluationRule(InvalidCharacterEvaluationRuleConfiguration configuration) {
    this.charMatcher = configuration.getCharMatcher();
    this.maximumLength = configuration.getMaximumLength();
  }

  @Override
  public EvaluationRuleResult evaluate(String str) {

    if (str == null) {
      return EvaluationRuleResult.SKIPPED;
    }

    int indexIn = charMatcher.indexIn(str);
    if (indexIn > 0) {
      // TODO when moving to Java 7 use Character.getName(int codePoint)
      int charValue = str.charAt(indexIn);
      // Remove invalid character from the error message to avoid display issues (e.g. NULL char)
      return new EvaluationRuleResult(EvaluationRuleResult.RuleResult.FAILED, ValidatorConfig.getLocalizedString(
        "rule.invalid_character", charMatcher.removeFrom(str), indexIn, charValue));
    }

    if (maximumLength != null && str.length() > maximumLength.intValue()) {
      return new EvaluationRuleResult(EvaluationRuleResult.RuleResult.FAILED, ValidatorConfig.getLocalizedString(
        "rule.maximum_length_exceeded", maximumLength, str));
    }
    return EvaluationRuleResult.PASSED;
  }
}
