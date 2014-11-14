package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.EvaluationRuleBuilder;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleBuilderKey;
import org.gbif.dwc.validator.rule.configuration.InvalidCharacterEvaluationRuleConfiguration;

import com.google.common.base.CharMatcher;

/**
 * Builder for InvalidCharacterEvaluationRule object.
 * 
 * @author cgendreau
 */
@EvaluationRuleBuilderKey("invalidCharacterEvaluationRule")
public class InvalidCharacterEvaluationRuleBuilder implements EvaluationRuleBuilder {

  // replacement char \uFFFD
  public static final char REPLACEMENT_CHAR = 65533;

  // default rule is no invisible character except space
  private static CharMatcher DEFAULT_CHAR_MATCHER = CharMatcher.INVISIBLE.and(CharMatcher.isNot(' '));

  private final InvalidCharacterEvaluationRuleConfiguration configuration;

  /**
   * Creates a default InvalidCharacterEvaluationRuleBuilder that rejects any invisible characters except space.
   * 
   * @return
   */
  public static InvalidCharacterEvaluationRuleBuilder builder() {
    return new InvalidCharacterEvaluationRuleBuilder();
  }

  /**
   * Private constructor, use builder() method.
   */
  private InvalidCharacterEvaluationRuleBuilder() {
    this.configuration = new InvalidCharacterEvaluationRuleConfiguration();
  }

  public InvalidCharacterEvaluationRuleBuilder(InvalidCharacterEvaluationRuleConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Create a CharMatcher object based on Configuration.
   * 
   * @param configuration
   * @return
   */
  private CharMatcher toCharMatcher() {
    CharMatcher charMatcher = DEFAULT_CHAR_MATCHER;

    if (configuration.isAllowFormattingWhiteSpace()) {
      charMatcher = charMatcher.and(CharMatcher.BREAKING_WHITESPACE.negate());
    }

    if (configuration.isRejectReplacementChar()) {
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
   * Reject the replacement character (\uFFFD) often seen with encoding issue.
   * 
   * @return
   */
  public InvalidCharacterEvaluationRuleBuilder rejectReplacementChar() {
    configuration.setRejectReplacementChar(true);
    return this;
  }

  /**
   * Build an immutable InvalidCharacterEvaluationRule instance
   * 
   * @return immutable InvalidCharacterEvaluationRule
   */
  @Override
  public EvaluationRuleIF<String> build() {
    configuration.setCharMatcher(toCharMatcher());
    return new InvalidCharacterEvaluationRule(configuration);
  }

}
