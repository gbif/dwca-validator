package org.gbif.dwc.validator.rule;

import org.gbif.dwc.validator.result.EvaluationRuleResult;

/**
 * Evaluation rules are used against the value of a Term within a record.
 * 
 * @author cgendreau
 * @param <T>
 */
public interface EvaluationRule<T> {

  /**
   * Returns a EvaluationRuleResult object, never null.
   * 
   * @param obj
   * @return EvaluationRuleResult instance.
   */
  EvaluationRuleResult evaluate(T obj);

}
