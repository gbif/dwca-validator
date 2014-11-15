package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule.BlankValueEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure BlankValueEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class BlankValueEvaluationRuleTest {

  @Test
  public void evaluate() {

    BlankValueEvaluationRule rule = BlankValueEvaluationRuleBuilder.create().build();

    // should send result (error)
    assertTrue(rule.evaluate("").failed());
    assertTrue(rule.evaluate(null).failed());
    assertTrue(rule.evaluate(" ").failed());
    assertTrue(rule.evaluate("\t").failed());

    // should be valid
    assertTrue(rule.evaluate("a").passed());
    assertTrue(rule.evaluate(" a").passed());

    // simply demonstrate that this would be considered valid. Maybe the rule should accommodate that.
    assertTrue(rule.evaluate("\"\"").passed());
  }
}
