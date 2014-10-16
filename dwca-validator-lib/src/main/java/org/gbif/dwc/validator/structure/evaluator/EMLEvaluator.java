package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.validation.ValidationResult;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.metadata.eml.ValidatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

/**
 * @author melecoq
 */
public class EMLEvaluator {

  // TODO replace with new annotation like @StructureEvaluator
  private static final String key = "EMLEvaluator";

  public void doEval(File eml, ResultAccumulatorIF result) {
    handleEval(eml, result);
  }

  protected Source getEmlSource(File eml) throws FileNotFoundException {
    Source src = new StreamSource(new FileInputStream(eml));
    return src;
  }

  protected void handleEval(File eml, ResultAccumulatorIF result) {
    String identifier = eml.getName();
    try {
      // TODO cache Validator because it's expensive to create
      ValidatorFactory.getGbifValidator().validate(getEmlSource(eml));
    } catch (MalformedURLException e) {
      result.accumulate(new ValidationResult(identifier, key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig.getLocalizedString(
          "evaluator.internal_error", e.getMessage()))));
    } catch (FileNotFoundException e) {
      result.accumulate(new ValidationResult(identifier, key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig
          .getLocalizedString("evaluator.file_not_found"))));
    } catch (SAXException e) {
      result.accumulate(new ValidationResult(identifier, key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig.getLocalizedString(
          "evaluator.internal_error", e.getMessage()))));
    } catch (IOException e) {
      result.accumulate(new ValidationResult(identifier, key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig.getLocalizedString(
          "evaluator.internal_error", e.getMessage()))));
    }
  }
}
