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
   * Builder of RecordCompletionEvaluator object.
   * 
   * @author cgendreau
   */
  public static class RecordCompletionEvaluatorBuilder {

    private final String key = RecordCompletionEvaluator.class.getAnnotation(RecordEvaluator.class).key();

    private final EvaluationContext evaluatorContext;
    private BlankValueEvaluationRule blankValueEvaluationRule;
    private List<ConceptTerm> terms;

    private RecordCompletionEvaluatorBuilder(EvaluationContext evaluatorContext,
      BlankValueEvaluationRule blankValueEvaluationRule) {
      this.evaluatorContext = evaluatorContext;
      this.blankValueEvaluationRule = blankValueEvaluationRule;
    }

    /**
     * Create with default values:
     * ValidationContext.CORE and default BlankValueEvaluationRule.
     * 
     * @return
     */
    public static RecordCompletionEvaluatorBuilder create() {
      return new RecordCompletionEvaluatorBuilder(EvaluationContext.CORE, BlankValueEvaluationRuleBuilder.create()
        .build());
    }

    /**
     * Build RecordCompletionEvaluator.
     * 
     * @return immutable RecordCompletionEvaluator object
     * @throws IllegalStateException
     */
    public RecordCompletionEvaluator build() throws IllegalStateException {
      if (evaluatorContext == null) {
        throw new IllegalStateException("The evaluatorContext must be set");
      }

      // both must be null or both must be provided
      if (blankValueEvaluationRule != null || terms != null) {
        if (blankValueEvaluationRule == null || terms == null || terms.isEmpty()) {
          throw new IllegalStateException(
            "blankValueEvaluationRule and term(s) must both be set if one of them is provided.");
        }
      }

      return new RecordCompletionEvaluator(key, evaluatorContext, blankValueEvaluationRule, terms);
    }

    /**
     * Add a term to check for completion.
     * 
     * @param term
     * @param rule
     * @return
     */
    public RecordCompletionEvaluatorBuilder checkTerm(ConceptTerm term) {
      if (terms == null) {
        terms = new ArrayList<ConceptTerm>();
      }
      terms.add(term);
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
      this.blankValueEvaluationRule = blankValueEvaluationRule;
      return this;
    }
  }

  private final String key;
  private final EvaluationContext evaluatorContext;
  private final BlankValueEvaluationRule blankValueEvaluationRule;
  private final List<ConceptTerm> terms;

  public RecordCompletionEvaluator(String key, EvaluationContext evaluatorContext,
    BlankValueEvaluationRule blankValueEvaluationRule, List<ConceptTerm> terms) {
    this.key = key;
    this.evaluatorContext = evaluatorContext;
    this.blankValueEvaluationRule = blankValueEvaluationRule;
    this.terms = Collections.unmodifiableList(new ArrayList<ConceptTerm>(terms));
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
