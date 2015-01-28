package org.gbif.dwc.validator.chain;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.Evaluators;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.chain.EvaluatorChain;
import org.gbif.dwc.validator.criteria.DatasetCriteria;
import org.gbif.dwc.validator.criteria.RecordCriteria;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.accumulator.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the ChainableRecordEvaluator wrapper.
 * Ensure chain element are connected properly.
 * 
 * @author cgendreau
 */
public class CriteriaChainTest {

  private Record buildMockRecord(String id, String lat, String lng) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {
      DwcTerm.decimalLatitude, DwcTerm.decimalLongitude}, new String[] {lat, lng});
  }

  @Test
  public void testChain() {

    File testFolder = new File(".", "ChainableRecordEvaluatorTest");
    testFolder.mkdir();

    EvaluatorChain chain =
      Evaluators
        .builder()
        .with(DatasetCriteria.coreIdUniqueness(testFolder))
        .with(
          RecordCriteria.tryTransformations(ValueTransformations.toNumeric(DwcTerm.decimalLatitude),
            ValueTransformations.toNumeric(DwcTerm.decimalLongitude))).buildChain();

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    Record rec1 = buildMockRecord("1", "30", "60");
    Record rec2 = buildMockRecord("2", "a", "160");
    Record rec3 = buildMockRecord("3", "30", "40");
    Record rec4 = buildMockRecord("3", "30", "40");

    try {
      chain.evaluateRecord(rec1, EvaluationContext.CORE, resultAccumulator);
      chain.evaluateRecord(rec2, EvaluationContext.CORE, resultAccumulator);
      chain.evaluateRecord(rec3, EvaluationContext.CORE, resultAccumulator);
      chain.evaluateRecord(rec4, EvaluationContext.CORE, resultAccumulator);
      chain.evaluateDataset(resultAccumulator);
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }

    // Fist record should be valid
    assertFalse(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_VALUE));

    // Next records should not be valid
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "2",
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "3",
      ContentValidationType.FIELD_UNIQUENESS));

    try {
      chain.cleanup();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    testFolder.delete();
  }

}
