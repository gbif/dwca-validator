package org.gbif.dwc.validator.aggregation;

import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ResultAccumulator;


/**
 * Holding aggregation result for a specific id within a context.
 * This object is immutable.
 * 
 * @author cgendreau
 */
public class AggregationResult<T> implements EvaluationResult {

  private final String id;
  private final String evaluatorKey;
  private final EvaluationContext context;
  private final T result;

  public AggregationResult(String id, String evaluatorKey, EvaluationContext context, T result) {
    this.id = id;
    this.evaluatorKey = evaluatorKey;
    this.context = context;
    this.result = result;
  }

  @Override
  public String getId() {
    return id;
  }

  // @Override
  public String getEvaluatorKey() {
    return evaluatorKey;
  }

  @Override
  public EvaluationContext getEvaluationContext() {
    return context;
  }

  public T getResult() {
    return result;
  }

  @Override
  public void accept(ResultAccumulator visitor) throws ResultAccumulationException {
    visitor.accumulate(this);
  }

}
