package org.gbif.dwc.validator.impl;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.gbif.dwc.validator.ArchiveValidatorIF;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.handler.ArchiveContentHandler;
import org.gbif.dwc.validator.handler.ArchiveStructureHandler;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.validation.ValidationResult;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.result.type.StructureValidationType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main DarwinCore archive validation implementation.
 * TODO: change to immutable class
 * 
 * @author cgendreau
 */
public class ArchiveValidator implements ArchiveValidatorIF {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveValidator.class);
  private static final String META_XML_FILE = "meta.xml";

  private String workingFolder = ".";

  private ArchiveStructureHandler structureHandler;
  private ArchiveContentHandler contentHandler;

  /**
   * TODO this should be handled/recorded by the StructureHandler
   * 
   * @param file
   * @param message
   * @return
   */
  private ValidationResult createArchiveStructureValidationResult(File file, String message) {
    return new ValidationResult(file.getName(), "ArchiveStructure", EvaluationContext.STRUCTURE,
      new ValidationResultElement(StructureValidationType.ARCHIVE_STRUCTURE, Result.ERROR,
        ArchiveValidatorConfig.getLocalizedString("evaluator.structure.archive.open", file.getAbsolutePath(), message)));
  }

  public void setContentHandler(ArchiveContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  public void setStructureHandler(ArchiveStructureHandler structureHandler) {
    this.structureHandler = structureHandler;
  }

  public void setWorkingFolder(String workingFolder) {
    this.workingFolder = workingFolder;
  }

  @Override
  public void validateArchive(File dwcaFile, ResultAccumulatorIF resultAccumulator) {
    File dwcFolder = new File(new File(workingFolder), UUID.randomUUID().toString());

    try {
      Archive dwc = null;
      if (dwcaFile.isFile()) {
        dwc = ArchiveFactory.openArchive(dwcaFile, dwcFolder);
      } else {
        dwc = ArchiveFactory.openArchive(dwcaFile);
        dwcFolder = dwcaFile;
      }

      structureHandler.inspectArchiveContent(dwc, resultAccumulator);

      File metaFile = new File(dwcFolder, META_XML_FILE);
      if (metaFile.exists()) {
        structureHandler.inspectMetaXML(metaFile, resultAccumulator);
      }

      // Inspect the eml, if present
      if (dwc.getMetadataLocation() != null) {
        structureHandler.inspectEML(dwc.getMetadataLocationFile(), resultAccumulator);
      }

      // Inspect the core
      contentHandler.inspectCore(dwc.getCore(), resultAccumulator);

    } catch (UnsupportedArchiveException e) {
      LOGGER.error("Can't open archive", e);
      resultAccumulator.accumulate(createArchiveStructureValidationResult(dwcaFile, e.getMessage()));
    } catch (IOException e) {
      LOGGER.error("Can't open archive", e);
      resultAccumulator.accumulate(createArchiveStructureValidationResult(dwcaFile, e.getMessage()));
    }
  }
}
