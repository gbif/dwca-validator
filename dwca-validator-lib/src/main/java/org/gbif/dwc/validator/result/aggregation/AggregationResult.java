package org.gbif.dwc.validator.result.aggregation;

import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;


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

  // @Override
  @Override
  public String getId() {
    return id;
  }

  @Override
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


}
