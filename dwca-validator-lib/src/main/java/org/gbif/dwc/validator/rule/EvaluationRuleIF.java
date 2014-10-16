package org.gbif.dwc.validator.rule;

import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;

/**
 * Evaluation rules are used against the value of a ConceptTerm within a record.
 * 
 * @author cgendreau
 * @param <T>
 */
public interface EvaluationRuleIF<T> {

  /**
   * Return a ValidationResultElement object, never null.
   * For now, we can only return one ValidationResultElement which may require more thinking in the future.
   * e.g. NumericalValueEvaluationRule could(should?) produce one ValidationResultElement for
   * numerical value check and another one for bounds check.
   * 
   * @param obj
   * @return ValidationResultElement instance.
   */
  ValidationResultElement evaluate(T obj);

}
