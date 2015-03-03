package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.criteria.configuration.MinMaxCriterionConfiguration;
import org.gbif.dwc.validator.criteria.record.MinMaxCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.RecordCriterion;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class MinMaxCriterionTest {

  private Record buildMockRecord(String occID, String minElevation, String maxElevation) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {
      DwcTerm.minimumElevationInMeters, DwcTerm.maximumElevationInMeters}, new String[] {minElevation, maxElevation});
  }

  @Test
  public void testMinMaxCriterion() {
    RecordCriterion criterion =
      MinMaxCriterionBuilder.builder().terms(DwcTerm.minimumElevationInMeters, DwcTerm.maximumElevationInMeters)
        .build();

    // min < max should be valid
    Optional<ValidationResult> result =
      criterion.handleRecord(buildMockRecord("1", "10", "11"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // same value should be valid
    result = criterion.handleRecord(buildMockRecord("1", "10", "10"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // min > max should NOT be valid
    result = criterion.handleRecord(buildMockRecord("1", "11", "10"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));

    // a non numeric value should fail
    result = criterion.handleRecord(buildMockRecord("1", "1", "b"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));

    // empty values should be skipped
    result = criterion.handleRecord(buildMockRecord("1", "", ""), EvaluationContext.CORE);
    assertFalse(result.isPresent());

    // one numeric value and the other empty should pass
    result = criterion.handleRecord(buildMockRecord("1", "1", ""), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));
  }

  @Test
  public void testMinMaxCriterionEnforceTwoTerms() {
    RecordCriterion criterion =
      MinMaxCriterionBuilder.builder().terms(DwcTerm.minimumElevationInMeters, DwcTerm.maximumElevationInMeters)
        .enforceTwoTermsUse().build();

    // min < max should be valid
    Optional<ValidationResult> result =
      criterion.handleRecord(buildMockRecord("1", "10", "11"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // same value should be valid
    result = criterion.handleRecord(buildMockRecord("1", "10", "10"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // min > max should NOT be valid
    result = criterion.handleRecord(buildMockRecord("1", "11", "10"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));

    // a non numeric value should fail
    result = criterion.handleRecord(buildMockRecord("1", "1", "b"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));

    // empty value should still be skipped
    result = criterion.handleRecord(buildMockRecord("1", "", ""), EvaluationContext.CORE);
    assertFalse(result.isPresent());

    // one numeric value and the other empty should fail
    result = criterion.handleRecord(buildMockRecord("1", "1", ""), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));
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
