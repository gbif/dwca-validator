package org.gbif.dwc.validator.evaluator.term;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.evaluator.configuration.RecordCompletionEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

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
  private final String rowTypeRestriction;
  private final BlankValueEvaluationRule blankValueEvaluationRule;
  private final List<ConceptTerm> terms;

  /**
   * Use builder to get instance.
   * 
   * @param configuration
   */
  RecordCompletionEvaluator(RecordCompletionEvaluatorConfiguration configuration) {
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
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
  public Optional<ValidationResult> handleEval(Record record, EvaluationContext evaluationContext) {
    ValidationResult validationResult = null;
    // record.terms().size());

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(record.rowType())) {
      return Optional.absent();
    }

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

          // create a new element based on the one from the evaluation rule
          validationResultElement =
            new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.ERROR,
              ValidatorConfig.getLocalizedString("evaluator.record_completion", currTerm,
                validationResultElement.getExplanation()));

          // evaluator.record_completion
          elementList.add(validationResultElement);
        }
      }
    }

    if (elementList != null && elementList.size() > 0) {
      validationResult = new ValidationResult(record.id(), key, evaluationContext, record.rowType(), elementList);
    }

    return Optional.fromNullable(validationResult);
  }
}
