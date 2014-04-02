package org.gbif.dwc.validator.rule;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;

/**
 * Evaluation rules are used against the value of a ConceptTerm within a record.
 * 
 * @author cgendreau
 * @param <T>
 */
public interface EvaluationRuleIF<T> {

  public void evaluate(T obj, ResultAccumulatorIF resultAccumulator);

}
