package org.gbif.dwc.validator.criteria.dataset;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.UniquenessCriterionConfiguration;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

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
 * DatasetCriteria implementation to check the uniqueness of specific fields.
 * This implementation will write a new file with all the id and then sort it using org.gbif.utils.file.FileUtils.
 * GBIF FileUtils can also sort directly on the archive file, it may be a better solution than writing a new
 * file containing all the id but referential integrity check needs the resulting file.
 * NOT thread-safe
 * 
 * @author cgendreau
 */
@RecordCriterionKey(key = "uniquenessCriterion")
class UniquenessCriterion implements DatasetCriterion {

  private final String key = UniquenessCriterion.class.getAnnotation(RecordCriterionKey.class).key();
  private final EvaluationContext evaluationContextRestriction;
  private final String rowTypeRestriction;
  private final Term term;
  private final String conceptTermString;

  private static final Logger LOGGER = LoggerFactory.getLogger(UniquenessCriterion.class);
  org.gbif.utils.file.FileUtils GBIF_FILE_UTILS = new org.gbif.utils.file.FileUtils();
  private static final int BUFFER_THRESHOLD = 1000;

  private final List<String> idList;
  private final FileWriter fw;

  private final File valueRecordingFile;
  private final File sortedValueFile;

  /**
   * @param UniquenessCriterionConfiguration
   * @throws IOException
   */
  UniquenessCriterion(UniquenessCriterionConfiguration configuration) throws IOException {
    this.evaluationContextRestriction = configuration.getEvaluationContextRestriction();
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.term = configuration.getTerm();
    this.conceptTermString = term != null ? term.simpleName() : "coreId";

    idList = new ArrayList<String>(BUFFER_THRESHOLD);
    String randomUUID = UUID.randomUUID().toString();
    String fileName = randomUUID + ValidatorConfig.TEXT_FILE_EXT;
    String sortedFileName = randomUUID + "_sorted" + ValidatorConfig.TEXT_FILE_EXT;

    valueRecordingFile = new File(configuration.getWorkingFolder(), fileName);
    sortedValueFile = new File(configuration.getWorkingFolder(), sortedFileName);
    fw = new FileWriter(valueRecordingFile);
  }

  private void flushCurrentIdList() {
    try {
      for (String curr : idList) {
        fw.write(curr + ValidatorConfig.ENDLINE);
      }
      fw.flush();
    } catch (IOException ioEx) {
      LOGGER.error("Can't write to file using FileWriter", ioEx);
    }
    idList.clear();
  }

  @Override
  public String getCriteriaKey() {
    return key;
  }

  /**
   * Returns the file used(or to be used) to store the sorted record value.
   * The file may or may not exist yet.
   * 
   * @return
   */
  File getSortedValueFile() {
    return sortedValueFile;
  }

  /**
   * Get term on which the uniqueness evaluation is performed.
   * 
   * @return
   */
  Term getTerm() {
    return term;
  }

  /**
   * Record each fields that shall be unique.
   */
  @Override
  public void onRecord(Record record, EvaluationContext evaluationContext) {

    // check that the record is in the right evaluation context
    if (evaluationContext != evaluationContextRestriction) {
      return;
    }

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(record.rowType())) {
      return;
    }

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
  public void validateDataset(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    flushCurrentIdList();

    try {
      fw.close();
    } catch (IOException ioEx) {
      LOGGER.error("Can't close UniquenessEvaluator FileWriter properly", ioEx);
    }

    // sort the file containing the value
    try {
      GBIF_FILE_UTILS.sort(valueRecordingFile, sortedValueFile, Charsets.UTF_8.toString(), 0, null, null,
        ValidatorConfig.ENDLINE, 0);
    } catch (IOException ioEx) {
      LOGGER.error("Can't sort id file", ioEx);
    }

    // search for duplicates
    BufferedReader br = null;
    try {
      String previousLine = null;
      String currentLine, displayValue;

      br = new BufferedReader(new FileReader(sortedValueFile));
      ValidationResultElement validationResultElement = null;
      while ((currentLine = br.readLine()) != null) {
        if (previousLine != null && previousLine.equalsIgnoreCase(currentLine)) {

          displayValue = StringUtils.isBlank(currentLine) ? ValidatorConfig.EMPTY_STRING_FOR_DISPLAY : currentLine;

          validationResultElement =
            new ValidationResultElement(key, ContentValidationType.FIELD_UNIQUENESS, Result.ERROR,
              ValidatorConfig.getLocalizedString("criterion.uniqueness_criterion.not_unique", displayValue,
                conceptTermString));
          resultAccumulator.accumulate(new ValidationResult(displayValue, evaluationContextRestriction, StringUtils
            .defaultString(rowTypeRestriction), validationResultElement));
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

  /**
   * Delete generated files.
   */
  @Override
  public void close() throws IOException {
    valueRecordingFile.delete();
    sortedValueFile.delete();
  }

}
