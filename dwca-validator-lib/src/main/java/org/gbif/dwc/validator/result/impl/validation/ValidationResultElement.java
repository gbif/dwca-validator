package org.gbif.dwc.validator.result.impl.validation;

import org.gbif.dwc.validator.result.EvaluationResultElementIF;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.UndefinedValidationType;
import org.gbif.dwc.validator.result.type.ValidationTypeIF;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Immutable object holding validation result for a single validation.
 * 
 * @author cgendreau
 */
public class ValidationResultElement implements EvaluationResultElementIF {

  // Generic constant ValidationResultElement instances
  public static final ValidationResultElement SKIPPED = new ValidationResultElement(UndefinedValidationType.UNDEFINED,
    Result.SKIPPED, "");
  public static final ValidationResultElement PASSED = new ValidationResultElement(UndefinedValidationType.UNDEFINED,
    Result.PASSED, "");

  private final ValidationTypeIF type;
  private final Result result;
  private final String explanation;
  private final Object resultedObject;

  public ValidationResultElement(ValidationTypeIF type, Result result, String explanation) {
    this(type, result, explanation, null);
  }

  /**
   * @param type
   * @param result
   * @param explanation
   * @param resultedObject object obtained while doing the evaluation (optional)
   */
  public ValidationResultElement(ValidationTypeIF type, Result result, String explanation, Object resultedObject) {
    this.type = type;
    this.result = result;
    this.explanation = explanation;
    this.resultedObject = resultedObject;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    ValidationResultElement vre = (ValidationResultElement) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(type, vre.type).append(result, vre.result)
      .append(explanation, vre.explanation).isEquals();
  }

  public String getExplanation() {
    return explanation;
  }

  public Result getResult() {
    return result;
  }

  public Object getResultedObject() {
    return resultedObject;
  }

  public ValidationTypeIF getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(7779, 8903).append(type).append(result).append(explanation).toHashCode();
  }

  /**
   * Check if the Result.
   * 
   * @param result
   * @return
   */
  public boolean is(Result result) {
    if (result == null) {
      return false;
    }
    return result.equals(this.result);
  }

  /**
   * Check if the Result.
   * 
   * @param result
   * @return
   */
  public boolean isNot(Result result) {
    if (result == null) {
      return false;
    }
    return !result.equals(this.result);
  }

  /**
   * Check if the result of this ValidationResultElement is Result.SKIPPED or Result.PASSED
   * 
   * @return
   */
  public boolean isSkippedOrPassed() {
    return (result == Result.SKIPPED || result == Result.PASSED);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("type", type).append("result", result).append("explanation", explanation)
      .toString();
  }
}
