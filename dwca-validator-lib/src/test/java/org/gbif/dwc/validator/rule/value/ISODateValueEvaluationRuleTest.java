package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.value.ISODateValueEvaluationRule.ISODateValueEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Ensure ISODateValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class ISODateValueEvaluationRuleTest {

  @Test
  public void evaluate() {
    ISODateValueEvaluationRule rule = ISODateValueEvaluationRuleBuilder.create().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  @Test
  public void evaluateAllowMissingLeadingZeros() {
    ISODateValueEvaluationRule rule = ISODateValueEvaluationRuleBuilder.create().allowMissingLeadingZeros().build();

    assertNull(rule.evaluate("2014-8-7"));

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  @Test
  public void evaluateAllowPartialDate() {
    ISODateValueEvaluationRule rule = ISODateValueEvaluationRuleBuilder.create().allowPartialDate().build();

    assertNull(rule.evaluate("2014"));
    assertNull(rule.evaluate("2014-08"));

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  @Test
  public void evaluateAllowPartialDateAndMissingLeadingZeros() {
    ISODateValueEvaluationRule rule =
      ISODateValueEvaluationRuleBuilder.create().allowPartialDate().allowMissingLeadingZeros().build();

    assertNull(rule.evaluate("2014"));
    assertNull(rule.evaluate("2014-08"));
    assertNull(rule.evaluate("2014-8"));
    assertNull(rule.evaluate("2014-8-7"));

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  private void testAlwaysValidString(ISODateValueEvaluationRule rule) {
    assertNull(rule.evaluate("20141010"));
    assertNull(rule.evaluate("2014-10-10"));
  }

  private void testNeverValidString(ISODateValueEvaluationRule rule) {
    assertNotNull(rule.evaluate("201411"));
    assertNotNull(rule.evaluate("21-10-2014"));
  }

}
