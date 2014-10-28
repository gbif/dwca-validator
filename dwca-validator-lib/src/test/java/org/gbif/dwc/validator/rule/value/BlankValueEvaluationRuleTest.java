package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.rule.value.BlankValueEvaluationRule.BlankValueEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    assertEquals(Result.PASSED, rule.evaluate("a").getResult());
    assertEquals(Result.PASSED, rule.evaluate(" a").getResult());

    // simply demonstrate that this would be considered valid. Maybe the rule should accommodate that.
    assertEquals(Result.PASSED, rule.evaluate("\"\"").getResult());
  }
}
