package org.gbif.dwc.validator.evaluator.configuration;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.result.EvaluationContext;
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

  private EvaluationContext evaluatorContext;
  private Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm;

  /**
   * Default value EvaluationContext.CORE, empty rulesPerTerm
   */
  public ValueEvaluatorConfiguration() {
    evaluatorContext = EvaluationContext.CORE;
    rulesPerTerm = new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>();
  }

  public EvaluationContext getEvaluatorContext() {
    return evaluatorContext;
  }

  public Map<ConceptTerm, List<EvaluationRuleIF<String>>> getRulesPerTerm() {
    return rulesPerTerm;
  }

  public void setEvaluatorContext(EvaluationContext evaluatorContext) {
    this.evaluatorContext = evaluatorContext;
  }

  public void setRulesPerTerm(Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm) {
    this.rulesPerTerm = rulesPerTerm;
  }

}
