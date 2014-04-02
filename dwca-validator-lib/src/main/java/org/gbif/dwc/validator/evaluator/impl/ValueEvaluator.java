package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.ChainableRecordEvaluator;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChainableRecordEvaluator to check the values inside a record.
 * This validation is about what the value is and not what the value represents.
 * 
 * @author cgendreau
 */
public class ValueEvaluator extends ChainableRecordEvaluator {

  // hold all evaluation rules per ConceptTerm
  private final Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm;

  public ValueEvaluator() {
    rulesPerTerm = new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>();
  }

  /**
   * Add an evaluation rule this ValueEvaluator.
   * 
   * @param term term against which this rule will be applied.
   * @param rule
   */
  public void addEvaluationRule(ConceptTerm term, EvaluationRuleIF<String> rule) {
    if (!rulesPerTerm.containsKey(term)) {
      rulesPerTerm.put(term, new ArrayList<EvaluationRuleIF<String>>());
    }
    rulesPerTerm.get(term).add(rule);
  }

  @Override
  protected void handleEval(Record record, ResultAccumulatorIF resultAccumulator) {
    // only iterate over terms we have a rule for
    for (ConceptTerm currTerm : rulesPerTerm.keySet()) {
      for (EvaluationRuleIF<String> currRule : rulesPerTerm.get(currTerm)) {
        currRule.evaluate(record.value(currTerm), resultAccumulator);
      }
    }
  }

  @Override
  protected void postIterate() {
    // noop
  }
}
