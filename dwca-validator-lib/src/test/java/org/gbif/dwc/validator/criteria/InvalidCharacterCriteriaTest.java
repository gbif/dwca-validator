package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class InvalidCharacterCriteriaTest {

  // never allowed char
  private static final char NULL_CHAR = 0;
  private static final char ESCAPE_CHAR = 27;

  // replacement character normally seen in wrong encoding situation
  private static final String REPLACEMENT_CHAR = "\uFFFD";

  // allowed if allowFormattingWhiteSpace() is used
  private static final String ENDLINE = System.getProperty("line.separator");
  private static final char VERTICAL_TAB_CHAR = 11;

  private Record buildMockRecord(String occID, String scientificName) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID,
      new Term[] {DwcTerm.scientificName}, new String[] {scientificName});
  }

  @Test
  public void testFormattingWhiteSpaceAllowed() {
    RecordCriteria criteria =
      InvalidCharacterCriteriaBuilder.builder().onTerm(DwcTerm.scientificName).allowFormattingWhiteSpace().build();

    testAlwaysValidString(criteria);
    testNeverValidString(criteria);

    Optional<ValidationResult> result =
      criteria.validate(buildMockRecord("1", "test" + VERTICAL_TAB_CHAR), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    result = criteria.validate(buildMockRecord("1", "test" + REPLACEMENT_CHAR), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    result = criteria.validate(buildMockRecord("1", "test" + ENDLINE), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());
  }

  @Test
  public void testNoFormattingWhiteSpaceAllowed() {
    // noFormattingWhiteSpaceAllowed is the default behavior
    RecordCriteria criteria = InvalidCharacterCriteriaBuilder.builder().onTerm(DwcTerm.scientificName).build();

    testAlwaysValidString(criteria);
    testNeverValidString(criteria);

    Optional<ValidationResult> result = criteria.validate(buildMockRecord("1", "test\t2"), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());

    result = criteria.validate(buildMockRecord("1", "test" + ENDLINE), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());

    result = criteria.validate(buildMockRecord("1", "test" + REPLACEMENT_CHAR), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));
  }

  @Test
  public void testNoReplacementCharAllowed() {
    RecordCriteria criteria =
      InvalidCharacterCriteriaBuilder.builder().onTerm(DwcTerm.scientificName).rejectReplacementChar().build();

    testAlwaysValidString(criteria);
    testNeverValidString(criteria);

    Optional<ValidationResult> result =
      criteria.validate(buildMockRecord("1", "test" + REPLACEMENT_CHAR), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());
  }

  private void testAlwaysValidString(RecordCriteria criteria) {

    Optional<ValidationResult> result = criteria.validate(buildMockRecord("1", "test"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    result = criteria.validate(buildMockRecord("1", "test 2"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    result = criteria.validate(buildMockRecord("1", "éä@%&*"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // null should be skipped
    result = criteria.validate(buildMockRecord("1", null), EvaluationContext.CORE);
    assertFalse(result.isPresent());
  }

  private void testNeverValidString(RecordCriteria criteria) {
    Optional<ValidationResult> result =
      criteria.validate(buildMockRecord("1", "test" + NULL_CHAR), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());

    result = criteria.validate(buildMockRecord("1", "test" + ESCAPE_CHAR), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());
  }
}
