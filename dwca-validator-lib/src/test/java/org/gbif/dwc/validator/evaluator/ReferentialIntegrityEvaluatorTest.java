package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.evaluator.integrity.ReferentialIntegrityEvaluator;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test ReferentialIntegrityEvaluator with mock records.
 * 
 * @author cgendreau
 */
public class ReferentialIntegrityEvaluatorTest {

  private Record buildMockRecord(String id, String acceptedNameUsageID) {
    // create a mock Record
    ArchiveField idField = new ArchiveField(0, DwcTerm.taxonID, null, DataType.string);
    ArchiveField acceptedNameUsageIDField = new ArchiveField(1, DwcTerm.acceptedNameUsageID, null, DataType.string);
    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(acceptedNameUsageIDField);

    RecordImpl testRecord = new RecordImpl(idField, fieldList, "rowType", false);
    testRecord.setRow(new String[] {id, acceptedNameUsageID});
    return testRecord;
  }

  /**
   * Get the reference file containing all the valid id for the tests.
   * 
   * @return
   */
  public File getReferenceFile() {
    File referenceFile = null;
    try {
      referenceFile =
        new File(this.getClass().getResource("/files/ReferentialIntegrityEvaluatorTest_referenceFile.txt").toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return referenceFile;
  }

  @Test
  public void referentialIntegrityEvaluatorCorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File referenceFile = getReferenceFile();

      ReferentialIntegrityEvaluator valueEvaluator =
        ReferentialIntegrityEvaluator.create(EvaluationContext.CORE, DwcTerm.acceptedNameUsageID)
          .referTo(EvaluationContext.CORE, DwcTerm.taxonID, referenceFile).build();

      valueEvaluator.handleEval(buildMockRecord("1", "4"));
      valueEvaluator.handleEval(buildMockRecord("2", "3"));
      // add blank acceptedNameUsageID, should be ignored.
      valueEvaluator.handleEval(buildMockRecord("2", ""));

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue(resultAccumulator.getValidationResultList().isEmpty());
  }

  @Test
  public void referentialIntegrityEvaluatorIncorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File referenceFile = getReferenceFile();

      ReferentialIntegrityEvaluator valueEvaluator =
        ReferentialIntegrityEvaluator.create(EvaluationContext.CORE, DwcTerm.acceptedNameUsageID)
          .referTo(EvaluationContext.CORE, DwcTerm.taxonID, referenceFile).build();
      valueEvaluator.handleEval(buildMockRecord("1", "4"));
      valueEvaluator.handleEval(buildMockRecord("2", "z"));

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals(1, resultAccumulator.getValidationResultList().size());
  }

  @Test
  public void referentialIntegrityEvaluatorMultipleCorrectId() {
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File referenceFile = getReferenceFile();

      // Test multiple id
      ReferentialIntegrityEvaluator valueEvaluator =
        ReferentialIntegrityEvaluator.create(EvaluationContext.CORE, DwcTerm.acceptedNameUsageID)
          .referTo(EvaluationContext.CORE, DwcTerm.taxonID, referenceFile).supportMultipleValues("|").build();
      valueEvaluator.handleEval(buildMockRecord("1", "3|4"));
      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue(resultAccumulator.getValidationResultList().isEmpty());
  }

  @Test
  public void referentialIntegrityEvaluatorMultipleIncorrectId() {
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File referenceFile = getReferenceFile();

      // Test multiple id
      ReferentialIntegrityEvaluator valueEvaluator =
        ReferentialIntegrityEvaluator.create(EvaluationContext.CORE, DwcTerm.acceptedNameUsageID)
          .referTo(EvaluationContext.CORE, DwcTerm.taxonID, referenceFile).supportMultipleValues("|").build();
      valueEvaluator.handleEval(buildMockRecord("1", "3|z"));
      valueEvaluator.handleEval(buildMockRecord("2", "z|3"));

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals(2, resultAccumulator.getValidationResultList().size());
  }
}
