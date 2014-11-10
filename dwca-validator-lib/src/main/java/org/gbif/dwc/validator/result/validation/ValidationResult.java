package org.gbif.dwc.validator.result.validation;

import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Main object holding validation results for a specific id within a context.
 * This object is immutable.
 * 
 * @author cgendreau
 */
public class ValidationResult implements EvaluationResult<ValidationResultElement> {

  private final String id;
  private final EvaluationContext evaluationContext;
  private final String evaluationContextDetails;

  private final String evaluatorKey;
  private final List<ValidationResultElement> results;

  /**
   * @param id The identifier is relative to the context.
   * @param evaluatorKey key of the validator who generated this entry
   * @param evaluationContext
   * @param results
   */
  public ValidationResult(String id, String evaluatorKey, EvaluationContext evaluationContext,
    String evaluationContextDetails, List<ValidationResultElement> results) {
    this.id = id;
    this.evaluatorKey = evaluatorKey;
    this.evaluationContext = evaluationContext;
    this.evaluationContextDetails = evaluationContextDetails;

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
   * @param evaluatorKey key of the validator who generated this entry
   * @param evaluationContext
   * @param result
   */
  public ValidationResult(String id, String evaluatorKey, EvaluationContext evaluationContext,
    ValidationResultElement result) {
    this(id, evaluatorKey, evaluationContext, "", Arrays.asList(result));
  }

  public ValidationResult(String id, String evaluatorKey, EvaluationContext evaluationContext,
    String evaluationContextDetails, ValidationResultElement result) {
    this(id, evaluatorKey, evaluationContext, evaluationContextDetails, Arrays.asList(result));
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

  @Override
  public List<ValidationResultElement> getResults() {
    return results;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", id).append("evaluatorKey", evaluatorKey)
      .append("evaluationContext", evaluationContext).append("evaluationContextDetails", evaluationContextDetails)
      .append("results", results).toString();
  }
}
