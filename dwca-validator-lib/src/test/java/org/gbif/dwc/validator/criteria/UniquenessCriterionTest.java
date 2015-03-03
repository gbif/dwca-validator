package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriterion;
import org.gbif.dwc.validator.criteria.dataset.UniquenessCriterionBuilder;
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
 * Test UniquenessCriterion with mock records.
 * 
 * @author cgendreau
 */
public class UniquenessCriterionTest {

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
  public void testUniquenessCriterionNonUniqueId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      DatasetCriterion uniquenessEvaluator = UniquenessCriterionBuilder.builder().build();
      uniquenessEvaluator.onRecord(buildMockRecord("1", "1"), EvaluationContext.CORE);
      uniquenessEvaluator.onRecord(buildMockRecord("1", "2"), EvaluationContext.CORE);

      uniquenessEvaluator.postIterate(resultAccumulator);
      uniquenessEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "1",
      ContentValidationType.FIELD_UNIQUENESS));
  }


  @Test
  public void testUniquenessCriterionWithEmptyString() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      DatasetCriterion valueEvaluator = UniquenessCriterionBuilder.builder().build();
      valueEvaluator.onRecord(buildMockRecord("", "1"), EvaluationContext.CORE);
      valueEvaluator.onRecord(buildMockRecord("", "2"), EvaluationContext.CORE);

      valueEvaluator.postIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(),
      ValidatorConfig.EMPTY_STRING_FOR_DISPLAY, ContentValidationType.FIELD_UNIQUENESS));
  }


  @Test
  public void testUniquenessCriterionOnAnotherTerm() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      DatasetCriterion valueEvaluator =
        UniquenessCriterionBuilder.builder()
          .on(DwcTerm.catalogNumber, EvaluationContext.CORE, DwcTerm.Occurrence.qualifiedName()).build();
      valueEvaluator.onRecord(buildMockRecord("1", "1"), EvaluationContext.CORE);
      valueEvaluator.onRecord(buildMockRecord("2", "1"), EvaluationContext.CORE);

      valueEvaluator.postIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }

    // remember that the id in UniquenessEvaluator will be catalogNumber
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "1",
      ContentValidationType.FIELD_UNIQUENESS));
  }

  @Test
  public void testUniquenessCriterionWithUniqueId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      DatasetCriterion valueEvaluator = UniquenessCriterionBuilder.builder().build();
      valueEvaluator.onRecord(buildMockRecord("1", "1"), EvaluationContext.CORE);
      valueEvaluator.onRecord(buildMockRecord("2", "1"), EvaluationContext.CORE);

      valueEvaluator.postIterate(resultAccumulator);
      valueEvaluator.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }
    assertTrue(resultAccumulator.getValidationResultList().isEmpty());
  }

}
