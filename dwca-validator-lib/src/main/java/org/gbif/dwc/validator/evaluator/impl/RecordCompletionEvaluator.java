package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfiguration;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.validation.ValidationResult;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule.BlankValueEvaluationRuleBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * RecordEvaluatorIF implementation to check the completion of a record.
 * The definition of 'completion' is defined by BlankValueEvaluationRule and will be used for all specified terms.
 * TODO Check the number of terms used
 * 
 * @author cgendreau
 */
@RecordEvaluatorKey(key = "recordCompletionEvaluator")
public class RecordCompletionEvaluator implements RecordEvaluator {

  /**
   * Container object holding RecordCompletionEvaluator configurations.
   * 
   * @author cgendreau
   */
  @RecordEvaluatorConfiguration
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
  public static class RecordCompletionEvaluatorBuilder implements RecordEvaluatorBuilder {

    private final Configuration configuration;

    private RecordCompletionEvaluatorBuilder() {
      configuration = new Configuration();
    }

    public RecordCompletionEvaluatorBuilder(Configuration configuration) {
      this.configuration = configuration;
    }

    public static RecordCompletionEvaluatorBuilder create() {
      return new RecordCompletionEvaluatorBuilder();
    }

    /**
     * Build RecordCompletionEvaluator object.
     * 
     * @return immutable RecordCompletionEvaluator object
     * @throws NullPointerException evaluatorContext, terms or blankValueEvaluationRule is null
     * @throws IllegalStateException if no terms were specified
     */
    @Override
    public RecordCompletionEvaluator build() throws NullPointerException, IllegalStateException {
      Preconditions.checkNotNull(configuration.getEvaluatorContext());
      Preconditions.checkNotNull(configuration.getTerms());
      Preconditions.checkNotNull(configuration.getBlankValueEvaluationRule());

      Preconditions.checkState(configuration.getTerms().size() > 0, "At least one term must be set");

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

  private final String key = RecordCompletionEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();
  private final EvaluationContext evaluatorContext;
  private final BlankValueEvaluationRule blankValueEvaluationRule;
  private final List<ConceptTerm> terms;

  /**
   * Use builder to get instance.
   * 
   * @param configuration
   */
  private RecordCompletionEvaluator(Configuration configuration) {
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
        if (validationResultElement.resultIsNot(Result.PASSED)) {
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
