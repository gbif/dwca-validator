package org.gbif.dwc.validator.result;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Main object holding validation results for a specific id within a context.
 * This object is immutable.
 * 
 * @author cgendreau
 */
public class ValidationResult {

  private final String id;
  private final ValidationContext context;
  private final List<ValidationResultElement> results;

  /**
   * @param id The identifier is relative to the context.
   * @param context
   * @param results
   */
  public ValidationResult(String id, ValidationContext context, List<ValidationResultElement> results) {
    this.id = id;
    this.context = context;

    // shouldn't we throw an exception or maybe create an empty list?
    if (results == null) {
      this.results = null;
    } else {
      this.results = ImmutableList.copyOf(results);
    }
  }

  public ValidationContext getContext() {
    return context;
  }

  public String getId() {
    return id;
  }

  public List<ValidationResultElement> getResults() {
    return results;
  }
}
