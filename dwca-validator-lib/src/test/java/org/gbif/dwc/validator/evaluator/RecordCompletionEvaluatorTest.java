package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.RecordCompletionEvaluator.RecordCompletionEvaluatorBuilder;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;
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

    RecordCompletionEvaluator recordCompletionEvaluator =
      RecordCompletionEvaluatorBuilder.create().checkTerm(DwcTerm.scientificName).build();

    Optional<ValidationResult> result1 = recordCompletionEvaluator.handleEval(buildMockRecord("1", "1", null));
    Optional<ValidationResult> result2 = recordCompletionEvaluator.handleEval(buildMockRecord("2", "2", ""));
    Optional<ValidationResult> result3 = recordCompletionEvaluator.handleEval(buildMockRecord("3", "3"));
    Optional<ValidationResult> result4 = recordCompletionEvaluator.handleEval(buildMockRecord("4", "4", "a name"));

    assertTrue(result1.isPresent());
    assertTrue(result2.isPresent());
    assertTrue(result3.isPresent());

    assertTrue(TestEvaluationResultHelper.containsValidationType(result1.get(),
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(result2.get(),
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(result3.get(),
      ContentValidationType.RECORD_CONTENT_VALUE));

    assertFalse(result4.isPresent());
  }

  @Test(expected = NullPointerException.class)
  public void testRecordCompletionEvaluatorIncompleteConfiguration() {
    RecordCompletionEvaluatorBuilder.create().build();
  }

  RecordCompletionEvaluator recordCompletionEvaluator = RecordCompletionEvaluatorBuilder.create()
    .checkTerm(DwcTerm.scientificName).build();

}
