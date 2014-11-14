package org.gbif.dwc.validator.evaluator.term;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.evaluator.configuration.ValueEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

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
class ValueEvaluator implements RecordEvaluator {

  private final String key = ValueEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();
  // hold all evaluation rules per ConceptTerm
  private final Map<ConceptTerm, List<EvaluationRule<String>>> rulesPerTerm;
  private final String rowTypeRestriction;

  ValueEvaluator(ValueEvaluatorConfiguration configuration) {
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.rulesPerTerm =
      Collections.unmodifiableMap(new HashMap<ConceptTerm, List<EvaluationRule<String>>>(configuration
        .getRulesPerTerm()));
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleEval(Record record, EvaluationContext evaluationContext) {
    ValidationResult validationResult = null;
    List<ValidationResultElement> elementList = null;
    ValidationResultElement validationResultElement;

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(record.rowType())) {
      return Optional.absent();
    }

    // only iterate over terms we have a rule for
    for (ConceptTerm currTerm : rulesPerTerm.keySet()) {
      for (EvaluationRule<String> currRule : rulesPerTerm.get(currTerm)) {
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
      validationResult = new ValidationResult(record.id(), key, evaluationContext, record.rowType(), elementList);
    }

    return Optional.fromNullable(validationResult);
  }
}
