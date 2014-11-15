package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.EvaluationRule;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure NumericalValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class NumericalValueEvaluationRuleTest {

  private void testAlwaysValidNumerical(EvaluationRule<String> rule) {
    assertTrue(rule.evaluate("1").passed());
    assertTrue(rule.evaluate("1.2").passed());
    assertTrue(rule.evaluate("0.3").passed());
    assertTrue(rule.evaluate("-8.3").passed());

    // empty string is ignored
    assertTrue(rule.evaluate("").skipped());
  }

  private void testNeverValidNumerical(EvaluationRule<String> rule) {
    assertTrue(rule.evaluate("1.1.1").failed());
    assertTrue(rule.evaluate("0.-9").failed());
    assertTrue(rule.evaluate("w").failed());
  }

  private void testNumericalBounds(EvaluationRule<String> rule, Double lowerBound, Double upperBound) {
    // should be valid
    assertTrue(rule.evaluate(lowerBound.toString()).passed());
    assertTrue(rule.evaluate(upperBound.toString()).passed());
    assertTrue(rule.evaluate(Double.toString(lowerBound + 0.001)).passed());
    assertTrue(rule.evaluate(Double.toString(upperBound - 0.001)).passed());

    // should be invalid
    assertTrue(rule.evaluate(Double.toString(lowerBound - 0.001)).failed());
    assertTrue(rule.evaluate(Double.toString(upperBound + 0.001)).failed());
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
