package org.gbif.dwc.validator.evaluator.integrity;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
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
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
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
public class ReferenceUniqueEvaluator implements StatefulRecordEvaluator {

  private final String key = ReferenceUniqueEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceUniqueEvaluator.class);
  org.gbif.utils.file.FileUtils GBIF_FILE_UTILS = new org.gbif.utils.file.FileUtils();
  private static final int BUFFER_THRESHOLD = 1000;

  private final EvaluationContext evaluationContextRestriction;
  private final String rowTypeRestriction;
  private final ConceptTerm term;

  private final UniquenessEvaluator uniquenessEvaluator;

  private final String multipleValuesSeparator;

  private final List<String> idList;

  private final FileWriter fw;

  private final File valueRecordingFile;
  private final File sortedValueFile;
  private final File diffFile;

  /**
   * @param configuration
   * @param uniquenessEvaluator configured on the term we want to check the referential integrity on.
   * @throws IOException
   */
  ReferenceUniqueEvaluator(ReferenceUniqueEvaluatorConfiguration configuration, UniquenessEvaluator uniquenessEvaluator)
    throws IOException {

    this.evaluationContextRestriction = configuration.getEvaluationContextRestriction();
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.term = configuration.getTerm();

    this.uniquenessEvaluator = uniquenessEvaluator;

    this.multipleValuesSeparator = configuration.getMultipleValuesSeparator();

    idList = new ArrayList<String>(BUFFER_THRESHOLD);

    String randomUUID = UUID.randomUUID().toString();
    String fileName = randomUUID + ValidatorConfig.TEXT_FILE_EXT;
    String sortedFileName = randomUUID + "_sorted" + ValidatorConfig.TEXT_FILE_EXT;
    String diffFileName = randomUUID + "_diff" + ValidatorConfig.TEXT_FILE_EXT;

    valueRecordingFile = new File(configuration.getWorkingFolder(), fileName);
    sortedValueFile = new File(configuration.getWorkingFolder(), sortedFileName);
    diffFile = new File(configuration.getWorkingFolder(), diffFileName);
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
  public String getKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleEval(Record record, EvaluationContext evaluationContext) {

    // always send to our composed RecordEvaluator
    uniquenessEvaluator.handleEval(record, evaluationContext);

    // check that the record is in the right evaluation context
    if (evaluationContext != evaluationContextRestriction) {
      Optional.absent();
    }

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(record.rowType())) {
      Optional.absent();
    }

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
    return Optional.absent();
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {

    // call our composed RecordEvaluator first
    uniquenessEvaluator.handlePostIterate(resultAccumulator);

    // use the UniquenessEvaluator sorted values file as reference file
    // this file could contains duplicates and the UniquenessEvaluator is responsible to flag them.
    File referenceFile = uniquenessEvaluator.getSortedValueFile();
    ConceptTerm referredTerm = uniquenessEvaluator.getTerm();

    flushCurrentIdList();
    // close FileWriter
    try {
      fw.close();
    } catch (IOException ioEx) {
      LOGGER.error("Can't close ReferentialIntegrityEvaluator FileWriter properly", ioEx);
    }

    // sort the recorded values
    try {
      GBIF_FILE_UTILS.sort(valueRecordingFile, sortedValueFile, Charsets.UTF_8.toString(), 0, null, null,
        ValidatorConfig.ENDLINE, 0);
    } catch (IOException ioEx) {
      LOGGER.error("Can't sort id file", ioEx);
    }

    // diff the files
    ToBeMovedFileUtils tbmFu = new ToBeMovedFileUtils();
    // TODO, use diffFile to allow usage in non-Unix environment
    try {
      tbmFu.diffFileInUnix(referenceFile, sortedValueFile, diffFile);
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
            ValidatorConfig.getLocalizedString("evaluator.referential_integrity", currentLine, term, referredTerm));
        resultAccumulator.accumulate(new ValidationResult(currentLine, key, evaluationContextRestriction,
          validationResultElement));
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
   * Clean generated files
   */
  @Override
  public void close() throws IOException {
    uniquenessEvaluator.close();

    valueRecordingFile.delete();
    sortedValueFile.delete();
    diffFile.delete();
  }
}
