package org.gbif.dwc.validator.rule.vocabulary;

import org.gbif.dwc.terms.DwcTerm;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Ensure ControlledVocabularyEvaluationRule object obtained by the builder work as expected.
 * 
 * @author cgendreau
 */
public class ControlledVocabularyEvaluationRuleTest {

  @Test
  public void evaluateControlledVocabulary() {

    Set<String> vocabulary = new HashSet<String>();
    vocabulary.add("PreservedSpecimen");

    ControlledVocabularyEvaluationRule rule =
      ControlledVocabularyEvaluationRule.createRule(DwcTerm.basisOfRecord, vocabulary).build();

    assertNull(rule.evaluate("PreservedSpecimen"));
    assertNotNull(rule.evaluate("Gulo Gulo"));
  }

  @Test(expected = IllegalStateException.class)
  public void testBuilderBehavior() {
    ControlledVocabularyEvaluationRule.createRule(DwcTerm.basisOfRecord, null).build();
  }

}
