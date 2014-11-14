package org.gbif.dwc.validator.evaluator;


import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.term.ValueEvaluatorBuilder;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRuleBuilder;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
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

    // register an InvalidCharacterEvaluationRule for scientificName
    RecordEvaluator valueEvaluator =
      ValueEvaluatorBuilder.builder()
        .addRule(DwcTerm.scientificName, InvalidCharacterEvaluationRuleBuilder.builder().build()).build();

    Optional<ValidationResult> result = valueEvaluator.handleEval(buildMockRecord("1"), EvaluationContext.CORE);

    assertTrue(result.isPresent());

    assertTrue(TestEvaluationResultHelper.containsValidationType(result.get(),
      ContentValidationType.RECORD_CONTENT_VALUE));
  }

  @Test(expected = IllegalStateException.class)
  public void testValueEvaluatorIncompleteConfiguration() {
    ValueEvaluatorBuilder.builder().build();
  }

}
