package org.gbif.dwc.validator.evaluator.structure;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * The DwcA reader will run the schema check on the meta.xml.
 * 
 * @author melecoq
 */
public class MetaDescriptorEvaluator {

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

  public void doEval(File metaXML, ResultAccumulatorIF result) throws ResultAccumulationException {
    handleEval(metaXML, result);
  }

  protected void handleEval(File metaXML, ResultAccumulatorIF result) throws ResultAccumulationException {

    if (metaXML == null || !metaXML.exists()) {
      result.accumulate(new ValidationResult("meta XML", key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.ARCHIVE_STRUCTURE, Result.ERROR, ValidatorConfig
          .getLocalizedString("evaluator.file_not_found"))));
    }

    String identifier = metaXML.getName();

    try {
      metaValidator.validate(new StreamSource(metaXML));
    } catch (SAXException saxEx) {
      result.accumulate(new ValidationResult(identifier, key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.METADATA_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.internal_error", saxEx.getMessage()))));
    } catch (IOException ioEx) {
      result.accumulate(new ValidationResult(identifier, key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.METADATA_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.internal_error", ioEx.getMessage()))));
    }
  }
}
