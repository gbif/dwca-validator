package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.result.type.ValidationTypeIF;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Object holding validation result for a specific validation type.
 * This object is immutable.
 * 
 * @author cgendreau
 */
public class ValidationResultElement {

  private final ValidationTypeIF type;
  private final Result result;
  private final String explanation;

  /**
   * @param type
   * @param result
   * @param explanation
   */
  public ValidationResultElement(ValidationTypeIF type, Result result, String explanation) {
    this.type = type;
    this.result = result;
    this.explanation = explanation;
  }

  public String getExplanation() {
    return explanation;
  }

  public Result getResult() {
    return result;
  }

  public ValidationTypeIF getType() {
    return type;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("type", type).append("result", result).append("explanation", explanation)
      .toString();
  }


}
