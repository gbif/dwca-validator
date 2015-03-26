package org.gbif.dwc.validator;

import org.gbif.dwc.record.RecordIterator;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.gbif.dwc.validator.chain.EvaluatorChain;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.metadata.EMLCriterionBuilder;
import org.gbif.dwc.validator.criteria.metadata.MetaDescriptorCriterionBuilder;
import org.gbif.dwc.validator.criteria.metadata.MetadataCriterion;
import org.gbif.dwc.validator.exception.CriterionBuilderException;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;
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

  private final EvaluatorChain criteriaChain;

  DwcArchiveEvaluator(EvaluatorChain criteriaChain) {
    this.criteriaChain = criteriaChain;
  }

  /**
   * Create a ValidationResult object when it's not possible to open the archive.
   *
   * @param file
   * @param message
   * @return
   */
  private void recordCantOpenArchiveValidationResult(ResultAccumulator resultAccumulator, File file, String message) {
    ValidationResult vr =
      new ValidationResult(file.getName(), EvaluationContext.STRUCTURE, new ValidationResultElement("ArchiveStructure",
        StructureValidationType.ARCHIVE_STRUCTURE, Result.ERROR, ValidatorConfig.getLocalizedString(
          "evaluator.structure.archive.open", file.getAbsolutePath(), message)));

    try {
      resultAccumulator.accumulate(vr);
    } catch (ResultAccumulationException e) {
      LOGGER.error("Issue with the ResultAccumulator", e);
    }
  }

  public void setWorkingFolder(String workingFolder) {
    this.workingFolder = workingFolder;
  }

  @Override
  public void evaluateFile(File dwcaFile, ResultAccumulator resultAccumulator) {
    File dwcFolder = new File(new File(workingFolder), UUID.randomUUID().toString());
    boolean isGeneratedFolder = true;
    try {
      Archive dwc = null;
      if (dwcaFile.isFile()) {
        dwc = ArchiveFactory.openArchive(dwcaFile, dwcFolder);
      } else {
        dwc = ArchiveFactory.openArchive(dwcaFile);
        // use the already extracted folder, do not delete it.
        isGeneratedFolder = false;
        dwcFolder = dwcaFile;
      }

      File metaFile = new File(dwcFolder, META_XML_FILE);
      if (metaFile.exists()) {
        // inspectMetaXML(metaFile, resultAccumulator);
        System.out.println("meta.xml validation temporary suspended");
      }

      // Inspect the eml, if declared
      if (dwc.getMetadataLocation() != null) {
        // inspectEML(dwc.getMetadataLocationFile(), resultAccumulator);
        System.out.println("eml.xml validation temporary suspended");
      }

      // inspect core
      inspectDwcComponent(dwc.getCore(), EvaluationContext.CORE, criteriaChain, resultAccumulator);

      // inspect extensions
      Set<ArchiveFile> extensions = dwc.getExtensions();
      for (ArchiveFile currExt : extensions) {
        inspectDwcComponent(currExt, EvaluationContext.EXT, criteriaChain, resultAccumulator);
      }
      // we only call postIterate once, at the end
      criteriaChain.postIterate(resultAccumulator);

      criteriaChain.cleanup();
    } catch (UnsupportedArchiveException e) {
      LOGGER.error("Can't open archive", e);
      recordCantOpenArchiveValidationResult(resultAccumulator, dwcaFile, e.getMessage());
    } catch (IOException e) {
      LOGGER.error("Can't open archive", e);
      recordCantOpenArchiveValidationResult(resultAccumulator, dwcaFile, e.getMessage());
    } catch (ResultAccumulationException e) {
      LOGGER.error("Error while accumulating results", e);
    }

    if (isGeneratedFolder) {
      try {
        FileUtils.forceDelete(dwcFolder);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Inspect DarwinCore component record loop function.
   *
   * @param dwcaComponent
   * @param evaluationContext
   * @param evaluatorChain head of the evaluators chain
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  private void inspectDwcComponent(ArchiveFile dwcaComponent, EvaluationContext evaluationContext,
    EvaluatorChain evaluatorChain, ResultAccumulator resultAccumulator) throws ResultAccumulationException {

    // In theory, we could optimize the validation if we realize the coreId is a term used in the chain
    // but it could also be very error prone.
    // Term idTerm = dwcaComponent.getId().getTerm();

    RecordIterator recordIt = RecordIterator.build(dwcaComponent, false);
    while (recordIt.hasNext()) {
      evaluatorChain.evaluateRecord(recordIt.next(), evaluationContext, resultAccumulator);
    }
  }

  public void inspectEML(File emlFile, ResultAccumulator resultAccumulator) throws ResultAccumulationException,
  CriterionBuilderException {
    // when should we use GBIF profile vs regular profile? should we run both?
    MetadataCriterion criterion = EMLCriterionBuilder.builder().build();
    Optional<ValidationResult> result = criterion.validate(emlFile);
    if (result.isPresent()) {
      resultAccumulator.accumulate(result.get());
    }
  }

  public void inspectMetaXML(File metaXmlFile, ResultAccumulator resultAccumulator) throws ResultAccumulationException,
  CriterionBuilderException {

    MetadataCriterion criterion = MetaDescriptorCriterionBuilder.builder().build();
    Optional<ValidationResult> result = criterion.validate(metaXmlFile);
    if (result.isPresent()) {
      resultAccumulator.accumulate(result.get());
    }
  }
}
