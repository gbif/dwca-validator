package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test RecordCompletionEvaluator implementation.
 * 
 * @author cgendreau
 */
public class CompletenessCriteriaTest {

  private Record buildMockRecord(String id, String catalogNumber) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {DwcTerm.catalogNumber},
      new String[] {catalogNumber});
  }

  private Record buildMockRecord(String id, String catalogNumber, String sciName) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {DwcTerm.catalogNumber,
      DwcTerm.scientificName}, new String[] {catalogNumber, sciName});
  }

  /**
   * // should send result (error)
   * assertTrue(rule.evaluate("").failed());
   * assertTrue(rule.evaluate(null).failed());
   * assertTrue(rule.evaluate(" ").failed());
   * assertTrue(rule.evaluate("\t").failed());
   * // should be valid
   * assertTrue(rule.evaluate("a").passed());
   * assertTrue(rule.evaluate(" a").passed());
   * // simply demonstrate that this would be considered valid. Maybe the rule should accommodate that.
   * assertTrue(rule.evaluate("\"\"").passed());
   */

  @Test
  public void testRecordCompletionEvaluator() {

    RecordCriteria recordCompletionCriteria =
      CompletenessCriteriaBuilder.builder().checkTerm(DwcTerm.scientificName).onRowType(DwcTerm.Occurrence).build();

    Optional<ValidationResult> result1 =
      recordCompletionCriteria.validate(buildMockRecord("1", "1", null), EvaluationContext.CORE);
    Optional<ValidationResult> result2 =
      recordCompletionCriteria.validate(buildMockRecord("2", "2", ""), EvaluationContext.CORE);
    Optional<ValidationResult> result3 =
      recordCompletionCriteria.validate(buildMockRecord("3", "3"), EvaluationContext.CORE);
    Optional<ValidationResult> result4 =
      recordCompletionCriteria.validate(buildMockRecord("4", "4", "a name"), EvaluationContext.CORE);

    assertTrue(result1.isPresent());
    assertTrue(result2.isPresent());
    assertTrue(result3.isPresent());

    assertTrue(TestEvaluationResultHelper.containsValidationType(result1.get(),
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(result2.get(),
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(result3.get(),
      ContentValidationType.RECORD_CONTENT_VALUE));

  }

  @Test(expected = IllegalStateException.class)
  public void testRecordCompletionEvaluatorIncompleteConfiguration() {
    CompletenessCriteriaBuilder.builder().build();
  }

}
