package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.evaluator.StatefulRecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluator;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ValidationResult;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.util.ToBeMovedFileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RecordEvaluatorIF implementation to check the integrity of field references.
 * This RecordEvaluator will only produce results on postIterate() call.
 * 
 * @author cgendreau
 */
@RecordEvaluator(key = "referentialIntegrityEvaluator")
public class ReferentialIntegrityEvaluator implements StatefulRecordEvaluatorIF {

  /**
   * Builder of ReferentialIntegrityEvaluator object.
   * Return ReferentialIntegrityEvaluator is NOT immutable due to file access.
   * 
   * @author cgendreau
   */
  public static class ReferentialIntegrityEvaluatorBuilder {

    private final String key = ReferentialIntegrityEvaluator.class.getAnnotation(RecordEvaluator.class).key();
    private final EvaluationContext evaluatorContext;
    private final ConceptTerm term;

    // for future use
    private EvaluationContext referedEvaluatorContext;
    private ConceptTerm referredTerm;
    private File referenceFile;
    private String multipleValuesSeparator = null;

    private File workingFolder;

    private ReferentialIntegrityEvaluatorBuilder(EvaluationContext evaluatorContext, ConceptTerm term) {
      this.evaluatorContext = evaluatorContext;
      this.term = term;
    }

    public static ReferentialIntegrityEvaluatorBuilder create(EvaluationContext evaluatorContext, ConceptTerm term) {
      return new ReferentialIntegrityEvaluatorBuilder(evaluatorContext, term);
    }

    public ReferentialIntegrityEvaluator build() throws IOException, IllegalStateException {
      if (evaluatorContext == null || referredTerm == null || referenceFile == null) {
        throw new IllegalStateException("The reference data must be set");
      }

      if (workingFolder != null) {
        if (!workingFolder.exists() || !workingFolder.isDirectory()) {
          throw new IllegalStateException("workingFolder must exist as a directory");
        }
      } else {
        workingFolder = new File(".");
      }

      return new ReferentialIntegrityEvaluator(key, evaluatorContext, term, multipleValuesSeparator, referredTerm,
        referenceFile, workingFolder);
    }

    public ReferentialIntegrityEvaluatorBuilder referTo(EvaluationContext referedEvaluatorContext,
      ConceptTerm referredTerm, File referenceFile) {
      this.referedEvaluatorContext = referedEvaluatorContext;
      this.referredTerm = referredTerm;
      this.referenceFile = referenceFile;
      return this;
    }

    /**
     * Should the evaluator accept multiple values using a defined separator.
     * e.g. 1234|2345
     * 
     * @param separator
     * @return
     */
    public ReferentialIntegrityEvaluatorBuilder supportMultipleValues(String separator) {
      this.multipleValuesSeparator = separator;
      return this;
    }

    /**
     * Set working folder to save temporary files.
     * 
     * @param workingFolder
     * @return
     */
    public ReferentialIntegrityEvaluatorBuilder workingFolder(File workingFolder) {
      this.workingFolder = workingFolder;
      return this;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferentialIntegrityEvaluator.class);
  org.gbif.utils.file.FileUtils GBIF_FILE_UTILS = new org.gbif.utils.file.FileUtils();
  private static final int BUFFER_THRESHOLD = 1000;

  private final String key;
  private final EvaluationContext evaluatorContext;
  private final ConceptTerm term;
  private final ConceptTerm referredTerm;
  private final File referenceFile;
  private final String multipleValuesSeparator;

  private final List<String> idList;

  private final FileWriter fw;

  private final File idRecordingFile;
  private final File sortedIdFile;
  private final File diffFile;

  /**
   * @param key
   * @param evaluatorContext Context of the term, core or extension. Should be more precise in the future about the
   *        extension.
   * @param term provided ConceptTerm to evaluate
   * @param multipleValuesSeparator separator used for multiple values or null
   * @param referredTerm ConceptTerm that the provided term should refer to.
   * @param referenceFile
   * @param workingFolder parent folder for generated files
   * @throws IOException
   */
  private ReferentialIntegrityEvaluator(String key, EvaluationContext evaluatorContext, ConceptTerm term,
    String multipleValuesSeparator, ConceptTerm referredTerm, File referenceFile, File workingFolder)
    throws IOException {

    this.key = key;
    this.evaluatorContext = evaluatorContext;
    this.term = term;
    this.multipleValuesSeparator = multipleValuesSeparator;
    this.referredTerm = referredTerm;
    this.referenceFile = referenceFile;
    idList = new ArrayList<String>(BUFFER_THRESHOLD);

    String randomUUID = UUID.randomUUID().toString();
    String fileName = randomUUID + ArchiveValidatorConfig.TEXT_FILE_EXT;
    String sortedFileName = randomUUID + "_sorted" + ArchiveValidatorConfig.TEXT_FILE_EXT;
    String diffFileName = randomUUID + "_diff" + ArchiveValidatorConfig.TEXT_FILE_EXT;

    idRecordingFile = new File(workingFolder, fileName);
    sortedIdFile = new File(workingFolder, sortedFileName);
    diffFile = new File(workingFolder, diffFileName);
    fw = new FileWriter(idRecordingFile);
  }

  public static ReferentialIntegrityEvaluatorBuilder create(EvaluationContext evaluatorContext, ConceptTerm term) {
    return new ReferentialIntegrityEvaluatorBuilder(evaluatorContext, term);
  }

  /**
   * Clean generated files
   */
  @Override
  public void cleanup() {
    idRecordingFile.delete();
    sortedIdFile.delete();
    diffFile.delete();
  }

  private void flushCurrentIdList() {
    try {
      for (String curr : idList) {
        fw.write(curr + ArchiveValidatorConfig.ENDLINE);
      }
      fw.flush();
    } catch (IOException ioEx) {
      LOGGER.error("Can't write to file using FileWriter", ioEx);
    }
    idList.clear();
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public void handleEval(Record record, ResultAccumulatorIF resultAccumulator) {
    String value = record.value(term);

    // only record non-blank value
    if (StringUtils.isNotBlank(value)) {
      if (multipleValuesSeparator == null || !value.contains(multipleValuesSeparator)) {
        idList.add(value);
      } else {
        for (String currValue : StringUtils.split(value, multipleValuesSeparator)) {
          idList.add(currValue);
        }
      }
      if (idList.size() >= BUFFER_THRESHOLD) {
        flushCurrentIdList();
      }
    }
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {

    flushCurrentIdList();
    // close FileWriter
    try {
      fw.close();
    } catch (IOException ioEx) {
      LOGGER.error("Can't close ReferentialIntegrityEvaluator FileWriter properly", ioEx);
    }

    // sort the id file
    try {
      GBIF_FILE_UTILS.sort(idRecordingFile, sortedIdFile, Charsets.UTF_8.toString(), 0, null, null,
        ArchiveValidatorConfig.ENDLINE, 0);
    } catch (IOException ioEx) {
      LOGGER.error("Can't sort id file", ioEx);
    }

    // diff the files
    ToBeMovedFileUtils tbmFu = new ToBeMovedFileUtils();
    // TODO, use diffFile to allow usage in non-Unix environment
    try {
      tbmFu.diffFileInUnix(referenceFile, sortedIdFile, diffFile);
    } catch (IOException ioEx) {
      LOGGER.error("diffFileInUnix error", ioEx);
    }

    // search for broken links
    BufferedReader br = null;
    try {
      String currentLine;
      br = new BufferedReader(new FileReader(diffFile));
      ValidationResultElement validationResultElement = null;
      while ((currentLine = br.readLine()) != null) {
        validationResultElement =
          new ValidationResultElement(ContentValidationType.FIELD_REFERENTIAL_INTEGRITY, Result.ERROR,
            ArchiveValidatorConfig.getLocalizedString("evaluator.referential_integrity", currentLine, term,
              referredTerm));
        resultAccumulator.accumulate(new ValidationResult(currentLine, key, evaluatorContext, validationResultElement));
      }
    } catch (IOException ioEx) {
      LOGGER.error("Can't sort id file", ioEx);
    } finally {
      try {
        if (br != null)
          br.close();
      } catch (IOException ioEx) {
        LOGGER.error("Can't close BufferedReader", ioEx);
      }
    }
  }

}
