package org.gbif.dwc.validator.result.type;

/**
 * Validation types related to content of the archive.
 * 
 * @author cgendreau
 */
public enum ContentValidationType implements ValidationTypeIF {
  RECORD_CONTENT(1, "record.content"), FIELD_UNIQUENESS(2, "field.uniqueness"), FIELD_REFERENTIAL_INTEGRITY(3,
    "field.referential_integrity");

  private static final String PREFIX = "CV-";
  private String id;
  private String descriptionKey;

  private ContentValidationType(int id, String descriptionKey) {
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
