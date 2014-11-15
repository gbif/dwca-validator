package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.value.RegexValueEvaluationRule.RegexValueEvaluationRuleBuilder;

import org.junit.Test;

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
    assertTrue(rule.evaluate("cat").passed());
    assertTrue(rule.evaluate("hat").passed());

    // should not passed
    assertTrue(rule.evaluate("bat").failed());
    assertTrue(rule.evaluate("a cat").failed());
  }

}
