package org.gbif.dwc.validator.evaluator;


import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator.ValueEvaluatorBuilder;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRule.InvalidCharacterEvaluationRuleBuilder;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test ValueEvaluator implementation
 * 
 * @author cgendreau
 */
public class ValueEvaluatorTest {

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
  public void testValueEvaluator() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    // register an InvalidCharacterEvaluationRule for scientificName
    ValueEvaluator valueEvaluator =
      ValueEvaluatorBuilder.create()
        .addRule(DwcTerm.scientificName, InvalidCharacterEvaluationRuleBuilder.create().build()).build();

    valueEvaluator.handleEval(buildMockRecord("1"), resultAccumulator);

    assertTrue(resultAccumulator.getEvaluationResultList().size() > 0);

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_VALUE));
  }

}
