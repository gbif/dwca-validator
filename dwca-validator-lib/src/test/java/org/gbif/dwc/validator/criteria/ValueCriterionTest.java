package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.criteria.record.RecordCriterion;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwca.record.Record;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test ValueCriterion with mock records.
 *
 * @author cgendreau
 */
public class ValueCriterionTest {

  private Record buildMockRecord(String occID, String lat, String lng) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {
      DwcTerm.decimalLatitude, DwcTerm.decimalLongitude}, new String[] {lat, lng});
  }

  @Test
  public void testValueCriterion() {
    RecordCriterion criteria = RecordCriteria.termNotEqualsTo(DwcTerm.decimalLatitude, 0).build();

    Optional<ValidationResult> result =
      criteria.handleRecord(buildMockRecord("1", "2.70", "0"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    result = criteria.handleRecord(buildMockRecord("1", "0", "0"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));

    // make sure we handle wrong data type
    result = criteria.handleRecord(buildMockRecord("1", "a", "0"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));
  }

}
