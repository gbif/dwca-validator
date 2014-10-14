package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule.BlankValueEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    assertNotNull(rule.evaluate(""));
    assertNotNull(rule.evaluate(null));
    assertNotNull(rule.evaluate(" "));
    assertNotNull(rule.evaluate("\t"));

    // should be valid
    assertNull(rule.evaluate("a"));
    assertNull(rule.evaluate(" a"));

    // simply demonstrate that this would be considered valid. Maybe the rule should accommodate that.
    assertNull(rule.evaluate("\"\""));
  }


}
