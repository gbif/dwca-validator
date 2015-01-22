package org.gbif.dwc.validator.result.evaluator;

import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Result of running an Evaluator
 * 
 * @author cgendreau
 */
public class EvaluatorResult implements EvaluationResult {

  private final String id;
  private final EvaluationContext evaluationContext;
  private final String evaluationContextDetails;

  private final String evaluatorInstanceID;

  private final List<EvaluatorResultElement> results;

  /**
   * @param id The identifier is relative to the context.
   * @param evaluatorKey key of the validator who generated this entry
   * @param evaluationContext
   * @param results
   */
  public EvaluatorResult(String id, EvaluationContext evaluationContext, String evaluationContextDetails,
    String evaluatorInstanceID, List<EvaluatorResultElement> results) {
    this.id = id;
    this.evaluationContext = evaluationContext;
    this.evaluationContextDetails = evaluationContextDetails;
    this.evaluatorInstanceID = evaluatorInstanceID;

    // shouldn't we throw an exception or maybe create an empty list?
    if (results == null) {
      this.results = null;
    } else {
      this.results = ImmutableList.copyOf(results);
    }
  }

  /**
   * Constructor to use only with one ValidationResultElement.
   * 
   * @param id
   * @param evaluationContext
   * @param result
   */
  public EvaluatorResult(String id, EvaluationContext evaluationContext, String evaluatorInstanceID,
    EvaluatorResultElement result) {
    this(id, evaluationContext, "", evaluatorInstanceID, Arrays.asList(result));
  }

  public EvaluatorResult(String id, EvaluationContext evaluationContext, String evaluationContextDetails,
    String evaluatorInstanceID, EvaluatorResultElement result) {
    this(id, evaluationContext, evaluationContextDetails, evaluatorInstanceID, Arrays.asList(result));
  }

  @Override
  public EvaluationContext getEvaluationContext() {
    return evaluationContext;
  }

  public String getEvaluationContextDetails() {
    return evaluationContextDetails;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getEvaluatorInstanceID() {
    return evaluatorInstanceID;
  }

  public List<EvaluatorResultElement> getResults() {
    return results;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", id).append("evaluationContext", evaluationContext)
      .append("evaluationContextDetails", evaluationContextDetails).append("results", results).toString();
  }

}
