package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class BoundCriteriaTest {

  public static final Double MIN_LATITUDE = -90d;
  public static final Double MAX_LATITUDE = 90d;

  private Record buildMockRecord(String occID, String lat) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID,
      new Term[] {DwcTerm.decimalLatitude}, new String[] {lat});
  }

  @Test
  public void testBoundCriteria() {
    RecordCriteria criteria =
      BoundCriteriaBuilder.builder().termBoundedBy(DwcTerm.decimalLatitude, MIN_LATITUDE, MAX_LATITUDE).build();

    Optional<ValidationResult> result =
      criteria.validate(buildMockRecord("1", MIN_LATITUDE.toString()), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    result = criteria.validate(buildMockRecord("1", MAX_LATITUDE.toString()), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    // try to add 0.001 to min value, this should still be valid
    result =
      criteria.validate(buildMockRecord("1", new Double(MIN_LATITUDE + 0.001).toString()), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    // try to subtract 0.001 to max value, this should still be valid
    result =
      criteria.validate(buildMockRecord("1", new Double(MAX_LATITUDE - 0.001).toString()), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    // try to subtract 0.001 to min value, this should NOT be valid
    result =
      criteria.validate(buildMockRecord("1", new Double(MIN_LATITUDE - 0.001).toString()), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());

    // try to subtract 0.001 to max value, this should still be valid
    result =
      criteria.validate(buildMockRecord("1", new Double(MAX_LATITUDE + 0.001).toString()), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());

  }

}
