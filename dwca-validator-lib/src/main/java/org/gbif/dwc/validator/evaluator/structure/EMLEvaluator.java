package org.gbif.dwc.validator.evaluator.structure;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Evaluator responsible to validate the EML file against its schema.
 * 
 * @author melecoq
 * @author cgendreau
 */
public class EMLEvaluator {

  // define the type of schema - we use W3C:
  private static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  private static final String EML_SCHEMA_URL = "http://rs.gbif.org/schema/eml-2.1.1/eml.xsd";
  private static final String EML_GBIF_PROFILE_SCHEMA_URL = "http://rs.gbif.org/schema/eml-gbif-profile/dev/eml.xsd";
  private static Validator validator;

  // TODO replace with new annotation like @StructureEvaluator
  private static final String key = "EMLEvaluator";

  private static Validator getValidator(String schemaUrl) {
    try {
      // get validation driver:
      SchemaFactory factory = SchemaFactory.newInstance(XML_SCHEMA);
      // create schema by reading it from an URL:
      Schema schema = factory.newSchema(new URL(schemaUrl));
      return schema.newValidator();
    } catch (SAXException e) {
      throw new IllegalStateException("EML schema is invalid", e);

    } catch (MalformedURLException e) {
      throw new IllegalStateException("EML URL is invalid", e);
    }
  }

  private static Validator getValidator() {
    if (validator == null) {
      validator = getValidator(EML_SCHEMA_URL);
    }
    return validator;
  }

  public void doEval(File eml, ResultAccumulator result) throws ResultAccumulationException {
    handleEval(eml, result);
  }

  protected Source getEmlSource(File eml) throws FileNotFoundException {
    Source src = new StreamSource(new FileInputStream(eml));
    return src;
  }

  protected void handleEval(File eml, ResultAccumulator result) throws ResultAccumulationException {

    if (eml == null || !eml.exists()) {
      result.accumulate(new ValidationResult("EML", EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.ARCHIVE_STRUCTURE, Result.ERROR, ValidatorConfig
          .getLocalizedString("evaluator.file_not_found"))));
    }

    String identifier = eml.getName();
    try {
      getValidator().validate(getEmlSource(eml));
    } catch (MalformedURLException e) {
      result.accumulate(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.EML_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.internal_error", e.getMessage()))));
    } catch (FileNotFoundException e) {
      result.accumulate(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.EML_SCHEMA, Result.ERROR, ValidatorConfig
          .getLocalizedString("evaluator.file_not_found"))));
    } catch (SAXException e) {
      result.accumulate(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.EML_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.internal_error", e.getMessage()))));
    } catch (IOException e) {
      result.accumulate(new ValidationResult(identifier, EvaluationContext.STRUCTURE, new ValidationResultElement(key,
        StructureValidationType.EML_SCHEMA, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.internal_error", e.getMessage()))));
    }
  }
}
