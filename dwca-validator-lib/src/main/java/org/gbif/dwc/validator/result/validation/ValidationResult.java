package org.gbif.dwc.validator.result.validation;

import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulator;

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
public class ValidationResult implements EvaluationResult {

  private final String id;
  private final EvaluationContext evaluationContext;
  private final String evaluationContextDetails;
  private Result result;

  private final List<ValidationResultElement> results;

  /**
   * @param id The identifier is relative to the context.
   * @param evaluatorKey key of the validator who generated this entry
   * @param evaluationContext
   * @param results
   */
  public ValidationResult(String id, EvaluationContext evaluationContext, String evaluationContextDetails,
    List<ValidationResultElement> results) {
    this.id = id;
    this.evaluationContext = evaluationContext;
    this.evaluationContextDetails = evaluationContextDetails;

    if (results != null) {
      this.results = ImmutableList.copyOf(results);

      Result tmpResult = Result.PASSED;
      for (ValidationResultElement currElement : results) {
        if (currElement.resultIs(Result.ERROR)) {
          tmpResult = Result.ERROR;
          break;
        }

        if (currElement.resultIs(Result.WARNING)) {
          tmpResult = Result.WARNING;
        }
      }
      this.result = tmpResult;
    } else {
      this.results = null;
      this.result = Result.PASSED;
    }
  }

  /**
   * Constructor to use only with one ValidationResultElement.
   * 
   * @param id
   * @param evaluationContext
   * @param result
   */
  public ValidationResult(String id, EvaluationContext evaluationContext, ValidationResultElement result) {
    this(id, evaluationContext, "", Arrays.asList(result));
  }

  public ValidationResult(String id, EvaluationContext evaluationContext, String evaluationContextDetails,
    ValidationResultElement result) {
    this(id, evaluationContext, evaluationContextDetails, Arrays.asList(result));
  }

  public ValidationResult(String id, EvaluationContext evaluationContext, String evaluationContextDetails) {
    this(id, evaluationContext, evaluationContextDetails, (List<ValidationResultElement>) null);
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

  public Result getResult() {
    return result;
  }

  /**
   * Same as getResult() == Result.PASSED
   * 
   * @return
   */
  public boolean passed() {
    return (Result.PASSED == result);
  }

  /**
   * Same as getResult() == Result.ERROR
   * 
   * @return
   */
  public boolean failed() {
    return (Result.ERROR == result);
  }

  public List<ValidationResultElement> getResults() {
    return results;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", id).append("evaluationContext", evaluationContext)
      .append("evaluationContextDetails", evaluationContextDetails).append("results", results).toString();
  }

  @Override
  public void accept(ResultAccumulator visitor) throws ResultAccumulationException {
    visitor.accumulate(this);
  }

}
