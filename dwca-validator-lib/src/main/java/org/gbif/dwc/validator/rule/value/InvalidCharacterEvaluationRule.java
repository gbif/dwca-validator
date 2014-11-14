package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
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

  InvalidCharacterEvaluationRule(InvalidCharacterEvaluationRuleConfiguration configuration) {
    this.charMatcher = configuration.getCharMatcher();
  }

  @Override
  public ValidationResultElement evaluate(String str) {

    if (str == null) {
      return ValidationResultElement.SKIPPED;
    }

    int indexIn = charMatcher.indexIn(str);
    if (indexIn > 0) {
      // Remove invalid character from the error message to avoid display issues (e.g. NULL char)
      return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.WARNING,
        ValidatorConfig.getLocalizedString("rule.invalid_character", charMatcher.removeFrom(str), indexIn));
    }
    return ValidationResultElement.PASSED;
  }
}
