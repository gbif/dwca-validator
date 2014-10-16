package org.gbif.dwc.validator.result.type;

/**
 * Validation types related to archive structure.
 * 
 * @author cgendreau
 */
public enum StructureValidationType implements ValidationTypeIF {
  ARCHIVE_STRUCTURE(1, "validation_type.archive_structure"), EML_SCHEMA(2, "validation_type.eml_schema"),
  METADATA_SCHEMA(3, "validation_type.metadata_schema");

  private static final String PREFIX = "SV-";
  private String id;
  private String descriptionKey;

  private StructureValidationType(int id, String descriptionKey) {
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
