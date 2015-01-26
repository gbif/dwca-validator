package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.configuration.MinMaxCriterionConfiguration;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class MinMaxCriterionTest {

  private Record buildMockRecord(String occID, String minElevation, String maxElevation) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {
      DwcTerm.minimumElevationInMeters, DwcTerm.maximumElevationInMeters}, new String[] {minElevation, maxElevation});
  }

  @Test
  public void testMinMaxCriterion() {
    RecordCriterionIF criterion =
      MinMaxCriterionBuilder.builder().terms(DwcTerm.minimumElevationInMeters, DwcTerm.maximumElevationInMeters)
        .build();

    // min < max should be valid
    Optional<ValidationResult> result = criterion.validate(buildMockRecord("1", "10", "11"), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    // same value should be valid
    result = criterion.validate(buildMockRecord("1", "10", "10"), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().passed());

    // min > max should NOT be valid
    result = criterion.validate(buildMockRecord("1", "11", "10"), EvaluationContext.CORE);
    assertTrue(result.isPresent() && result.get().failed());
  }

  @Test(expected = IllegalStateException.class)
  public void testBuilderWrongConfigurationBehavior() {
    MinMaxCriterionConfiguration config = new MinMaxCriterionConfiguration();
    config.setMinValueTerm(DwcTerm.minimumDepthInMeters);
    new MinMaxCriterionBuilder(config).build();
  }

  @Test(expected = IllegalStateException.class)
  public void testBuilderUnclearConfigurationBehavior() {
    MinMaxCriterionConfiguration config = new MinMaxCriterionConfiguration();
    config.setMinValueTerm(DwcTerm.minimumDepthInMeters);
    config.setMaxValueTerm(DwcTerm.maximumDepthInMeters);

    config.setMinValueTransformation(ValueTransformations.toNumeric(DwcTerm.minimumDepthInMeters));
    config.setMaxValueTransformation(ValueTransformations.toNumeric(DwcTerm.maximumDepthInMeters));
    new MinMaxCriterionBuilder(config).build();
  }

}
