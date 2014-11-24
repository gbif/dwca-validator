package org.gbif.dwc.validator.evaluator.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule.BlankValueEvaluationRuleBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * Container object holding RecordCompletionEvaluator configurations.
 * 
 * @author cgendreau
 */
@RecordEvaluatorConfigurationKey
public class RecordCompletionEvaluatorConfiguration {

  private String rowTypeRestriction;
  private BlankValueEvaluationRule blankValueEvaluationRule;
  private List<Term> terms;

  /**
   * Create Configuration with default values:
   * ValidationContext.CORE and default BlankValueEvaluationRule.
   */
  public RecordCompletionEvaluatorConfiguration() {
    blankValueEvaluationRule = BlankValueEvaluationRuleBuilder.create().build();
  }

  public void addTerm(Term term) {
    if (terms == null) {
      terms = new ArrayList<Term>();
    }
    terms.add(term);
  }

  public BlankValueEvaluationRule getBlankValueEvaluationRule() {
    return blankValueEvaluationRule;
  }

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public List<Term> getTerms() {
    return terms;
  }

  public void setBlankValueEvaluationRule(BlankValueEvaluationRule blankValueEvaluationRule) {
    this.blankValueEvaluationRule = blankValueEvaluationRule;
  }

  public void setTerms(List<Term> terms) {
    this.terms = terms;
  }
}
