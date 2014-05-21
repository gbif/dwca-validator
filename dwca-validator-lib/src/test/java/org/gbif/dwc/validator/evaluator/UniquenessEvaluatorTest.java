package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.evaluator.impl.UniquenessEvaluator;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test UniquenessEvaluator with mock records.
 * 
 * @author cgendreau
 */
public class UniquenessEvaluatorTest {

  private Record buildMockRecord(String id) {
    // create a mock Record
    ArchiveField idField = new ArchiveField(0, DwcTerm.occurrenceID, null, DataType.string);
    ArchiveField scientificNameField = new ArchiveField(1, DwcTerm.scientificName, null, DataType.string);
    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(scientificNameField);
    RecordImpl testRecord = new RecordImpl(idField, fieldList, "rowType", false);
    testRecord.setRow(new String[] {id, "gulo\tgulo"});
    return testRecord;
  }

  @Test
  public void testUniquenessEvaluatorNonUniqueId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      UniquenessEvaluator valueEvaluator = new UniquenessEvaluator();
      valueEvaluator.handleEval(buildMockRecord("1"), resultAccumulator);
      valueEvaluator.handleEval(buildMockRecord("1"), resultAccumulator);

      valueEvaluator.handlePostIterate(resultAccumulator);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertTrue(resultAccumulator.getValidationResultsList().size() > 0);
    assertEquals("1", resultAccumulator.getValidationResultsList().get(0).getId());
  }

  @Test
  public void testUniquenessEvaluatorWithUniqueId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      UniquenessEvaluator valueEvaluator = new UniquenessEvaluator();
      valueEvaluator.handleEval(buildMockRecord("1"), resultAccumulator);
      valueEvaluator.handleEval(buildMockRecord("2"), resultAccumulator);

      valueEvaluator.handlePostIterate(resultAccumulator);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertTrue(resultAccumulator.getValidationResultsList().isEmpty());
  }

}
