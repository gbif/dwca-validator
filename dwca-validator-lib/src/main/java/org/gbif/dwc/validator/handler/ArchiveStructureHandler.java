package org.gbif.dwc.validator.handler;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.result.ValidationResult;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.dwc.validator.structure.evaluator.EMLEvaluator;
import org.gbif.dwc.validator.structure.evaluator.EMLEvaluator.EMLInvalidException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler responsible for the validation of the structure of the archive.
 * Structure of the archive represents the archive structure itself, the metadata and the EML file.
 * 
 * @author cgendreau
 */
public class ArchiveStructureHandler {

  /**
   * Inspect the content of the archive to ensure all needed files are present.
   * 
   * @param archive
   */
  public void inspectArchiveContent(Archive archive, ResultAccumulatorIF resultAccumulator) {

  }

  public void inspectEML(File eml, ResultAccumulatorIF resultAccumulator) {
    EMLEvaluator validator = new EMLEvaluator();
    try {
      validator.doEval(eml);
    } catch (EMLInvalidException e) {
      List<ValidationResultElement> list = new ArrayList<ValidationResultElement>();
      list.add(new ValidationResultElement(StructureValidationType.EML_SCHEMA, Result.ERROR,
        "Can't validate against schema (nom du schema): " + e.getMessage()));
      resultAccumulator.accumulate(new ValidationResult("EMLValidation", ValidationContext.STRUCTURE, list));
    }

  }

  public void inspectMetaXML(File metaXML, ResultAccumulatorIF resultAccumulator) {

  }
}
