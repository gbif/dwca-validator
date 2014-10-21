package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.rule.value.RegexValueEvaluationRule.RegexValueEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Ensure RegexValueEvaluationRul object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class RegexValueEvaluationRuleTest {

  @Test
  public void evaluate() {
    RegexValueEvaluationRule rule = RegexValueEvaluationRuleBuilder.create().usingRegex("[hc]at").build();

    // should passed
    assertEquals(Result.PASSED, rule.evaluate("cat").getResult());
    assertEquals(Result.PASSED, rule.evaluate("hat").getResult());

    // should not passed
    assertTrue(rule.evaluate("bat").resultIsOneOf(Result.WARNING, Result.ERROR));
    assertTrue(rule.evaluate("a cat").resultIsOneOf(Result.WARNING, Result.ERROR));
  }

}
