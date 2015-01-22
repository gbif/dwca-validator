package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriteriaBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.InvalidCharacterCriteriaConfiguration;

import com.google.common.base.CharMatcher;

/**
 * Builder for InvalidCharacterEvaluationRule object.
 * 
 * @author cgendreau
 */
@RecordCriteriaBuilderKey("invalidCharacterCriteria")
public class InvalidCharacterCriteriaBuilder implements RecordCriteriaBuilder {

  // replacement char \uFFFD
  public static final char REPLACEMENT_CHAR = 65533;

  // default rule is no invisible character except space
  private static CharMatcher DEFAULT_CHAR_MATCHER = CharMatcher.INVISIBLE.and(CharMatcher.isNot(' '));

  private final InvalidCharacterCriteriaConfiguration configuration;

  /**
   * Creates a default InvalidCharacterEvaluationRuleBuilder that rejects any invisible characters except space.
   * 
   * @return
   */
  public static InvalidCharacterCriteriaBuilder builder() {
    return new InvalidCharacterCriteriaBuilder();
  }

  /**
   * Private constructor, use builder() method.
   */
  private InvalidCharacterCriteriaBuilder() {
    this.configuration = new InvalidCharacterCriteriaConfiguration();
  }

  public InvalidCharacterCriteriaBuilder(InvalidCharacterCriteriaConfiguration configuration) {
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
  public InvalidCharacterCriteriaBuilder allowFormattingWhiteSpace() {
    configuration.setAllowFormattingWhiteSpace(true);
    return this;
  }

  /**
   * Reject the replacement character (\uFFFD) often seen with encoding issue.
   * 
   * @return
   */
  public InvalidCharacterCriteriaBuilder rejectReplacementChar() {
    configuration.setRejectReplacementChar(true);
    return this;
  }

  public InvalidCharacterCriteriaBuilder onTerm(Term term) {
    configuration.setTerm(term);
    return this;
  }

  /**
   * Build an immutable InvalidCharacterEvaluationRule instance
   * 
   * @return immutable InvalidCharacterEvaluationRule
   */
  @Override
  public RecordCriteria build() {
    configuration.setCharMatcher(toCharMatcher());
    return new InvalidCharacterCriteria(configuration);
  }

}
