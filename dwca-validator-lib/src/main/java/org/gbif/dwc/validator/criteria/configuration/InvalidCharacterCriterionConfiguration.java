package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.Result;

import com.google.common.base.CharMatcher;

/**
 * Container object holding InvalidCharacterCriterion configurations.
 * 
 * @author cgendreau
 */
@CriterionConfigurationKey("invalidCharacterCriterion")
public class InvalidCharacterCriterionConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;
  private Term term;

  private boolean allowFormattingWhiteSpace = false;
  private boolean rejectReplacementChar = false;

  private CharMatcher charMatcher;

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Result getLevel() {
    return level;
  }

  public void setLevel(Result level) {
    this.level = level;
  }

  public Term getTerm() {
    return term;
  }

  public void setTerm(Term term) {
    this.term = term;
  }

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
