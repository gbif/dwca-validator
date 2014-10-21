package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRule.NumericalValueEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Ensure NumericalValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class NumericalValueEvaluationRuleTest {

  @Test
  public void evaluate() {
    NumericalValueEvaluationRule rule = NumericalValueEvaluationRuleBuilder.create().build();

    testAlwaysValidNumerical(rule);
    testNeverValidNumerical(rule);

    // trigger bound tests
    rule = NumericalValueEvaluationRule.createRule().boundedBy(1, 10).build();
    testNumericalBounds(rule, 1d, 10d);
  }

  private void testAlwaysValidNumerical(NumericalValueEvaluationRule rule) {
    assertEquals(Result.PASSED, rule.evaluate("1").getResult());
    assertEquals(Result.PASSED, rule.evaluate("1.2").getResult());
    assertEquals(Result.PASSED, rule.evaluate("0.3").getResult());
    assertEquals(Result.PASSED, rule.evaluate("-8.3").getResult());

    // empty string is ignored
    assertEquals(Result.SKIPPED, rule.evaluate("").getResult());
  }

  /**
   * Ensure an exception is thrown if we invert lower and upper bound.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBoundedByCallInverted() {
    NumericalValueEvaluationRule.createRule().boundedBy(23, 20).build();
  }

  /**
   * Ensure an exception is thrown if we user null lower and upper bound.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBoundedByCallWithNulls() {
    NumericalValueEvaluationRule.createRule().boundedBy(null, null).build();
  }

  private void testNeverValidNumerical(NumericalValueEvaluationRule rule) {
    assertNotNull(rule.evaluate("1.1.1"));
    assertNotNull(rule.evaluate("0.-9"));
    assertNotNull(rule.evaluate("w"));
  }

  private void testNumericalBounds(NumericalValueEvaluationRule rule, Double lowerBound, Double upperBound) {
    // should be valid
    assertEquals(Result.PASSED, rule.evaluate(lowerBound.toString()).getResult());
    assertEquals(Result.PASSED, rule.evaluate(upperBound.toString()).getResult());
    assertEquals(Result.PASSED, rule.evaluate(Double.toString(lowerBound + 0.001)).getResult());
    assertEquals(Result.PASSED, rule.evaluate(Double.toString(upperBound - 0.001)).getResult());

    // should be invalid
    assertNotNull(rule.evaluate(Double.toString(lowerBound - 0.001)));
    assertNotNull(rule.evaluate(Double.toString(upperBound + 0.001)));
  }

}
