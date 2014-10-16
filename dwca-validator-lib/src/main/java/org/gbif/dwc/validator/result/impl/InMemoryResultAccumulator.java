package org.gbif.dwc.validator.result.impl;

import org.gbif.dwc.validator.result.EvaluationResultElementIF;
import org.gbif.dwc.validator.result.EvaluationResultIF;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory ResultAccumulatorIF, maximum number of accumulated results is defined
 * by MAX_RESULT.
 * 
 * @author cgendreau
 */
public class InMemoryResultAccumulator implements ResultAccumulatorIF {

  public static final int MAX_RESULT = 100;
  private final List<EvaluationResultIF<? extends EvaluationResultElementIF>> evaluationResultList;

  public InMemoryResultAccumulator() {
    evaluationResultList = new ArrayList<EvaluationResultIF<? extends EvaluationResultElementIF>>();
  }

  @Override
  public boolean accumulate(EvaluationResultIF<? extends EvaluationResultElementIF> result) {
    if (evaluationResultList.size() < MAX_RESULT) {
      return evaluationResultList.add(result);
    }
    return false;
  }

  @Override
  public void close() {
    // noop
  }

  @Override
  public int getCount() {
    return evaluationResultList.size();
  }

  public List<EvaluationResultIF<? extends EvaluationResultElementIF>> getEvaluationResultList() {
    return evaluationResultList;
  }

}
