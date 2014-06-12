package org.gbif.dwc.validator.rule.value;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Ensure NumericalValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class NumericalValueEvaluationRuleTest {

  @Test
  public void evaluate() {
    NumericalValueEvaluationRule rule = NumericalValueEvaluationRule.createRule().build();

    testAlwaysValidNumerical(rule);
    testNeverValidNumerical(rule);
  }

  private void testAlwaysValidNumerical(NumericalValueEvaluationRule rule) {
    assertNull(rule.evaluate("1"));
    assertNull(rule.evaluate("1.2"));
    assertNull(rule.evaluate("0.3"));
    assertNull(rule.evaluate("-8.3"));

    // empty string is ignored
    assertNull(rule.evaluate(""));
  }

  private void testNeverValidNumerical(NumericalValueEvaluationRule rule) {
    assertNotNull(rule.evaluate("1.1.1"));
    assertNotNull(rule.evaluate("0.-9"));
    assertNotNull(rule.evaluate("w"));
  }

}
