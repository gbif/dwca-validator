package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRule.InvalidCharacterEvaluationRuleBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Ensure InvalidCharacterEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class InvalidCharacterEvaluationRuleTest {

  // never allowed char
  private static final char NULL_CHAR = 0;
  private static final char ESCAPE_CHAR = 27;

  // replacement character normally seen in wrong encoding situation
  private static final String REPLACEMENT_CHAR = "\uFFFD";

  // allowed if allowFormattingWhiteSpace() is used
  private static final String ENDLINE = System.getProperty("line.separator");
  private static final char VERTICAL_TAB_CHAR = 11;

  @Test
  public void evaluateFormattingWhiteSpaceAllowed() {
    InvalidCharacterEvaluationRule rule =
      InvalidCharacterEvaluationRuleBuilder.create().allowFormattingWhiteSpace().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertEquals(Result.PASSED, rule.evaluate("test" + VERTICAL_TAB_CHAR).getResult());
    assertEquals(Result.PASSED, rule.evaluate("test" + REPLACEMENT_CHAR).getResult());
    assertEquals(Result.PASSED, rule.evaluate("test" + ENDLINE).getResult());
  }

  @Test
  public void evaluateNoFormattingWhiteSpaceAllowed() {
    InvalidCharacterEvaluationRule rule = InvalidCharacterEvaluationRuleBuilder.create().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertTrue(rule.evaluate("test\t2").resultIsOneOf(Result.WARNING, Result.ERROR));
    assertTrue(rule.evaluate("test" + ENDLINE).resultIsOneOf(Result.WARNING, Result.ERROR));
    assertEquals(Result.PASSED, rule.evaluate("test" + REPLACEMENT_CHAR).getResult());
  }

  @Test
  public void evaluateNoReplacementCharAllowed() {
    InvalidCharacterEvaluationRule rule =
      InvalidCharacterEvaluationRuleBuilder.create().rejectReplacementChar().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertTrue(rule.evaluate("test" + REPLACEMENT_CHAR).resultIsOneOf(Result.ERROR, Result.WARNING));
  }

  private void testAlwaysValidString(InvalidCharacterEvaluationRule rule) {
    assertEquals(Result.PASSED, rule.evaluate("test").getResult());
    assertEquals(Result.PASSED, rule.evaluate("test 2").getResult());
    assertEquals(Result.PASSED, rule.evaluate("éä@%&*").getResult());

    // empty string passed
    assertEquals(Result.PASSED, rule.evaluate("").getResult());
    // null should be skipped
    assertEquals(Result.SKIPPED, rule.evaluate(null).getResult());
  }

  private void testNeverValidString(InvalidCharacterEvaluationRule rule) {
    assertTrue(rule.evaluate("test" + NULL_CHAR).resultIsOneOf(Result.ERROR, Result.WARNING));
    assertTrue(rule.evaluate("test" + ESCAPE_CHAR).resultIsOneOf(Result.ERROR, Result.WARNING));
  }
}
