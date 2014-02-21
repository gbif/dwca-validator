package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.result.type.ValidationTypeIF;

/**
 * Object holding validation result for a specific validation type.
 * 
 * @author cgendreau
 */
public class ValidationResultElement {

  private ValidationTypeIF type;
  private Result result;
  private String explanation;

  public String getExplanation() {
    return explanation;
  }

  public Result getResult() {
    return result;
  }

  public ValidationTypeIF getType() {
    return type;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public void setType(ValidationTypeIF type) {
    this.type = type;
  }

}
