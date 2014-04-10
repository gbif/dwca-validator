package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.result.ValidationResult;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChainableRecordEvaluator to check the values inside a record.
 * This validation is about what the value is and not what the value represents.
 * TODO: Evaluate the possibility to add its own builder and offer more options like applyToAll
 * 
 * @author cgendreau
 */
public class ValueEvaluator implements RecordEvaluatorIF {

  // hold all evaluation rules per ConceptTerm
  private final Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm;
  private final ValidationContext evaluatorContext;

  public ValueEvaluator(Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm,
    ValidationContext evaluatorContext) {
    this.rulesPerTerm =
      Collections.unmodifiableMap(new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>(rulesPerTerm));
    this.evaluatorContext = evaluatorContext;
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
      resultAccumulator.accumulate(new ValidationResult(record.id(), evaluatorContext, elementList));
    }
  }

  @Override
  public void postIterate() {
    // noop
  }
}
