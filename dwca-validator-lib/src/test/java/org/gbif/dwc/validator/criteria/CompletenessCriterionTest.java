package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.criteria.record.CompletenessCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.RecordCriterion;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwca.record.Record;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test RecordCompletionCriterion implementation.
 *
 * @author cgendreau
 */
public class CompletenessCriterionTest {

  private Record buildMockRecord(String id, String catalogNumber) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {DwcTerm.catalogNumber},
      new String[] {catalogNumber});
  }

  private Record buildMockRecord(String id, String catalogNumber, String sciName) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {DwcTerm.catalogNumber,
      DwcTerm.scientificName}, new String[] {catalogNumber, sciName});
  }

  @Test
  public void testRecordCompletionCriterion() {

    RecordCriterion recordCompletionCriterion =
      CompletenessCriterionBuilder.builder().checkTerm(DwcTerm.scientificName).onRowType(DwcTerm.Occurrence).build();

    Optional<ValidationResult> result1 =
      recordCompletionCriterion.handleRecord(buildMockRecord("1", "1", null), EvaluationContext.CORE);
    Optional<ValidationResult> result2 =
      recordCompletionCriterion.handleRecord(buildMockRecord("2", "2", ""), EvaluationContext.CORE);
    Optional<ValidationResult> result3 =
      recordCompletionCriterion.handleRecord(buildMockRecord("3", "3"), EvaluationContext.CORE);
    Optional<ValidationResult> result4 =
      recordCompletionCriterion.handleRecord(buildMockRecord("4", "4", "\t"), EvaluationContext.CORE);
    Optional<ValidationResult> result5 =
      recordCompletionCriterion.handleRecord(buildMockRecord("5", "5", " "), EvaluationContext.CORE);

    assertTrue(TestEvaluationResultHelper.validationFailed(result1));
    assertTrue(TestEvaluationResultHelper.validationFailed(result2));
    assertTrue(TestEvaluationResultHelper.validationFailed(result3));
    assertTrue(TestEvaluationResultHelper.validationFailed(result4));
    assertTrue(TestEvaluationResultHelper.validationFailed(result5));

    Optional<ValidationResult> result6 =
      recordCompletionCriterion.handleRecord(buildMockRecord("6", "6", "a name"), EvaluationContext.CORE);
    Optional<ValidationResult> result7 =
      recordCompletionCriterion.handleRecord(buildMockRecord("7", "7", " a "), EvaluationContext.CORE);

    assertTrue(TestEvaluationResultHelper.validationPassed(result6));
    assertTrue(TestEvaluationResultHelper.validationPassed(result7));

    // simply demonstrate that this would be considered valid. Maybe the criterion should accommodate that.
    Optional<ValidationResult> result8 =
      recordCompletionCriterion.handleRecord(buildMockRecord("7", "7", "\"\""), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result8));

  }

  @Test
  public void testRecordCompletionCriterionWithAbsenceSynonym() {
    RecordCriterion recordCompletionCriterion =
      CompletenessCriterionBuilder.builder().checkTerm(DwcTerm.scientificName, "null").onRowType(DwcTerm.Occurrence)
      .build();

    Optional<ValidationResult> result =
      recordCompletionCriterion.handleRecord(buildMockRecord("2", "2", "nil"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    result = recordCompletionCriterion.handleRecord(buildMockRecord("2", "2", "null"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));
  }

  @Test(expected = IllegalStateException.class)
  public void testRecordCompletionCriterionIncompleteConfiguration() {
    CompletenessCriterionBuilder.builder().build();
  }

}
