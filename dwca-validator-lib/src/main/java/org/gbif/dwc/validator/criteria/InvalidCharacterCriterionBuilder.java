package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.InvalidCharacterCriterionConfiguration;

import com.google.common.base.CharMatcher;

/**
 * Builder for InvalidCharacterCriterion object.
 * 
 * @author cgendreau
 */
@RecordCriterionBuilderKey("invalidCharacterCriterion")
public class InvalidCharacterCriterionBuilder implements RecordCriteriaBuilder {

  // replacement char \uFFFD
  public static final char REPLACEMENT_CHAR = 65533;

  // default rule is no invisible character except space
  private static CharMatcher DEFAULT_CHAR_MATCHER = CharMatcher.INVISIBLE.and(CharMatcher.isNot(' '));

  private final InvalidCharacterCriterionConfiguration configuration;

  /**
   * Creates a default InvalidCharacterEvaluationRuleBuilder that rejects any invisible characters except space.
   * 
   * @return
   */
  public static InvalidCharacterCriterionBuilder builder() {
    return new InvalidCharacterCriterionBuilder();
  }

  /**
   * Private constructor, use builder() method.
   */
  private InvalidCharacterCriterionBuilder() {
    this.configuration = new InvalidCharacterCriterionConfiguration();
  }

  public InvalidCharacterCriterionBuilder(InvalidCharacterCriterionConfiguration configuration) {
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
  public InvalidCharacterCriterionBuilder allowFormattingWhiteSpace() {
    configuration.setAllowFormattingWhiteSpace(true);
    return this;
  }

  /**
   * Reject the replacement character (\uFFFD) often seen with encoding issue.
   * 
   * @return
   */
  public InvalidCharacterCriterionBuilder rejectReplacementChar() {
    configuration.setRejectReplacementChar(true);
    return this;
  }

  public InvalidCharacterCriterionBuilder onTerm(Term term) {
    configuration.setTerm(term);
    return this;
  }

  /**
   * Build an immutable InvalidCharacterCriterion instance
   * 
   * @return immutable InvalidCharacterCriterion
   */
  @Override
  public RecordCriteria build() {
    configuration.setCharMatcher(toCharMatcher());
    return new InvalidCharacterCriterion(configuration);
  }

}
