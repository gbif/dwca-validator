package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.RecordCompletionEvaluator.RecordCompletionEvaluatorBuilder;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test RecordCompletionEvaluator implementation.
 * 
 * @author cgendreau
 */
public class RecordCompletionEvaluatorTest {

  private Record buildMockRecord(String id, String catalogNumber) {
    return MockRecordFactory.buildMockRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {DwcTerm.catalogNumber},
      new String[] {catalogNumber});
  }

  private Record buildMockRecord(String id, String catalogNumber, String sciName) {
    return MockRecordFactory.buildMockRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {DwcTerm.catalogNumber,
      DwcTerm.scientificName}, new String[] {catalogNumber, sciName});
  }

  @Test
  public void testRecordCompletionEvaluator() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    RecordCompletionEvaluator recordCompletionEvaluator =
      RecordCompletionEvaluatorBuilder.create().checkTerm(DwcTerm.scientificName).build();

    recordCompletionEvaluator.handleEval(buildMockRecord("1", "1", null), resultAccumulator);
    recordCompletionEvaluator.handleEval(buildMockRecord("2", "2", ""), resultAccumulator);
    recordCompletionEvaluator.handleEval(buildMockRecord("3", "3"), resultAccumulator);
    recordCompletionEvaluator.handleEval(buildMockRecord("4", "4", "a name"), resultAccumulator);

    assertTrue(resultAccumulator.getEvaluationResultList().size() > 0);

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "2",
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "3",
      ContentValidationType.RECORD_CONTENT_VALUE));

    assertFalse(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "4",
      ContentValidationType.RECORD_CONTENT_VALUE));
  }

  @Test(expected = NullPointerException.class)
  public void testRecordCompletionEvaluatorIncompleteConfiguration() {
    RecordCompletionEvaluatorBuilder.create().build();
  }

  RecordCompletionEvaluator recordCompletionEvaluator = RecordCompletionEvaluatorBuilder.create()
    .checkTerm(DwcTerm.scientificName).build();

}
