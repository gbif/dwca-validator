package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import com.google.common.base.CharMatcher;

/**
 * Rule use to ensure a String does not contain invalid characters.
 * 
 * @author cgendreau
 */
public class InvalidCharacterEvaluationRule implements EvaluationRuleIF<String> {

  /**
   * Builder used to customized, if needed, the InvalidCharacterEvaluationRule.
   * 
   * @author cgendreau
   */
  public static class InvalidCharacterEvaluationRuleBuilder {

    private final CharMatcher currentCharMatcher;

    private InvalidCharacterEvaluationRuleBuilder(CharMatcher currentCharMatcher) {
      this.currentCharMatcher = currentCharMatcher;
    }

    /**
     * Creates a default InvalidCharacterEvaluationRuleBuilder that rejects any invisible characters except space.
     * 
     * @return
     */
    public static InvalidCharacterEvaluationRuleBuilder create() {
      return new InvalidCharacterEvaluationRuleBuilder(DEFAULT_CHAR_MATCHER);
    }

    /**
     * Formating whitespace (tab, end-line) will be allowed.
     * 
     * @return
     */
    public InvalidCharacterEvaluationRuleBuilder allowFormattingWhiteSpace() {
      return new InvalidCharacterEvaluationRuleBuilder(currentCharMatcher.and(CharMatcher.BREAKING_WHITESPACE.negate()));
    }

    /**
     * Build an immutable InvalidCharacterEvaluationRule instance
     * 
     * @return immutable InvalidCharacterEvaluationRule
     */
    public InvalidCharacterEvaluationRule build() {
      return new InvalidCharacterEvaluationRule(currentCharMatcher);
    }
  }

  private final CharMatcher charMatcher;

  // default rule is no invisible character, except space.
  private static CharMatcher DEFAULT_CHAR_MATCHER = CharMatcher.INVISIBLE.and(CharMatcher.isNot(' '));

  private InvalidCharacterEvaluationRule(CharMatcher charMatcher) {
    this.charMatcher = charMatcher;
  }

  /**
   * Simple alias of InvalidCharacterEvaluationRuleBuilder.create() for code readability.
   * 
   * @return default InvalidCharacterEvaluationRuleBuilder
   */
  public static InvalidCharacterEvaluationRuleBuilder createRule() {
    return InvalidCharacterEvaluationRuleBuilder.create();
  }

  @Override
  public ValidationResultElement evaluate(String str) {
    int indexIn = charMatcher.indexIn(str);
    if (indexIn > 0) {
      return new ValidationResultElement(ContentValidationType.RECORD_CONTENT, Result.WARNING, str
        + "contains invalid character at position " + indexIn);
    }
    return null;
  }
}
