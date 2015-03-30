package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;

/**
 * Container object holding RegexCriterion configurations.
 *
 * @author cgendreau
 */
@CriterionConfigurationKey("regexCriterion")
public class RegexCriterionConfiguration extends AbstractRecordCriterionConfiguration {

  private Term term;
  private String regex;
  private String explanation;

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
