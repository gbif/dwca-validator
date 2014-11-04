package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.UniquenessEvaluator;
import org.gbif.dwc.validator.evaluator.ValueEvaluator;
import org.gbif.dwc.validator.evaluator.ValueEvaluator.ValueEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.chain.builder.ChainableRecordEvaluatorBuilderIF;
import org.gbif.dwc.validator.evaluator.chain.builder.DefaultChainableRecordEvaluatorBuilder;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRule;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    UniquenessEvaluator uniquenessEvaluator = null;
    try {
      uniquenessEvaluator = UniquenessEvaluator.create().build();
    } catch (NullPointerException e) {
      e.printStackTrace();
      fail();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      fail();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }

    ValueEvaluatorBuilder rulesBuilder = ValueEvaluatorBuilder.create();
    NumericalValueEvaluationRule numericalValueEvaluationRule = NumericalValueEvaluationRule.createRule().build();
    rulesBuilder.addRule(DwcTerm.decimalLatitude, numericalValueEvaluationRule);
    rulesBuilder.addRule(DwcTerm.decimalLongitude, numericalValueEvaluationRule);

    ValueEvaluator valueEvaluator = rulesBuilder.build();

    ChainableRecordEvaluatorBuilderIF chainBuilder =
      DefaultChainableRecordEvaluatorBuilder.create(valueEvaluator).linkTo(uniquenessEvaluator);

    ChainableRecordEvaluator chain = chainBuilder.build();
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    Record rec1 = buildMockRecord("1", "30", "60");
    Record rec2 = buildMockRecord("2", "a", "160");
    Record rec3 = buildMockRecord("3", "30", "40");
    Record rec4 = buildMockRecord("3", "30", "40");

    chain.doEval(rec1, resultAccumulator);
    chain.doEval(rec2, resultAccumulator);
    chain.doEval(rec3, resultAccumulator);
    chain.doEval(rec4, resultAccumulator);
    chain.postIterate(resultAccumulator);

    // Fist record should be valid
    assertFalse(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "2",
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "3",
      ContentValidationType.FIELD_UNIQUENESS));

    chain.cleanup();
  }

}
