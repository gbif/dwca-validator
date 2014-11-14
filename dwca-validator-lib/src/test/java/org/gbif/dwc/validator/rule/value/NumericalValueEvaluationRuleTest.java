package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.rule.EvaluationRule;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Ensure NumericalValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class NumericalValueEvaluationRuleTest {

  private void testAlwaysValidNumerical(EvaluationRule<String> rule) {
    assertEquals(Result.PASSED, rule.evaluate("1").getResult());
    assertEquals(Result.PASSED, rule.evaluate("1.2").getResult());
    assertEquals(Result.PASSED, rule.evaluate("0.3").getResult());
    assertEquals(Result.PASSED, rule.evaluate("-8.3").getResult());

    // empty string is ignored
    assertEquals(Result.SKIPPED, rule.evaluate("").getResult());
  }

  private void testNeverValidNumerical(EvaluationRule<String> rule) {
    assertNotNull(rule.evaluate("1.1.1"));
    assertNotNull(rule.evaluate("0.-9"));
    assertNotNull(rule.evaluate("w"));
  }

  private void testNumericalBounds(EvaluationRule<String> rule, Double lowerBound, Double upperBound) {
    // should be valid
    assertEquals(Result.PASSED, rule.evaluate(lowerBound.toString()).getResult());
    assertEquals(Result.PASSED, rule.evaluate(upperBound.toString()).getResult());
    assertEquals(Result.PASSED, rule.evaluate(Double.toString(lowerBound + 0.001)).getResult());
    assertEquals(Result.PASSED, rule.evaluate(Double.toString(upperBound - 0.001)).getResult());

    // should be invalid
    assertNotNull(rule.evaluate(Double.toString(lowerBound - 0.001)));
    assertNotNull(rule.evaluate(Double.toString(upperBound + 0.001)));
  }

  @Test
  public void evaluate() {
    EvaluationRule<String> rule = NumericalValueEvaluationRuleBuilder.builder().build();

    testAlwaysValidNumerical(rule);
    testNeverValidNumerical(rule);

    // trigger bound tests
    rule = NumericalValueEvaluationRuleBuilder.builder().boundedBy(1, 10).build();
    testNumericalBounds(rule, 1d, 10d);
  }

  /**
   * Ensure an exception is thrown if we invert lower and upper bound.
   */
  @Test(expected = IllegalStateException.class)
  public void testBoundedByCallWithInvertedBounds() {
    NumericalValueEvaluationRuleBuilder.builder().boundedBy(23, 20).build();
  }

  /**
   * Ensure an exception is thrown if we use null lower bound.
   */
  @Test(expected = NullPointerException.class)
  public void testBoundedByCallWithNull() {
    NumericalValueEvaluationRuleBuilder.builder().boundedBy(null, 8).build();
  }

}
