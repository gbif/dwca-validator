package org.gbif.dwc.validator.criteria.archive;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * The DwcA reader will run the schema check on the meta.xml.
 * 
 * @author melecoq
 * @author cgendreau
 */
public class MetaDescriptorEvaluator implements ArchiveCriterion {

  private static final String META_XML_FILE = "meta.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(MetaDescriptorEvaluator.class);

  // TODO replace with new annotation like @StructureEvaluator
  private static final String key = "MetaDescriptorEvaluator";
  private Validator metaValidator;

  /**
   * TODO propagate exceptions
   */
  public MetaDescriptorEvaluator() {
    String schemaLang = "http://www.w3.org/2001/XMLSchema";

    try {
      SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
      Schema schema = factory.newSchema(new URL(ValidatorConfig.META_XML_SCHEMA));
      metaValidator = schema.newValidator();
    } catch (MalformedURLException e) {
      e.printStackTrace();
      LOGGER.error("Can't create Meta XML Schema", e);
    } catch (SAXException e) {
      LOGGER.error("Can't create Meta XML Schema", e);
    }
  }

  @Override
  public Optional<ValidationResult> validate(Archive dwc) {

    File metaXML = new File(dwc.getLocation(), META_XML_FILE);

    if (!metaXML.exists()) {
      return Optional.absent();
    }

    String identifier = metaXML.getName();

    try {
      metaValidator.validate(new StreamSource(metaXML));
    } catch (SAXException saxEx) {
      return Optional.of(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.METADATA_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.internal_error", saxEx.getMessage()))));
    } catch (IOException ioEx) {
      return Optional.of(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.METADATA_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.internal_error", ioEx.getMessage()))));
    }

    return Optional.of(new ValidationResult(identifier, EvaluationContext.STRUCTURE, (String) null));
  }
}
