package org.gbif.dwc.validator.result.impl;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.EvaluationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory ResultAccumulatorIF, maximum number of accumulated results is defined
 * by MAX_RESULT.
 * 
 * @author cgendreau
 */
public class InMemoryResultAccumulator implements ResultAccumulatorIF {

  private static final int MAX_RESULT = 100;
  private final List<EvaluationResult> evaluationResultList;

  public InMemoryResultAccumulator() {
    evaluationResultList = new ArrayList<EvaluationResult>();
  }

  @Override
  public boolean accumulate(EvaluationResult result) {
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

  public List<EvaluationResult> getEvaluationResultList() {
    return evaluationResultList;
  }

}
