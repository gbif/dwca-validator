package org.gbif.dwc.validator.evaluator.term;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;
import org.gbif.dwc.validator.evaluator.configuration.RecordCompletionEvaluatorConfiguration;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule;

import com.google.common.base.Preconditions;

/**
 * Builder of RecordCompletionEvaluator object.
 * 
 * @author cgendreau
 */
@RecordEvaluatorBuilderKey
public class RecordCompletionEvaluatorBuilder implements RecordEvaluatorBuilder {

  private final RecordCompletionEvaluatorConfiguration configuration;

  public RecordCompletionEvaluatorBuilder() {
    configuration = new RecordCompletionEvaluatorConfiguration();
  }

  public RecordCompletionEvaluatorBuilder(RecordCompletionEvaluatorConfiguration configuration) {
    this.configuration = configuration;
  }

  public static RecordCompletionEvaluatorBuilder builder() {
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
  public RecordEvaluator build() throws NullPointerException, IllegalStateException {
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
  public RecordCompletionEvaluatorBuilder
    setBlankValueEvaluationRule(BlankValueEvaluationRule blankValueEvaluationRule) {
    configuration.setBlankValueEvaluationRule(blankValueEvaluationRule);
    return this;
  }
}
