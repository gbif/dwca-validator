package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.Evaluators;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.IntegrityEvaluators;
import org.gbif.dwc.validator.evaluator.TermsValidators;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRule;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the ChainableRecordEvaluator wrapper.
 * Ensure chain element are connected properly.
 * 
 * @author cgendreau
 */
public class ChainableRecordEvaluatorTest {

  private Record buildMockRecord(String id, String lat, String lng) {
    return MockRecordFactory.buildMockRecord(DwcTerm.occurrenceID, id, new DwcTerm[] {DwcTerm.decimalLatitude,
      DwcTerm.decimalLongitude}, new String[] {lat, lng});
  }

  @Test
  public void testChain() {

    File testFolder = new File(".", "ChainableRecordEvaluatorTest");
    testFolder.mkdir();

    ChainableRecordEvaluator chain =
      Evaluators
        .builder()
        .with(IntegrityEvaluators.coreIdUniqueness(testFolder))
        .with(
          TermsValidators.rule(NumericalValueEvaluationRule.createRule().build(), DwcTerm.decimalLatitude,
            DwcTerm.decimalLongitude)).buildChain();

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    Record rec1 = buildMockRecord("1", "30", "60");
    Record rec2 = buildMockRecord("2", "a", "160");
    Record rec3 = buildMockRecord("3", "30", "40");
    Record rec4 = buildMockRecord("3", "30", "40");

    chain.doEval(rec1, EvaluationContext.CORE, resultAccumulator);
    chain.doEval(rec2, EvaluationContext.CORE, resultAccumulator);
    chain.doEval(rec3, EvaluationContext.CORE, resultAccumulator);
    chain.doEval(rec4, EvaluationContext.CORE, resultAccumulator);
    chain.postIterate(resultAccumulator);

    // Fist record should be valid
    assertFalse(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_VALUE));

    // Next records should not be valid
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "2",
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "3",
      ContentValidationType.FIELD_UNIQUENESS));

    chain.cleanup();
    testFolder.delete();
  }

}
