package org.gbif.dwc.validator.rule;

import org.gbif.dwc.validator.result.ValidationResultElement;

/**
 * Evaluation rules are used against the value of a ConceptTerm within a record.
 * 
 * @author cgendreau
 * @param <T>
 */
public interface EvaluationRuleIF<T> {

  /**
   * Be aware, this method returns null if we have no results. Maybe a final static ValidationResultElement instance for
   * 'valid' state would be better?
   * 
   * @param obj
   * @return null if no ValidationResultElement is needed, ValidationResultElement instance otherwise.
   */
  public ValidationResultElement evaluate(T obj);

}
