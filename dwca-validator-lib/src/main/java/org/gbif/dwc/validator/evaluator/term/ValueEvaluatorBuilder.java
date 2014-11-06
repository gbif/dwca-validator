package org.gbif.dwc.validator.evaluator.term;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.configuration.ValueEvaluatorConfiguration;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * Builder of ValueEvaluator object.
 * 
 * @author cgendreau
 */
public class ValueEvaluatorBuilder implements RecordEvaluatorBuilder {

  private final ValueEvaluatorConfiguration configuration;


  public ValueEvaluatorBuilder() {
    this.configuration = new ValueEvaluatorConfiguration();
  }

  public ValueEvaluatorBuilder(ValueEvaluatorConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Create with default value.
   * 
   * @return
   */
  public static ValueEvaluatorBuilder builder() {
    return new ValueEvaluatorBuilder();
  }


  /**
   * Add a rule for a ConceptTerm value.
   * 
   * @param term
   * @param rule
   * @return
   */
  public ValueEvaluatorBuilder addRule(ConceptTerm term, EvaluationRuleIF<String> rule) {
    if (configuration.getRulesPerTerm() == null) {
      configuration.setRulesPerTerm(new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>());
    }

    Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm = configuration.getRulesPerTerm();
    if (rulesPerTerm.get(term) == null) {
      rulesPerTerm.put(term, new ArrayList<EvaluationRuleIF<String>>());
    }
    rulesPerTerm.get(term).add(rule);

    return this;
  }

  /**
   * Add a rule to multiple ConceptTerm
   * 
   * @param terms
   * @param rule
   * @return
   */
  public ValueEvaluatorBuilder addRule(List<ConceptTerm> terms, EvaluationRuleIF<String> rule) {
    for (ConceptTerm currTerm : terms) {
      addRule(currTerm, rule);
    }
    return this;
  }

  /**
   * Add multiple rules to a ConceptTerm.
   * 
   * @param term
   * @param rules
   * @return
   */
  public ValueEvaluatorBuilder addRules(ConceptTerm term, List<EvaluationRuleIF<String>> rules) {
    for (EvaluationRuleIF<String> currRule : rules) {
      addRule(term, currRule);
    }
    return this;
  }

  /**
   * Build ValueEvaluator object.
   * 
   * @return immutable ValueEvaluator object
   * @throws NullPointerException if evaluatorContext or rulesPerTerm is null
   * @throws IllegalStateException if rulesPerTerm is empty
   */
  @Override
  public ValueEvaluator build() throws NullPointerException, IllegalStateException {
    Preconditions.checkNotNull(configuration.getEvaluatorContext());
    Preconditions.checkNotNull(configuration.getRulesPerTerm());
    Preconditions.checkState(configuration.getRulesPerTerm().size() > 0,
      "The rulesPerTerm must contains at least one element");

    return new ValueEvaluator(configuration);
  }
}
