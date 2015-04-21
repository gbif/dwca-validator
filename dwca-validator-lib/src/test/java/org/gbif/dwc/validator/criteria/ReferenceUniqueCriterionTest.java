package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriterion;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.accumulator.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwca.io.ArchiveField;
import org.gbif.dwca.io.ArchiveField.DataType;
import org.gbif.dwca.record.Record;
import org.gbif.dwca.record.RecordImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test ReferenceUniqueCriterion with mock records.
 *
 * @author cgendreau
 */
public class ReferenceUniqueCriterionTest {

  private Record buildMockRecord(String taxonID, String acceptedNameUsageID) {
    // create a mock Record
    ArchiveField idField = new ArchiveField(0, DwcTerm.taxonID, null, DataType.string);
    ArchiveField acceptedNameUsageIDField = new ArchiveField(1, DwcTerm.acceptedNameUsageID, null, DataType.string);
    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(idField);
    fieldList.add(acceptedNameUsageIDField);

    RecordImpl testRecord = new RecordImpl(idField, fieldList, DwcTerm.Taxon, false, false);
    testRecord.setRow(new String[] {taxonID, acceptedNameUsageID});
    return testRecord;
  }

  @Test
  public void referentialIntegrityEvaluatorCorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    try {
      DatasetCriterion referenceCriterion =
        DatasetCriteria.termReferentialIntegrityInCore(null, DwcTerm.acceptedNameUsageID, DwcTerm.taxonID,
          DwcTerm.Taxon).build();

      referenceCriterion.onRecord(buildMockRecord("1", "2b"), EvaluationContext.CORE);
      referenceCriterion.onRecord(buildMockRecord("2a", "1"), EvaluationContext.CORE);
      // add blank acceptedNameUsageID, should be ignored.
      referenceCriterion.onRecord(buildMockRecord("2b", ""), EvaluationContext.CORE);

      referenceCriterion.postIterate(resultAccumulator);
      referenceCriterion.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }
    assertTrue(resultAccumulator.getValidationResultList().isEmpty());
  }

  @Test
  public void referentialIntegrityEvaluatorIncorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    try {
      DatasetCriterion referenceCriterion =
        DatasetCriteria.termReferentialIntegrityInCore(null, DwcTerm.acceptedNameUsageID, DwcTerm.taxonID,
          DwcTerm.Taxon).build();

      referenceCriterion.onRecord(buildMockRecord("1", "4"), EvaluationContext.CORE);
      referenceCriterion.onRecord(buildMockRecord("2", "1"), EvaluationContext.CORE);

      referenceCriterion.postIterate(resultAccumulator);
      referenceCriterion.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "4",
      ContentValidationType.FIELD_REFERENTIAL_INTEGRITY));
  }

  @Test
  public void referentialIntegrityEvaluatorMultipleCorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    try {
      DatasetCriterion referenceCriterion =
        DatasetCriteria
        .termReferentialIntegrityInCore(null, DwcTerm.acceptedNameUsageID, DwcTerm.taxonID, DwcTerm.Taxon)
        .supportMultipleValues("|").build();

      referenceCriterion.onRecord(buildMockRecord("1", "3|4"), EvaluationContext.CORE);
      referenceCriterion.onRecord(buildMockRecord("3", ""), EvaluationContext.CORE);
      referenceCriterion.onRecord(buildMockRecord("4", ""), EvaluationContext.CORE);
      referenceCriterion.postIterate(resultAccumulator);
      referenceCriterion.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }
    assertTrue(resultAccumulator.getValidationResultList().isEmpty());
  }

  @Test
  public void referentialIntegrityEvaluatorMultipleIncorrectId() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    try {
      DatasetCriterion referenceCriterion =
        DatasetCriteria
        .termReferentialIntegrityInCore(null, DwcTerm.acceptedNameUsageID, DwcTerm.taxonID, DwcTerm.Taxon)
        .supportMultipleValues("|").build();
      referenceCriterion.onRecord(buildMockRecord("1", "3|5"), EvaluationContext.CORE);
      referenceCriterion.onRecord(buildMockRecord("3", ""), EvaluationContext.CORE);
      referenceCriterion.onRecord(buildMockRecord("4", ""), EvaluationContext.CORE);

      referenceCriterion.postIterate(resultAccumulator);
      referenceCriterion.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "5",
      ContentValidationType.FIELD_REFERENTIAL_INTEGRITY));
  }
}
