package org.gbif.dwc.validator.rule.value;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Ensure InvalidCharacterEvaluationRule object obtained by the builder works as expected.
 * 
 * @author cgendreau
 */
public class InvalidCharacterEvaluationRuleTest {

  // never allowed char
  private static final char NULL_CHAR = 0;
  private static final char ESCAPE_CHAR = 27;

  // allowed if allowFormattingWhiteSpace() is used
  private static final String ENDLINE = System.getProperty("line.separator");
  private static final char VERTICAL_TAB_CHAR = 11;

  @Test
  public void evaluateFormattingWhiteSpaceAllowed() {
    InvalidCharacterEvaluationRule rule =
      InvalidCharacterEvaluationRule.createRule().allowFormattingWhiteSpace().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertNull(rule.evaluate("test" + VERTICAL_TAB_CHAR));
  }

  @Test
  public void evaluateNoFormattingWhiteSpaceAllowed() {
    InvalidCharacterEvaluationRule rule = InvalidCharacterEvaluationRule.createRule().build();

    testAlwaysValidString(rule);
    testNeverValidString(rule);

    assertNotNull(rule.evaluate("test\t2"));
    assertNotNull(rule.evaluate("test" + ENDLINE));
  }

  private void testAlwaysValidString(InvalidCharacterEvaluationRule rule) {
    assertNull(rule.evaluate("test"));
    assertNull(rule.evaluate("test 2"));
    assertNull(rule.evaluate("éä@%&*"));
  }

  private void testNeverValidString(InvalidCharacterEvaluationRule rule) {
    assertNotNull(rule.evaluate("test" + NULL_CHAR));
    assertNotNull(rule.evaluate("test" + ESCAPE_CHAR));
  }
}
