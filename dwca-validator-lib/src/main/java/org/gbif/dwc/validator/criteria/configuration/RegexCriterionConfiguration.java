package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.Result;

/**
 * Container object holding RegexCriterion configurations.
 * 
 * @author cgendreau
 */
@CriterionConfigurationKey("regexCriterion")
public class RegexCriterionConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;
  private Term term;
  private String regex;
  private String explanation;

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

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public String getExplanation() {
    return explanation;
  }

  /**
   * Set an optional explanation for end user explaining the purpose of the regex.
   * 
   * @param explanation
   */
  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

}
