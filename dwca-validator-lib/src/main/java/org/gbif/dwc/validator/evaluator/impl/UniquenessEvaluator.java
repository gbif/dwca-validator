package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.evaluator.StatefulRecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluator;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RecordEvaluatorIF implementation to check the uniqueness of specific fields.
 * This RecordEvaluator will only produce results on postIterate() call.
 * This implementation will write a new file with all the id and then sort it using org.gbif.utils.file.FileUtils.
 * GBIF FileUtils can also sort directly on the archive file, it may be a better solution than writing a new
 * file containing all the id but referential integrity check needs the resulting file.
 * 
 * @author cgendreau
 */
@RecordEvaluator(key = "uniquenessEvaluator")
public class UniquenessEvaluator implements StatefulRecordEvaluatorIF {

  /**
   * Builder of UniquenessEvaluator object.
   * Return UniquenessEvaluator is NOT totally immutable due to file access.
   * 
   * @author cgendreau
   */
  public static class UniquenessEvaluatorBuilder {

    private final String key = UniquenessEvaluator.class.getAnnotation(RecordEvaluator.class).key();
    private EvaluationContext evaluatorContext;
    private ConceptTerm term;
    private File workingFolder;

    private UniquenessEvaluatorBuilder(EvaluationContext evaluatorContext) {
      this.evaluatorContext = evaluatorContext;
    }

    /**
     * Create with default value. Using coreId, ValidationContext.CORE
     * 
     * @return
     */
    public static UniquenessEvaluatorBuilder create() {
      return new UniquenessEvaluatorBuilder(EvaluationContext.CORE);
    }

    public UniquenessEvaluator build() throws IOException, IllegalStateException {
      if (evaluatorContext == null) {
        throw new IllegalStateException("The evaluatorContext must be set");
      }

      if (workingFolder != null) {
        if (!workingFolder.exists() || !workingFolder.isDirectory()) {
          throw new IllegalStateException("workingFolder must exist as a directory");
        }
      } else {
        workingFolder = new File(".");
      }

      return new UniquenessEvaluator(key, term, evaluatorContext, workingFolder);
    }

    /**
     * Set on which ConceptTerm the evaluation should be made.
     * Override default values.
     * 
     * @param term
     * @param evaluatorContext context of the provided term
     * @return
     */
    public UniquenessEvaluatorBuilder on(ConceptTerm term, EvaluationContext evaluatorContext) {
      this.evaluatorContext = evaluatorContext;
      this.term = term;
      return this;
    }

    /**
     * Set working folder to save temporary files.
     * 
     * @param workingFolder
     * @return
     */
    public UniquenessEvaluatorBuilder workingFolder(File workingFolder) {
      this.workingFolder = workingFolder;
      return this;
    }
  }

  private final String key;
  private final EvaluationContext evaluatorContext;
  private final ConceptTerm term;
  private final String conceptTermString;

  private static final Logger LOGGER = LoggerFactory.getLogger(UniquenessEvaluator.class);
  org.gbif.utils.file.FileUtils GBIF_FILE_UTILS = new org.gbif.utils.file.FileUtils();
  private static final int BUFFER_THRESHOLD = 1000;

  private final List<String> idList;
  private final FileWriter fw;

  private final File idRecordingFile;
  private final File sortedIdFile;

  /**
   * @param evaluatorContext
   * @param term id term is null, the coreId will be used
   * @param workingFolder place to save temporary files
   * @throws IOException
   */
  private UniquenessEvaluator(String key, ConceptTerm term, EvaluationContext evaluatorContext, File workingFolder)
    throws IOException {
    this.key = key;
    this.evaluatorContext = evaluatorContext;
    this.term = term;
    this.conceptTermString = term != null ? term.simpleName() : "coreId";

    idList = new ArrayList<String>(BUFFER_THRESHOLD);
    String randomUUID = UUID.randomUUID().toString();
    String fileName = randomUUID + ArchiveValidatorConfig.TEXT_FILE_EXT;
    String sortedFileName = randomUUID + "_sorted" + ArchiveValidatorConfig.TEXT_FILE_EXT;

    idRecordingFile = new File(workingFolder, fileName);
    sortedIdFile = new File(workingFolder, sortedFileName);
    fw = new FileWriter(idRecordingFile);
  }

  public static UniquenessEvaluatorBuilder create() {
    return UniquenessEvaluatorBuilder.create();
  }

  /**
   * Delete generated files.
   */
  @Override
  public void cleanup() {
    idRecordingFile.delete();
    sortedIdFile.delete();
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

  /**
   * Returns the file used(or to be used) to store the sorted ID.
   * The file may or may not exist yet.
   * 
   * @return
   */
  public File getSortedIdFile() {
    return sortedIdFile;
  }

  /**
   * Record each fields that shall be unique.
   */
  @Override
  public void handleEval(Record record, ResultAccumulatorIF resultAccumulator) {

    if (term == null) {
      idList.add(record.id());
    } else {
      idList.add(record.value(term));
    }

    if (idList.size() >= BUFFER_THRESHOLD) {
      flushCurrentIdList();
    }
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    flushCurrentIdList();

    try {
      fw.close();
    } catch (IOException ioEx) {
      LOGGER.error("Can't close UniquenessEvaluator FileWriter properly", ioEx);
    }

    // sort the file containing the id
    try {
      GBIF_FILE_UTILS.sort(idRecordingFile, sortedIdFile, Charsets.UTF_8.toString(), 0, null, null,
        ArchiveValidatorConfig.ENDLINE, 0);
    } catch (IOException ioEx) {
      LOGGER.error("Can't sort id file", ioEx);
    }

    // search for duplicates
    BufferedReader br = null;
    try {
      String previousLine = "";
      String currentLine;

      br = new BufferedReader(new FileReader(sortedIdFile));
      ValidationResultElement validationResultElement = null;
      while ((currentLine = br.readLine()) != null) {
        if (previousLine.equalsIgnoreCase(currentLine)) {
          validationResultElement =
            new ValidationResultElement(ContentValidationType.FIELD_UNIQUENESS, Result.ERROR,
              ArchiveValidatorConfig.getLocalizedString("evaluator.uniqueness", currentLine, conceptTermString));
          resultAccumulator
            .accumulate(new EvaluationResult(currentLine, key, evaluatorContext, validationResultElement));
        }
        previousLine = currentLine;
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
