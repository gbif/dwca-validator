package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluator;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.validation.ValidationResult;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * General RecordEvaluatorIF implementation to check the values inside a record.
 * If an evaluation requires more than one field or relies on a specific order of the EvaluationRuleIF to be
 * accomplished this implementation should NOT be used.
 * This validation is about what the value is and not what the value represents.
 * The evaluation of the values is made by ConceptTerm, using a list of EvaluationRuleIF.
 * 
 * @author cgendreau
 */
@RecordEvaluator(key = "valueEvaluator")
public class ValueEvaluator implements RecordEvaluatorIF {

  /**
   * Container object holding ValueEvaluator configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private EvaluationContext evaluatorContext;
    private Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm;

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

    private ValueEvaluatorBuilder(EvaluationContext evaluatorContext) {
      configuration.setEvaluatorContext(evaluatorContext);
    }

    /**
     * Create with default value. Using coreId, ValidationContext.CORE
     * 
     * @return
     */
    public static ValueEvaluatorBuilder create() {
      return new ValueEvaluatorBuilder(EvaluationContext.CORE);
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
     * Build ValueEvaluator.
     * 
     * @return immutable ValueEvaluator object
     * @throws IllegalStateException
     */
    public ValueEvaluator build() throws IllegalStateException {
      if (configuration.getEvaluatorContext() == null) {
        throw new IllegalStateException("The evaluatorContext must be set");
      }

      if (configuration.getRulesPerTerm() == null || configuration.getRulesPerTerm().size() == 0) {
        throw new IllegalStateException("The rulesPerTerm must contains at least one element");
      }

      return new ValueEvaluator(configuration);
    }
  }

  private final String key = ValueEvaluator.class.getAnnotation(RecordEvaluator.class).key();
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
  public void handleEval(Record record, ResultAccumulatorIF resultAccumulator) {
    List<ValidationResultElement> elementList = null;
    ValidationResultElement validationResultElement;

    // only iterate over terms we have a rule for
    for (ConceptTerm currTerm : rulesPerTerm.keySet()) {
      for (EvaluationRuleIF<String> currRule : rulesPerTerm.get(currTerm)) {
        validationResultElement = currRule.evaluate(record.value(currTerm));
        if (validationResultElement != null) {
          // lazy create the list assuming, in normal case, we should have more valid record
          if (elementList == null) {
            elementList = new ArrayList<ValidationResultElement>();
          }
          elementList.add(validationResultElement);
        }
      }
    }

    if (elementList != null && elementList.size() > 0) {
      resultAccumulator.accumulate(new ValidationResult(record.id(), key, evaluatorContext, elementList));
    }
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    // noop
  }
}
