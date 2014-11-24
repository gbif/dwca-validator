package org.gbif.dwc.validator.evaluator.term;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;
import org.gbif.dwc.validator.evaluator.configuration.ValueEvaluatorConfiguration;
import org.gbif.dwc.validator.rule.EvaluationRule;

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
@RecordEvaluatorBuilderKey("valueEvaluator")
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
   * Add a rule for a Term value.
   * 
   * @param term
   * @param rule
   * @return
   */
  public ValueEvaluatorBuilder addRule(Term term, EvaluationRule<String> rule) {
    if (configuration.getRulesPerTerm() == null) {
      configuration.setRulesPerTerm(new HashMap<Term, List<EvaluationRule<String>>>());
    }

    Map<Term, List<EvaluationRule<String>>> rulesPerTerm = configuration.getRulesPerTerm();
    if (rulesPerTerm.get(term) == null) {
      rulesPerTerm.put(term, new ArrayList<EvaluationRule<String>>());
    }
    rulesPerTerm.get(term).add(rule);

    return this;
  }

  /**
   * Add a rule to multiple Term
   * 
   * @param terms
   * @param rule
   * @return
   */
  public ValueEvaluatorBuilder addRule(List<Term> terms, EvaluationRule<String> rule) {
    for (Term currTerm : terms) {
      addRule(currTerm, rule);
    }
    return this;
  }

  /**
   * Add multiple rules to a Term.
   * 
   * @param term
   * @param rules
   * @return
   */
  public ValueEvaluatorBuilder addRules(Term term, List<EvaluationRule<String>> rules) {
    for (EvaluationRule<String> currRule : rules) {
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
  public RecordEvaluator build() throws NullPointerException, IllegalStateException {
    Preconditions.checkNotNull(configuration.getRulesPerTerm());
    Preconditions.checkState(configuration.getRulesPerTerm().size() > 0,
      "The rulesPerTerm must contains at least one element");

    return new ValueEvaluator(configuration);
  }
}
