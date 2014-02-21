package org.gbif.dwc.validator.result;

import java.util.List;

/**
 * Main object holding validation results for a specific id within a context.
 * 
 * @author cgendreau
 */
public class ValidationResult {

  private String id;
  private ValidationContext context;
  private List<ValidationResultElement> results;

  public ValidationContext getContext() {
    return context;
  }

  public String getId() {
    return id;
  }

  public List<ValidationResultElement> getResults() {
    return results;
  }

  public void setContext(ValidationContext context) {
    this.context = context;
  }

  /**
   * The identifier is relative to the context.
   * 
   * @param id
   */
  public void setId(String id) {
    this.id = id;
  }

  public void setResults(List<ValidationResultElement> results) {
    this.results = results;
  }
}
