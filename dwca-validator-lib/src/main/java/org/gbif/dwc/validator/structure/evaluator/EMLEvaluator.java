package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.result.ValidationResult;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.metadata.eml.ValidatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

/**
 * @author melecoq
 */
public class EMLEvaluator {

  /**
   * @author melecoq
   * @throws EMLInvalidException
   */
  public void doEval(File eml, ResultAccumulatorIF result) {
    handleEval(eml, result);
  }

  protected Source getEmlSource(File eml) throws FileNotFoundException {
    Source src = new StreamSource(new FileInputStream(eml));
    return src;
  }

  protected void handleEval(File eml, ResultAccumulatorIF result) {

    try {
      ValidatorFactory.getGbifValidator().validate(getEmlSource(eml));
    } catch (MalformedURLException e) {
      List<ValidationResultElement> list = new ArrayList<ValidationResultElement>();
      list.add(new ValidationResultElement(StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig
        .getLocalizedString("evaluator.internal_error", e.getMessage())));
      result.accumulate(new ValidationResult("EMLValidation", ValidationContext.STRUCTURE, list));
    } catch (FileNotFoundException e) {
      List<ValidationResultElement> list = new ArrayList<ValidationResultElement>();
      list.add(new ValidationResultElement(StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig
        .getLocalizedString("evaluator.file_not_found")));
      result.accumulate(new ValidationResult("EMLValidation", ValidationContext.STRUCTURE, list));
    } catch (SAXException e) {
      List<ValidationResultElement> list = new ArrayList<ValidationResultElement>();
      list.add(new ValidationResultElement(StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig
        .getLocalizedString("evaluator.internal_error", e.getMessage())));
      result.accumulate(new ValidationResult("EMLValidation", ValidationContext.STRUCTURE, list));
    } catch (IOException e) {
      List<ValidationResultElement> list = new ArrayList<ValidationResultElement>();
      list.add(new ValidationResultElement(StructureValidationType.EML_SCHEMA, Result.ERROR, ArchiveValidatorConfig
        .getLocalizedString("evaluator.internal_error", e.getMessage())));
      result.accumulate(new ValidationResult("EMLValidation", ValidationContext.STRUCTURE, list));
    }

  }
}
