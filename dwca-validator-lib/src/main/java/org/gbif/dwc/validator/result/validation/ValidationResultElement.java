package org.gbif.dwc.validator.result.validation;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ValidationTypeIF;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Immutable object holding validation result for a single ValidationTypeIF.
 * 
 * @author cgendreau
 */
public class ValidationResultElement {

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
   * Check if the Result is the provided Result value.
   * 
   * @param result
   * @return
   */
  public boolean resultIs(Result result) {
    if (result == null) {
      return false;
    }
    return result.equals(this.result);
  }

  /**
   * Check if the Result is not the provided Result value.
   * 
   * @param result
   * @return
   */
  public boolean resultIsNot(Result result) {
    if (result == null) {
      return false;
    }
    return !result.equals(this.result);
  }

  /**
   * Check if the Result is not one of the provided Result values.
   * 
   * @param result
   * @return
   */
  public boolean resultIsNotOneOf(Result... results) {
    if (results == null) {
      return false;
    }

    for (Result currResult : results) {
      if (currResult.equals(result)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if the Result is one of the provided Result values.
   * 
   * @param result
   * @return
   */
  public boolean resultIsOneOf(Result... results) {
    if (results == null) {
      return false;
    }

    for (Result currResult : results) {
      if (currResult.equals(result)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("type", type).append("result", result).append("explanation", explanation)
      .toString();
  }
}
