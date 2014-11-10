package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import com.google.common.base.CharMatcher;

/**
 * Rule used to ensure a String does not contain invalid characters.
 * Get instance using the builder InvalidCharacterEvaluationRuleBuilder
 * 
 * @author cgendreau
 */
public class InvalidCharacterEvaluationRule implements EvaluationRuleIF<String> {

  /**
   * Container object holding InvalidCharacterEvaluationRule configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private boolean allowFormattingWhiteSpace = false;
    private boolean rejectReplacementChar = false;

    public boolean isAllowFormattingWhiteSpace() {
      return allowFormattingWhiteSpace;
    }

    public boolean isRejectReplacementChar() {
      return rejectReplacementChar;
    }

    public void setAllowFormattingWhiteSpace(boolean allowFormattingWhiteSpace) {
      this.allowFormattingWhiteSpace = allowFormattingWhiteSpace;
    }

    public void setRejectReplacementChar(boolean rejectReplacementChar) {
      this.rejectReplacementChar = rejectReplacementChar;
    }
  }

  /**
   * Builder used to customized, if needed, the InvalidCharacterEvaluationRule.
   * 
   * @author cgendreau
   */
  public static class InvalidCharacterEvaluationRuleBuilder {

    private final Configuration configuration = new Configuration();

    /**
     * Creates a default InvalidCharacterEvaluationRuleBuilder that rejects any invisible characters except space.
     * 
     * @return
     */
    public static InvalidCharacterEvaluationRuleBuilder create() {
      return new InvalidCharacterEvaluationRuleBuilder();
    }

    /**
     * Create a CharMatcher object based on Configuration.
     * 
     * @param configuration
     * @return
     */
    private static CharMatcher toCharMatcher(Configuration configuration) {
      CharMatcher charMatcher = DEFAULT_CHAR_MATCHER;

      if (configuration.allowFormattingWhiteSpace) {
        charMatcher = charMatcher.and(CharMatcher.BREAKING_WHITESPACE.negate());
      }

      if (configuration.rejectReplacementChar) {
        charMatcher = charMatcher.or(CharMatcher.is(REPLACEMENT_CHAR));
      }

      return charMatcher;
    }

    /**
     * Formating whitespace (tab, end-line) will be allowed.
     * 
     * @return
     */
    public InvalidCharacterEvaluationRuleBuilder allowFormattingWhiteSpace() {
      configuration.setAllowFormattingWhiteSpace(true);
      return this;
    }

    /**
     * Build an immutable InvalidCharacterEvaluationRule instance
     * 
     * @return immutable InvalidCharacterEvaluationRule
     */
    public InvalidCharacterEvaluationRule build() {
      return new InvalidCharacterEvaluationRule(configuration);
    }

    /**
     * Reject the replacement character (\uFFFD) often seen with encoding issue.
     * 
     * @return
     */
    public InvalidCharacterEvaluationRuleBuilder rejectReplacementChar() {
      configuration.setRejectReplacementChar(true);
      return this;
    }
  }

  // replacement char \uFFFD
  public static final char REPLACEMENT_CHAR = 65533;

  private final CharMatcher charMatcher;

  // default rule is no invisible character except space
  private static CharMatcher DEFAULT_CHAR_MATCHER = CharMatcher.INVISIBLE.and(CharMatcher.isNot(' '));

  /**
   * Build immutable InvalidCharacterEvaluationRule from Configuration.
   * Internally, the builder will be used to convert configurations into CharMatcher.
   * 
   * @param configuration
   */
  public InvalidCharacterEvaluationRule(Configuration configuration) {
    this.charMatcher = InvalidCharacterEvaluationRuleBuilder.toCharMatcher(configuration);
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
