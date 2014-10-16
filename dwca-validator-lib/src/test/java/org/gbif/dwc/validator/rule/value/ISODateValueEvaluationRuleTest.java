package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.rule.value.ISODateValueEvaluationRule.ISODateValueEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    assertEquals(Result.PASSED, rule.evaluate("2014-8-7").getResult());

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  @Test
  public void evaluateAllowPartialDate() {
    ISODateValueEvaluationRule rule = ISODateValueEvaluationRuleBuilder.create().allowPartialDate().build();

    assertEquals(Result.PASSED, rule.evaluate("2014").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-08").getResult());

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  @Test
  public void evaluateAllowPartialDateAndMissingLeadingZeros() {
    ISODateValueEvaluationRule rule =
      ISODateValueEvaluationRuleBuilder.create().allowPartialDate().allowMissingLeadingZeros().build();

    assertEquals(Result.PASSED, rule.evaluate("2014").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-08").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-8").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-8-7").getResult());

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  private void testAlwaysValidString(ISODateValueEvaluationRule rule) {
    // should be valid
    assertEquals(Result.PASSED, rule.evaluate("20141010").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-10-10").getResult());

    // empty string is ignored
    assertEquals(Result.SKIPPED, rule.evaluate("").getResult());
  }

  private void testNeverValidString(ISODateValueEvaluationRule rule) {
    assertNotNull(rule.evaluate("201411"));
    assertNotNull(rule.evaluate("21-10-2014"));
  }

}
