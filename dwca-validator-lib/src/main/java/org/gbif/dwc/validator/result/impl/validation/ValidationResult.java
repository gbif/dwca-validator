package org.gbif.dwc.validator.result.impl.validation;

import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResultIF;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Main object holding evaluation results for a specific id within a context.
 * This object is immutable.
 * 
 * @author cgendreau
 */
public class ValidationResult implements EvaluationResultIF<ValidationResultElement> {

  private final String id;
  private final String evaluatorKey;
  private final EvaluationContext context;
  private final List<ValidationResultElement> results;

  /**
   * @param id The identifier is relative to the context.
   * @param evaluatorKey key of the validator who generated this entry
   * @param context
   * @param results
   */
  public ValidationResult(String id, String evaluatorKey, EvaluationContext context,
    List<ValidationResultElement> results) {
    this.id = id;
    this.evaluatorKey = evaluatorKey;
    this.context = context;

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
   * @param context
   * @param result
   */
  public ValidationResult(String id, String evaluatorKey, EvaluationContext context, ValidationResultElement result) {
    this(id, evaluatorKey, context, Arrays.asList(result));
  }

  @Override
  public EvaluationContext getContext() {
    return context;
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
    return new ToStringBuilder(this).append("id", id).append("evaluatorKey", evaluatorKey).append("context", context)
      .append("results", results).toString();
  }
}
