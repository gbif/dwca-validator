package org.gbif.dwc.validator.evaluator.term;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.evaluator.configuration.RecordCompletionEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

/**
 * RecordEvaluatorIF implementation to check the completion of a record.
 * The definition of 'completion' is defined by BlankValueEvaluationRule and will be used for all specified terms.
 * TODO Check the number of terms used
 * 
 * @author cgendreau
 */
@RecordEvaluatorKey(key = "recordCompletionEvaluator")
class RecordCompletionEvaluator implements RecordEvaluator {

  private final String key = RecordCompletionEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();
  private final EvaluationContext evaluatorContext;
  private final BlankValueEvaluationRule blankValueEvaluationRule;
  private final List<ConceptTerm> terms;

  /**
   * Use builder to get instance.
   * 
   * @param configuration
   */
  RecordCompletionEvaluator(RecordCompletionEvaluatorConfiguration configuration) {
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
  public Optional<ValidationResult> handleEval(Record record) {
    ValidationResult validationResult = null;
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
      validationResult = new ValidationResult(record.id(), key, evaluatorContext, elementList);
    }

    return Optional.fromNullable(validationResult);

  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    // noop
  }
}
