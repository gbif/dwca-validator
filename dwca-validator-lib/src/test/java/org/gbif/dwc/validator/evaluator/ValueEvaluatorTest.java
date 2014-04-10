package org.gbif.dwc.validator.evaluator;


import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator;
import org.gbif.dwc.validator.result.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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

    Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm =
      new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>();

    // register an InvalidCharacterEvaluationRule for scientificName
    List<EvaluationRuleIF<String>> ruleList = new ArrayList<EvaluationRuleIF<String>>();
    ruleList.add(InvalidCharacterEvaluationRule.createRule().build());
    rulesPerTerm.put(DwcTerm.scientificName, ruleList);

    ValueEvaluator valueEvaluator = new ValueEvaluator(rulesPerTerm, ValidationContext.CORE);
    // clear original map to ensure ValueEvaluator immutability
    rulesPerTerm.clear();

    valueEvaluator.handleEval(buildMockRecord("1"), resultAccumulator);

    assertTrue(resultAccumulator.getValidationResultsList().size() > 0);
    assertEquals("1", resultAccumulator.getValidationResultsList().get(0).getId());
  }

}
