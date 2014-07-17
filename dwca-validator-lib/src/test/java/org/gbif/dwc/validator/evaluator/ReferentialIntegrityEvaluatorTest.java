package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.evaluator.impl.ReferentialIntegrityEvaluator;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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

  @Test
  public void ReferentialIntegretyEvaluatorCorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File referenceFile =
        new File(this.getClass().getResource("/files/ReferentialIntegrityEvaluatorTest_referenceFile.txt").toURI());

      ReferentialIntegrityEvaluator valueEvaluator =
        ReferentialIntegrityEvaluator.create(ValidationContext.CORE, DwcTerm.acceptedNameUsageID)
          .referTo(ValidationContext.CORE, DwcTerm.taxonID, referenceFile).build();

      valueEvaluator.handleEval(buildMockRecord("1", "4"), resultAccumulator);
      valueEvaluator.handleEval(buildMockRecord("2", "3"), resultAccumulator);
      // add blank acceptedNameUsageID, should be ignored.
      valueEvaluator.handleEval(buildMockRecord("2", ""), resultAccumulator);

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.cleanup();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    assertTrue(resultAccumulator.getValidationResultsList().size() == 0);
  }

  @Test
  public void ReferentialIntegretyEvaluatorIncorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File referenceFile =
        new File(this.getClass().getResource("/files/ReferentialIntegrityEvaluatorTest_referenceFile.txt").toURI());

      ReferentialIntegrityEvaluator valueEvaluator =
        ReferentialIntegrityEvaluator.create(ValidationContext.CORE, DwcTerm.acceptedNameUsageID)
          .referTo(ValidationContext.CORE, DwcTerm.taxonID, referenceFile).build();
      valueEvaluator.handleEval(buildMockRecord("1", "4"), resultAccumulator);
      valueEvaluator.handleEval(buildMockRecord("2", "z"), resultAccumulator);

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.cleanup();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    assertTrue(resultAccumulator.getValidationResultsList().size() == 1);
  }
}
