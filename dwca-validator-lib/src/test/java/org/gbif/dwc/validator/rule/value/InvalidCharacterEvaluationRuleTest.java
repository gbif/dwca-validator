package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.rule.EvaluationRule;

import org.junit.Test;

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
    EvaluationRule<String> rule = InvalidCharacterEvaluationRuleBuilder.builder().allowFormattingWhiteSpace().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertTrue(rule.evaluate("test" + VERTICAL_TAB_CHAR).passed());
    assertTrue(rule.evaluate("test" + REPLACEMENT_CHAR).passed());
    assertTrue(rule.evaluate("test" + ENDLINE).passed());
  }

  @Test
  public void evaluateNoFormattingWhiteSpaceAllowed() {
    EvaluationRule<String> rule = InvalidCharacterEvaluationRuleBuilder.builder().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertTrue(rule.evaluate("test\t2").failed());
    assertTrue(rule.evaluate("test" + ENDLINE).failed());

    assertTrue(rule.evaluate("test" + REPLACEMENT_CHAR).passed());
  }

  @Test
  public void evaluateNoReplacementCharAllowed() {
    EvaluationRule<String> rule = InvalidCharacterEvaluationRuleBuilder.builder().rejectReplacementChar().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertTrue(rule.evaluate("test" + REPLACEMENT_CHAR).failed());
  }

  private void testAlwaysValidString(EvaluationRule<String> rule) {
    assertTrue(rule.evaluate("test").passed());
    assertTrue(rule.evaluate("test 2").passed());
    assertTrue(rule.evaluate("éä@%&*").passed());

    // empty string passed
    assertTrue(rule.evaluate("").passed());
    // null should be skipped
    assertTrue(rule.evaluate(null).skipped());
  }

  private void testNeverValidString(EvaluationRule<String> rule) {
    assertTrue(rule.evaluate("test" + NULL_CHAR).failed());
    assertTrue(rule.evaluate("test" + ESCAPE_CHAR).failed());
  }
}
