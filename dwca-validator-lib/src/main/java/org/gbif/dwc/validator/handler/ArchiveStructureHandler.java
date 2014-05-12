package org.gbif.dwc.validator.handler;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.structure.evaluator.EMLEvaluator;

import java.io.File;

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
    validator.doEval(eml);

  }

  public void inspectMetaXML(File metaXML, ResultAccumulatorIF resultAccumulator) {

  }
}
