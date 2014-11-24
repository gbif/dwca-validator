package org.gbif.dwc.validator.evaluator.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.rule.EvaluationRule;

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
  private Map<Term, List<EvaluationRule<String>>> rulesPerTerm;

  public ValueEvaluatorConfiguration() {
    rulesPerTerm = new HashMap<Term, List<EvaluationRule<String>>>();
  }

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Map<Term, List<EvaluationRule<String>>> getRulesPerTerm() {
    return rulesPerTerm;
  }

  public void setRulesPerTerm(Map<Term, List<EvaluationRule<String>>> rulesPerTerm) {
    this.rulesPerTerm = rulesPerTerm;
  }

}
