package org.gbif.dwc.validator.evaluator.integrity;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.evaluator.StatefulRecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.evaluator.configuration.ReferenceUniqueEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.util.ToBeMovedFileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RecordEvaluator implementation to check the integrity of field references that should point to a unique value.
 * This implementation is composed by a UniquenessEvaluator.
 * This RecordEvaluator will only produce results on postIterate() call.
 * 
 * @author cgendreau
 */
@RecordEvaluatorKey(key = "referentialIntegrityEvaluator")
class ReferenceUniqueEvaluator implements StatefulRecordEvaluator {

  private final String key = ReferenceUniqueEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceUniqueEvaluator.class);
  org.gbif.utils.file.FileUtils GBIF_FILE_UTILS = new org.gbif.utils.file.FileUtils();
  private final TermFactory TERM_FACTORY = new TermFactory();
  private static final int BUFFER_THRESHOLD = 1000;

  private static final String SORTED_FILE_SUFFIX = "_sorted" + ValidatorConfig.TEXT_FILE_EXT;
  private static final String DIFF_FILE_SUFFIX = "_diff" + ValidatorConfig.TEXT_FILE_EXT;

  private final EvaluationContext evaluationContextRestriction;
  private final String rowTypeRestriction;
  private final ConceptTerm term;

  private final UniquenessEvaluator uniquenessEvaluator;

  private final String multipleValuesSeparator;

  private final File workingFolder;
  private final String randomUUID;

  private final Map<String, List<String>> valuePerRowType;
  private final Map<String, FileWriter> fileWriterPerRowType;
  private final Map<String, File> valueFilePerRowType;

  private final List<File> filesCreated;

  /**
   * @param configuration
   * @param uniquenessEvaluator configured on the term we want to check the referential integrity on.
   * @throws IOException
   */
  ReferenceUniqueEvaluator(ReferenceUniqueEvaluatorConfiguration configuration, UniquenessEvaluator uniquenessEvaluator) {

    this.evaluationContextRestriction = configuration.getEvaluationContextRestriction();
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.term = configuration.getTerm();

    this.uniquenessEvaluator = uniquenessEvaluator;

    this.multipleValuesSeparator = configuration.getMultipleValuesSeparator();

    this.randomUUID = UUID.randomUUID().toString();
    this.workingFolder = configuration.getWorkingFolder();
    this.filesCreated = new ArrayList<File>();

    this.valuePerRowType = new HashMap<String, List<String>>();
    this.fileWriterPerRowType = new HashMap<String, FileWriter>();
    this.valueFilePerRowType = new HashMap<String, File>();
  }

  /**
   * Flush the provided valueList into the provided FileWriter and clear the list.
   * 
   * @param valueList
   * @param fw
   * @throws IOException
   */
  private void flushValueList(List<String> valueList, FileWriter fw) throws IOException {

    for (String curr : valueList) {
      fw.write(curr + ValidatorConfig.ENDLINE);
    }
    fw.flush();

    valueList.clear();
  }

  /**
   * Ensure the provided maps are ready to deal with the provided rowType.
   * 
   * @param rowType
   * @param valuePerRowType
   * @param fileWriterPerRowType
   */
  private void ensureReadyForRowType(String rowType, Map<String, List<String>> valuePerRowType,
    Map<String, File> valueFilePerRowType, Map<String, FileWriter> fileWriterPerRowType) throws IOException {
    if (valuePerRowType.get(rowType) == null) {
      valuePerRowType.put(rowType, new ArrayList<String>(BUFFER_THRESHOLD));
    }

    if (fileWriterPerRowType.get(rowType) == null) {

      ConceptTerm ct = TERM_FACTORY.findTerm(rowType);
      String fileName = randomUUID + "_" + ct.simpleName() + ValidatorConfig.TEXT_FILE_EXT;
      File valueRecordingFile = new File(workingFolder, fileName);
      fileWriterPerRowType.put(rowType, new FileWriter(valueRecordingFile));
      valueFilePerRowType.put(rowType, valueRecordingFile);

      filesCreated.add(valueRecordingFile);
    }
  }

  /**
   * Record in resultAccumulator all broken links (if any).
   * 
   * @param rowType
   * @param diffFile
   * @param resultAccumulator
   */
  private void recordBrokenLinks(String rowType, File diffFile, ResultAccumulatorIF resultAccumulator) {

    BufferedReader br = null;
    try {
      String currentLine;
      String termString = (term != null ? term.toString() : ValidatorConfig.CORE_ID);
      String referedTermString =
        (uniquenessEvaluator.getTerm() != null ? uniquenessEvaluator.getTerm().toString() : ValidatorConfig.CORE_ID);

      br = new BufferedReader(new FileReader(diffFile));
      ValidationResultElement validationResultElement = null;
      while ((currentLine = br.readLine()) != null) {
        validationResultElement =
          new ValidationResultElement(ContentValidationType.FIELD_REFERENTIAL_INTEGRITY, Result.ERROR,
            ValidatorConfig.getLocalizedString("evaluator.referential_integrity", currentLine, termString,
              referedTermString));
        resultAccumulator.accumulate(new ValidationResult(currentLine, key, evaluationContextRestriction, rowType,
          validationResultElement));
      }
    } catch (IOException ioEx) {
      LOGGER.error("Can't sort id file", ioEx);
    } finally {
      IOUtils.closeQuietly(br);
    }
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleEval(Record record, EvaluationContext evaluationContext) {

    // always send to our composed RecordEvaluator
    uniquenessEvaluator.handleEval(record, evaluationContext);

    String currentRowType = record.rowType();

    // check that the record is in the right evaluation context
    if (evaluationContext != evaluationContextRestriction) {
      return Optional.absent();
    }

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(currentRowType)) {
      return Optional.absent();
    }

    String value;
    if (term == null) {
      value = record.id();
    } else {
      value = record.value(term);
    }

    // only record non-blank value
    if (StringUtils.isNotBlank(value)) {
      try {
        ensureReadyForRowType(currentRowType, valuePerRowType, valueFilePerRowType, fileWriterPerRowType);

        List<String> valueList = valuePerRowType.get(currentRowType);
        if (multipleValuesSeparator == null || !value.contains(multipleValuesSeparator)) {
          valueList.add(value);
        } else {
          for (String currValue : StringUtils.split(value, multipleValuesSeparator)) {
            valueList.add(currValue);
          }
        }
        if (valueList.size() >= BUFFER_THRESHOLD) {
          flushValueList(valueList, fileWriterPerRowType.get(currentRowType));
        }
      } catch (IOException ioEx) {
        LOGGER.error("Can't write to file using FileWriter", ioEx);
      }
    }
    return Optional.absent();
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    String sortedFileName, diffFileName;
    File sortedValueFile, diffFile;
    ToBeMovedFileUtils tbmFu = new ToBeMovedFileUtils();

    // call our composed RecordEvaluator first
    uniquenessEvaluator.handlePostIterate(resultAccumulator);

    // use the UniquenessEvaluator sorted values file as reference file
    // this file could contains duplicates and the UniquenessEvaluator is responsible to flag them.
    File referenceFile = uniquenessEvaluator.getSortedValueFile();

    try {
      // flush and close all resources
      for (String currRowType : valuePerRowType.keySet()) {
        flushValueList(valuePerRowType.get(currRowType), fileWriterPerRowType.get(currRowType));
        fileWriterPerRowType.get(currRowType).close();
        ConceptTerm ct = TERM_FACTORY.findTerm(currRowType);
        sortedFileName = randomUUID + "_" + ct.simpleName() + SORTED_FILE_SUFFIX;
        diffFileName = randomUUID + "_" + ct.simpleName() + DIFF_FILE_SUFFIX;

        sortedValueFile = new File(workingFolder, sortedFileName);
        diffFile = new File(workingFolder, diffFileName);
        // remember them so we can delete them
        filesCreated.add(sortedValueFile);
        filesCreated.add(diffFile);

        // sort the recorded values
        GBIF_FILE_UTILS.sort(valueFilePerRowType.get(currRowType), sortedValueFile, Charsets.UTF_8.toString(), 0, null,
          null, ValidatorConfig.ENDLINE, 0);

        tbmFu.diffFileInUnix(referenceFile, sortedValueFile, diffFile);

        recordBrokenLinks(currRowType, diffFile, resultAccumulator);
      }
    } catch (IOException ioEx) {
      LOGGER.error("IO issue", ioEx);
    }

  }

  /**
   * Clean generated files
   */
  @Override
  public void close() throws IOException {
    uniquenessEvaluator.close();

    // delete all created files
    for (File currFile : filesCreated) {
      currFile.delete();
    }
  }
}
