package org.gbif.dwc.validator.result.type;

/**
 * Validation types related to content of the archive.
 * 
 * @author cgendreau
 */
public enum ContentValidationType implements ValidationTypeIF {
  RECORD_CONTENT_VALUE(1, "validation_type.record_content.value"), RECORD_CONTENT_BOUNDS(2,
    "validation_type.record_content.bounds"), RECORD_CONTENT_PRECISION(3, "validation_type.record_content.precision"),
  FIELD_UNIQUENESS(4, "validation_type.uniqueness"), FIELD_REFERENTIAL_INTEGRITY(5,
    "validation_type.referential_integrity");

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
