package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluator;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.validation.ValidationResult;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule.BlankValueEvaluationRuleBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RecordEvaluatorIF implementation to check the completion of a record.
 * The definition of 'completion' is defined by BlankValueEvaluationRule and will be used for all specified terms.
 * TODO Check the number of terms used
 * 
 * @author cgendreau
 */
@RecordEvaluator(key = "recordCompletionEvaluator")
public class RecordCompletionEvaluator implements RecordEvaluatorIF {

  /**
   * Container object holding RecordCompletionEvaluator configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private EvaluationContext evaluatorContext;
    private BlankValueEvaluationRule blankValueEvaluationRule;
    private List<ConceptTerm> terms;

    /**
     * Create Configuration with default values:
     * ValidationContext.CORE and default BlankValueEvaluationRule.
     */
    public Configuration() {
      evaluatorContext = EvaluationContext.CORE;
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

    public EvaluationContext getEvaluatorContext() {
      return evaluatorContext;
    }

    public List<ConceptTerm> getTerms() {
      return terms;
    }

    public void setBlankValueEvaluationRule(BlankValueEvaluationRule blankValueEvaluationRule) {
      this.blankValueEvaluationRule = blankValueEvaluationRule;
    }

    public void setEvaluatorContext(EvaluationContext evaluatorContext) {
      this.evaluatorContext = evaluatorContext;
    }

    public void setTerms(List<ConceptTerm> terms) {
      this.terms = terms;
    }
  }

  /**
   * Builder of RecordCompletionEvaluator object.
   * 
   * @author cgendreau
   */
  public static class RecordCompletionEvaluatorBuilder {

    private final Configuration configuration = new Configuration();

    private RecordCompletionEvaluatorBuilder() {
    }

    public static RecordCompletionEvaluatorBuilder create() {
      return new RecordCompletionEvaluatorBuilder();
    }

    /**
     * Build RecordCompletionEvaluator.
     * 
     * @return immutable RecordCompletionEvaluator object
     * @throws IllegalStateException
     */
    public RecordCompletionEvaluator build() throws IllegalStateException {
      if (configuration.getEvaluatorContext() == null) {
        throw new IllegalStateException("The evaluatorContext must be set");
      }

      // both must be null or both must be provided
      if (configuration.getBlankValueEvaluationRule() != null || configuration.getTerms() != null) {
        if (configuration.getBlankValueEvaluationRule() == null || configuration.getTerms() == null
          || configuration.getTerms().isEmpty()) {
          throw new IllegalStateException(
            "blankValueEvaluationRule and term(s) must both be set if one of them is provided.");
        }
      }

      return new RecordCompletionEvaluator(configuration);
    }

    /**
     * Add a term to check for completion.
     * 
     * @param term
     * @param rule
     * @return
     */
    public RecordCompletionEvaluatorBuilder checkTerm(ConceptTerm term) {
      configuration.addTerm(term);
      return this;
    }

    /**
     * Override the default BlankValueEvaluationRule.
     * 
     * @param blankValueEvaluationRule
     * @return
     */
    public RecordCompletionEvaluatorBuilder setBlankValueEvaluationRule(
      BlankValueEvaluationRule blankValueEvaluationRule) {
      configuration.setBlankValueEvaluationRule(blankValueEvaluationRule);
      return this;
    }
  }

  private final String key = RecordCompletionEvaluator.class.getAnnotation(RecordEvaluator.class).key();
  private final EvaluationContext evaluatorContext;
  private final BlankValueEvaluationRule blankValueEvaluationRule;
  private final List<ConceptTerm> terms;

  public RecordCompletionEvaluator(Configuration configuration) {
    this.evaluatorContext = configuration.getEvaluatorContext();
    this.blankValueEvaluationRule = configuration.getBlankValueEvaluationRule();
    this.terms = Collections.unmodifiableList(new ArrayList<ConceptTerm>(configuration.getTerms()));
  }

  @Override
  public String getKey() {
    return key;
  }

  /**
   * Some lines are candidate for abstraction. see ValueEvaluator
   */
  @Override
  public void handleEval(Record record, ResultAccumulatorIF resultAccumulator) {

    // record.terms().size());

    List<ValidationResultElement> elementList = null;
    ValidationResultElement validationResultElement;
    if (terms != null) {
      for (ConceptTerm currTerm : terms) {
        validationResultElement = blankValueEvaluationRule.evaluate(record.value(currTerm));
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
