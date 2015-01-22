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


public class MinMaxCriteriaTest {

  private Record buildMockRecord(String occID, String minElevation, String maxElevation) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {
      DwcTerm.minimumElevationInMeters, DwcTerm.maximumElevationInMeters}, new String[] {minElevation, maxElevation});
  }

  @Test
  public void testMinMaxCriteria() {
    RecordCriteria criteria =
      MinMaxCriteriaBuilder.builder().terms(DwcTerm.minimumElevationInMeters, DwcTerm.maximumElevationInMeters).build();

    // min < max should be valid
    Optional<ValidationResult> result = criteria.validate(buildMockRecord("1", "10", "11"), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    // same value should ve valid
    result = criteria.validate(buildMockRecord("1", "10", "10"), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    // min > max should NOT be valid
    result = criteria.validate(buildMockRecord("1", "11", "10"), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());
  }

}
