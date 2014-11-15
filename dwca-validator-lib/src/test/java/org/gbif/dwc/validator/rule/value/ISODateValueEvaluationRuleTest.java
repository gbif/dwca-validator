package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.EvaluationRule;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure ISODateValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class ISODateValueEvaluationRuleTest {

  @Test
  public void evaluate() {
    EvaluationRule<String> rule = ISODateValueEvaluationRuleBuilder.builder().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Validate the allowMissingLeadingZeros option alone
   */
  @Test
  public void evaluateAllowMissingLeadingZeros() {
    EvaluationRule<String> rule = ISODateValueEvaluationRuleBuilder.builder().allowMissingLeadingZeros().build();

    assertTrue(rule.evaluate("2014-8-7").passed());
    assertTrue(rule.evaluate("2014-08-7").passed());
    assertTrue(rule.evaluate("2014-8-07").passed());

    // But we don't accept partial dates
    assertTrue(rule.evaluate("2014-8").failed());

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Validate the allowPartialDate option alone
   */
  @Test
  public void evaluateAllowPartialDate() {
    EvaluationRule<String> rule = ISODateValueEvaluationRuleBuilder.builder().allowPartialDate().build();

    assertTrue(rule.evaluate("2014").passed());
    assertTrue(rule.evaluate("2014-08").passed());

    // But we don't accept missing leading zero
    assertTrue(rule.evaluate("2014-8").failed());

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Validate both allowPartialDate and allowPartialDate options
   */
  @Test
  public void evaluateAllowPartialDateAndMissingLeadingZeros() {
    EvaluationRule<String> rule =
      ISODateValueEvaluationRuleBuilder.builder().allowPartialDate().allowMissingLeadingZeros().build();

    assertTrue(rule.evaluate("2014").passed());
    assertTrue(rule.evaluate("2014-08").passed());
    assertTrue(rule.evaluate("2014-8").passed());
    assertTrue(rule.evaluate("2014-8-7").passed());

    testAlwaysValidString(rule);
    testNeverValidString(rule);
  }

  /**
   * Dates that must always be evaluated as valid.
   * 
   * @param rule
   */
  private void testAlwaysValidString(EvaluationRule<String> rule) {
    // should be valid
    assertTrue(rule.evaluate("20141010").passed());
    assertTrue(rule.evaluate("2014-10-10").passed());

    // empty string is ignored
    assertTrue(rule.evaluate("").skipped());
  }

  /**
   * Dates that must never be evaluated as valid.
   * 
   * @param rule
   */
  private void testNeverValidString(EvaluationRule<String> rule) {
    assertTrue(rule.evaluate("201411").failed());
    assertTrue(rule.evaluate("21-10-2014").failed());

    // non existing month
    assertTrue(rule.evaluate("2014-13-08").failed());

    // non existing date
    assertTrue(rule.evaluate("2014-02-30").failed());
  }

}
