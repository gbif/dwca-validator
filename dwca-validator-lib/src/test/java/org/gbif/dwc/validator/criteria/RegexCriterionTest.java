package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.criteria.record.RecordCriterion;
import org.gbif.dwc.validator.criteria.record.RegexCriterionBuilder;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test RegexCriterion implementation.
 * 
 * @author cgendreau
 */
public class RegexCriterionTest {

  private Record buildMockRecord(String occID, String country) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {DwcTerm.country},
      new String[] {country});
  }

  @Test
  public void testRecordCriterion() {

    RecordCriterion criteria = RegexCriterionBuilder.builder().regex(DwcTerm.country, "[hc]at").build();

    Optional<ValidationResult> result = criteria.validate(buildMockRecord("1", "cat"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));
    result = criteria.validate(buildMockRecord("12", "hat"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // should not passed
    result = criteria.validate(buildMockRecord("3", "bat"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));
    result = criteria.validate(buildMockRecord("4", "a cat"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));
  }

  @Test(expected = NullPointerException.class)
  public void testBuilderBehavior() {
    RegexCriterionBuilder.builder().build();
  }

}
