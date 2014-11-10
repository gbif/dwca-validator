package org.gbif.dwc.validator.evaluator.configuration;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container object holding ValueEvaluator configurations.
 * 
 * @author cgendreau
 */
@RecordEvaluatorConfigurationKey
public class ValueEvaluatorConfiguration {

  private String rowTypeRestriction;
  private Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm;

  public ValueEvaluatorConfiguration() {
    rulesPerTerm = new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>();
  }

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Map<ConceptTerm, List<EvaluationRuleIF<String>>> getRulesPerTerm() {
    return rulesPerTerm;
  }

  public void setRulesPerTerm(Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm) {
    this.rulesPerTerm = rulesPerTerm;
  }

}
