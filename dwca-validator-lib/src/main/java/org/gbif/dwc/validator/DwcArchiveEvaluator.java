package org.gbif.dwc.validator;

import org.gbif.dwc.record.RecordIterator;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;
import org.gbif.dwc.validator.evaluator.structure.EMLEvaluator;
import org.gbif.dwc.validator.evaluator.structure.MetaDescriptorEvaluator;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main DarwinCore archive validation implementation.
 * 
 * @author cgendreau
 */
public class DwcArchiveEvaluator implements FileEvaluator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DwcArchiveEvaluator.class);
  private static final String META_XML_FILE = "meta.xml";

  private String workingFolder = ".";

  private final ChainableRecordEvaluator evaluationChainHead;

  DwcArchiveEvaluator(ChainableRecordEvaluator evaluationChainHead) {
    this.evaluationChainHead = evaluationChainHead;
  }

  /**
   * Create a ValidationResult object when it's not possible to open the archive.
   * 
   * @param file
   * @param message
   * @return
   */
  private ValidationResult createCantOpenArchiveValidationResult(File file, String message) {
    return new ValidationResult(file.getName(), "ArchiveStructure", EvaluationContext.STRUCTURE,
      new ValidationResultElement(StructureValidationType.ARCHIVE_STRUCTURE, Result.ERROR,
        ArchiveValidatorConfig.getLocalizedString("evaluator.structure.archive.open", file.getAbsolutePath(), message)));
  }

  public void setWorkingFolder(String workingFolder) {
    this.workingFolder = workingFolder;
  }

  @Override
  public void evaluateFile(File dwcaFile, ResultAccumulatorIF resultAccumulator) {
    File dwcFolder = new File(new File(workingFolder), UUID.randomUUID().toString());

    try {
      Archive dwc = null;
      if (dwcaFile.isFile()) {
        dwc = ArchiveFactory.openArchive(dwcaFile, dwcFolder);
      } else {
        dwc = ArchiveFactory.openArchive(dwcaFile);
        dwcFolder = dwcaFile;
      }

      File metaFile = new File(dwcFolder, META_XML_FILE);
      if (metaFile.exists()) {
        inspectMetaXML(metaFile, resultAccumulator);
      }

      // Inspect the eml, if declared
      if (dwc.getMetadataLocation() != null) {
        inspectEML(dwc.getMetadataLocationFile(), resultAccumulator);
      }

      // Inspect the core
      inspectDwcComponent(dwc.getCore(), evaluationChainHead, resultAccumulator);

      // TODO extensions

    } catch (UnsupportedArchiveException e) {
      LOGGER.error("Can't open archive", e);
      resultAccumulator.accumulate(createCantOpenArchiveValidationResult(dwcaFile, e.getMessage()));
    } catch (IOException e) {
      LOGGER.error("Can't open archive", e);
      resultAccumulator.accumulate(createCantOpenArchiveValidationResult(dwcaFile, e.getMessage()));
    }

    resultAccumulator.close();
  }

  /**
   * Inspect DarwinCore component record loop function.
   * 
   * @param dwcaComponent
   * @param evaluatorChain head of the evaluators chain
   * @param resultAccumulator
   */
  private void inspectDwcComponent(ArchiveFile dwcaComponent, ChainableRecordEvaluator evaluatorChain,
    ResultAccumulatorIF resultAccumulator) {
    RecordIterator recordIt = RecordIterator.build(dwcaComponent, false);
    while (recordIt.hasNext()) {
      evaluatorChain.doEval(recordIt.next(), resultAccumulator);
    }
    evaluatorChain.postIterate(resultAccumulator);

    evaluatorChain.cleanup();
  }

  public void inspectEML(File eml, ResultAccumulatorIF resultAccumulator) {
    EMLEvaluator validator = new EMLEvaluator();
    validator.doEval(eml, resultAccumulator);
  }

  public void inspectMetaXML(File metaXML, ResultAccumulatorIF resultAccumulator) {
    MetaDescriptorEvaluator validator = new MetaDescriptorEvaluator();
    validator.doEval(metaXML, resultAccumulator);
  }
}
