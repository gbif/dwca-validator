package org.gbif.dwc.validator.criteria.metadata;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import com.google.common.base.Optional;
import org.xml.sax.SAXException;

/**
 * XMLMetadataCriterion allows validate a XML metadata file against a XML schema.
 * 
 * @author melecoq
 * @author cgendreau
 */
class XMLMetadataCriterion implements MetadataCriterion {

  // TODO replace with new annotation like @StructureEvaluator
  private static final String key = "XMLMetadataCriterion";

  private final String metadataFilename;
  private final Validator metaValidator;

  XMLMetadataCriterion(Validator metaValidator, String metadataFilename) {
    this.metaValidator = metaValidator;
    this.metadataFilename = metadataFilename;
  }

  @Override
  public Optional<ValidationResult> validate(File metadataFile) {

    // only validate the meta.xml file
    if (!metadataFilename.equalsIgnoreCase(metadataFile.getName())) {
      return Optional.absent();
    }

// if (!metaXML.exists()) {
// return Optional.absent();
// }

    String identifier = metadataFile.getName();

    try {
      metaValidator.validate(new StreamSource(metadataFile));
    } catch (SAXException saxEx) {
      return Optional.of(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.METADATA_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "criterion.xml_metadata_criterion.schema_validation", saxEx.getMessage()))));
    } catch (IOException ioEx) {
      return Optional.of(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.METADATA_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "criterion.xml_metadata_criterion.schema_validation", ioEx.getMessage()))));
    }

    return Optional.of(new ValidationResult(identifier, EvaluationContext.STRUCTURE, (String) null));
  }
}
