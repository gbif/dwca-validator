package org.gbif.dwc.validator.rule.configuration;

import org.gbif.dwc.validator.rule.annotation.EvaluationRuleConfigurationKey;

import com.google.common.base.CharMatcher;

/**
 * Container object holding InvalidCharacterEvaluationRule configurations.
 * 
 * @author cgendreau
 */
@EvaluationRuleConfigurationKey
public class InvalidCharacterEvaluationRuleConfiguration {

  private boolean allowFormattingWhiteSpace = false;
  private boolean rejectReplacementChar = false;

  private Integer maximumLength;

  private CharMatcher charMatcher;

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

  public Integer getMaximumLength() {
    return maximumLength;
  }

  public void setMaximumLength(Integer maximumLength) {
    this.maximumLength = maximumLength;
  }

  public CharMatcher getCharMatcher() {
    return charMatcher;
  }

  /**
   * CharMatcher will be set by the build, do not set a CharMatcher directly using this method.
   * 
   * @param charMatcher
   */
  public void setCharMatcher(CharMatcher charMatcher) {
    this.charMatcher = charMatcher;
  }
}
