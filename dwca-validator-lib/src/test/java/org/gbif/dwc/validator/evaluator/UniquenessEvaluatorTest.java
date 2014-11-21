package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.evaluator.integrity.UniquenessEvaluatorBuilder;
import org.gbif.dwc.validator.exception.EvaluationException;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.accumulator.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test UniquenessEvaluator with mock records.
 * 
 * @author cgendreau
 */
public class UniquenessEvaluatorTest {

  private Record buildMockRecord(String id, String catalogNumber) {
    // create a mock Record
    ArchiveField idField = new ArchiveField(0, DwcTerm.occurrenceID, null, DataType.string);
    ArchiveField scientificNameField = new ArchiveField(1, DwcTerm.scientificName, null, DataType.string);
    ArchiveField catalogNumberField = new ArchiveField(2, DwcTerm.catalogNumber, null, DataType.string);
    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(scientificNameField);
    fieldList.add(catalogNumberField);

    RecordImpl testRecord = new RecordImpl(idField, fieldList, DwcTerm.Occurrence.qualifiedName(), false);
    testRecord.setRow(new String[] {id, "gulo\tgulo", catalogNumber});
    return testRecord;
  }

  @Test
  public void testUniquenessEvaluatorNonUniqueId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      StatefulRecordEvaluator valueEvaluator = UniquenessEvaluatorBuilder.builder().build();
      valueEvaluator.handleEval(buildMockRecord("1", "1"), EvaluationContext.CORE);
      valueEvaluator.handleEval(buildMockRecord("1", "2"), EvaluationContext.CORE);

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    } catch (EvaluationException e) {
      e.printStackTrace();
      fail();
    }

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "1",
      ContentValidationType.FIELD_UNIQUENESS));
  }


  @Test
  public void testUniquenessEvaluatorWithEmptyString() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      StatefulRecordEvaluator valueEvaluator = UniquenessEvaluatorBuilder.builder().build();
      valueEvaluator.handleEval(buildMockRecord("", "1"), EvaluationContext.CORE);
      valueEvaluator.handleEval(buildMockRecord("", "2"), EvaluationContext.CORE);

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    } catch (EvaluationException e) {
      e.printStackTrace();
      fail();
    }

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(),
      ValidatorConfig.EMPTY_STRING_FOR_DISPLAY, ContentValidationType.FIELD_UNIQUENESS));
  }


  @Test
  public void testUniquenessEvaluatorOnAnotherTerm() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      StatefulRecordEvaluator valueEvaluator =
        UniquenessEvaluatorBuilder.builder()
          .on(DwcTerm.catalogNumber, EvaluationContext.CORE, DwcTerm.Occurrence.qualifiedName()).build();
      valueEvaluator.handleEval(buildMockRecord("1", "1"), EvaluationContext.CORE);
      valueEvaluator.handleEval(buildMockRecord("2", "1"), EvaluationContext.CORE);

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    } catch (EvaluationException e) {
      e.printStackTrace();
      fail();
    }

    // remember that the id in UniquenessEvaluator will be catalogNumber
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "1",
      ContentValidationType.FIELD_UNIQUENESS));
  }

  @Test
  public void testUniquenessEvaluatorWithUniqueId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      StatefulRecordEvaluator valueEvaluator = UniquenessEvaluatorBuilder.builder().build();
      valueEvaluator.handleEval(buildMockRecord("1", "1"), EvaluationContext.CORE);
      valueEvaluator.handleEval(buildMockRecord("2", "1"), EvaluationContext.CORE);

      valueEvaluator.handlePostIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    } catch (EvaluationException e) {
      e.printStackTrace();
      fail();
    }
    assertTrue(resultAccumulator.getValidationResultList().isEmpty());
  }

}
