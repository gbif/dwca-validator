package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * General RecordEvaluatorIF implementation to check the values inside a record.
 * If an evaluation requires more than one field or relies on a specific order of the EvaluationRuleIF to be
 * accomplished this implementation should NOT be used.
 * This validation is about what the value is and not what the value represents.
 * The evaluation of the values is made by ConceptTerm, using a list of EvaluationRuleIF.
 * 
 * @author cgendreau
 */
@RecordEvaluatorKey(key = "valueEvaluator")
public class ValueEvaluator implements RecordEvaluator {

  /**
   * Container object holding ValueEvaluator configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private EvaluationContext evaluatorContext;
    private Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm;

    /**
     * Default value EvaluationContext.CORE, empty rulesPerTerm
     */
    public Configuration() {
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

  /**
   * Builder of ValueEvaluator object.
   * 
   * @author cgendreau
   */
  public static class ValueEvaluatorBuilder {

    private final Configuration configuration = new Configuration();

    /**
     * Create with default value.
     * 
     * @return
     */
    public static ValueEvaluatorBuilder create() {
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
    public ValueEvaluator build() throws NullPointerException, IllegalStateException {
      Preconditions.checkNotNull(configuration.getEvaluatorContext());
      Preconditions.checkNotNull(configuration.getRulesPerTerm());
      Preconditions.checkState(configuration.getRulesPerTerm().size() > 0,
        "The rulesPerTerm must contains at least one element");

      return new ValueEvaluator(configuration);
    }
  }

  private final String key = ValueEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();
  // hold all evaluation rules per ConceptTerm
  private final Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm;
  private final EvaluationContext evaluatorContext;

  public ValueEvaluator(Configuration configuration) {
    this.evaluatorContext = configuration.getEvaluatorContext();
    this.rulesPerTerm =
      Collections.unmodifiableMap(new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>(configuration
        .getRulesPerTerm()));
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleEval(Record record) {
    ValidationResult validationResult = null;
    List<ValidationResultElement> elementList = null;
    ValidationResultElement validationResultElement;

    // only iterate over terms we have a rule for
    for (ConceptTerm currTerm : rulesPerTerm.keySet()) {
      for (EvaluationRuleIF<String> currRule : rulesPerTerm.get(currTerm)) {
        validationResultElement = currRule.evaluate(record.value(currTerm));
        if (validationResultElement.resultIsNotOneOf(Result.SKIPPED, Result.PASSED)) {
          // lazy create the list assuming, in normal case, we should have more valid record
          if (elementList == null) {
            elementList = new ArrayList<ValidationResultElement>();
          }
          elementList.add(validationResultElement);
        }
      }
    }

    if (elementList != null && elementList.size() > 0) {
      validationResult = new ValidationResult(record.id(), key, evaluatorContext, elementList);
    }

    return Optional.fromNullable(validationResult);
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    // noop
  }
}
