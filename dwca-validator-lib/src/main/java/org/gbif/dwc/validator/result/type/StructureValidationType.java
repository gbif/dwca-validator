package org.gbif.dwc.validator.result.type;

/**
 * Validation types related to archive structure.
 * 
 * @author cgendreau
 */
public enum StructureValidationType implements ValidationTypeIF {
  EML_SCHEMA(1, "eml.schema");

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
