package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Ensure ISODateValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class ISODateValueEvaluationRuleTest {

  @Test
  public void evaluate() {
    ISODateValueEvaluationRule rule = ISODateValueEvaluationRuleBuilder.builder().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Validate the allowMissingLeadingZeros option alone
   */
  @Test
  public void evaluateAllowMissingLeadingZeros() {
    ISODateValueEvaluationRule rule = ISODateValueEvaluationRuleBuilder.builder().allowMissingLeadingZeros().build();

    assertEquals(Result.PASSED, rule.evaluate("2014-8-7").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-08-7").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-8-07").getResult());

    // But we don't accept partial dates
    assertTrue(rule.evaluate("2014-8").resultIsOneOf(Result.WARNING, Result.ERROR));

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Validate the allowPartialDate option alone
   */
  @Test
  public void evaluateAllowPartialDate() {
    ISODateValueEvaluationRule rule = ISODateValueEvaluationRuleBuilder.builder().allowPartialDate().build();

    assertEquals(Result.PASSED, rule.evaluate("2014").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-08").getResult());

    // But we don't accept missing leading zero
    assertTrue(rule.evaluate("2014-8").resultIsOneOf(Result.WARNING, Result.ERROR));

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Validate both allowPartialDate and allowPartialDate options
   */
  @Test
  public void evaluateAllowPartialDateAndMissingLeadingZeros() {
    ISODateValueEvaluationRule rule =
      ISODateValueEvaluationRuleBuilder.builder().allowPartialDate().allowMissingLeadingZeros().build();

    assertEquals(Result.PASSED, rule.evaluate("2014").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-08").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-8").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-8-7").getResult());

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Dates that must always be evaluated as valid.
   * 
   * @param rule
   */
  private void testAlwaysValidString(ISODateValueEvaluationRule rule) {
    // should be valid
    assertEquals(Result.PASSED, rule.evaluate("20141010").getResult());
    assertEquals(Result.PASSED, rule.evaluate("2014-10-10").getResult());

    // empty string is ignored
    assertEquals(Result.SKIPPED, rule.evaluate("").getResult());
  }

  /**
   * Dates that must never be evaluated as valid.
   * 
   * @param rule
   */
  private void testNeverValidString(ISODateValueEvaluationRule rule) {
    assertTrue(rule.evaluate("201411").resultIsOneOf(Result.ERROR, Result.WARNING));
    assertTrue(rule.evaluate("21-10-2014").resultIsOneOf(Result.ERROR, Result.WARNING));

    // non existing month
    assertTrue(rule.evaluate("2014-13-08").resultIsOneOf(Result.ERROR, Result.WARNING));

    // non existing date
    assertTrue(rule.evaluate("2014-02-30").resultIsOneOf(Result.ERROR, Result.WARNING));
  }

}
