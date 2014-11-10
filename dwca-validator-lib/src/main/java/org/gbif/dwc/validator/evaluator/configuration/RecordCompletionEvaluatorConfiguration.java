package org.gbif.dwc.validator.evaluator.configuration;

import org.gbif.dwc.terms.ConceptTerm;
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
  private List<ConceptTerm> terms;

  /**
   * Create Configuration with default values:
   * ValidationContext.CORE and default BlankValueEvaluationRule.
   */
  public RecordCompletionEvaluatorConfiguration() {
    blankValueEvaluationRule = BlankValueEvaluationRuleBuilder.create().build();
  }

  public void addTerm(ConceptTerm term) {
    if (terms == null) {
      terms = new ArrayList<ConceptTerm>();
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

  public List<ConceptTerm> getTerms() {
    return terms;
  }

  public void setBlankValueEvaluationRule(BlankValueEvaluationRule blankValueEvaluationRule) {
    this.blankValueEvaluationRule = blankValueEvaluationRule;
  }

  public void setTerms(List<ConceptTerm> terms) {
    this.terms = terms;
  }
}
