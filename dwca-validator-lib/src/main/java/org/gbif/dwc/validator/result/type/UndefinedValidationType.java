package org.gbif.dwc.validator.result.type;

/**
 * Undefined validation types are mostly used to return positive result (success).
 * When there is a failure, we know which ValidationTypeIF failed but in case of success we may not want
 * to return one object per successful event so we use the type UndefinedValidationType.
 * 
 * @author cgendreau
 */
public enum UndefinedValidationType implements ValidationTypeIF {
  UNDEFINED(1, "validation_type.undefined");

  private static final String PREFIX = "UD-";
  private String id;
  private String descriptionKey;

  private UndefinedValidationType(int id, String descriptionKey) {
    this.id = PREFIX + id;
    this.descriptionKey = descriptionKey;
  }

  @Override
  public String getDescriptionKey() {
    return descriptionKey;
  }

  @Override
  public String getId() {
    return id;
  }

}
