package org.gbif.dwc.validator.result.type;

/**
 * Common interface for validation types.
 * 
 * @author cgendreau
 */
public interface ValidationTypeIF {

  /**
   * Get the description key of this validation type.
   * This key is used in resource bundle to get the textual representation of this validation type.
   * 
   * @return
   */
  String getDescriptionKey();

  /**
   * Id of this validation type.
   * 
   * @return
   */
  String getId();
}
