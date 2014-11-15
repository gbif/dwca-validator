package org.gbif.dwc.validator.rule.vocabulary;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.rule.EvaluationRule;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Ensure ControlledVocabularyEvaluationRule object obtained by the builder work as expected.
 * 
 * @author cgendreau
 */
public class ControlledVocabularyEvaluationRuleTest {

  @Test
  public void evaluateControlledVocabularyFromFile() {

    File testFile = null;
    try {
      testFile = new File(this.getClass().getResource("/dictionary/european_union_country.txt").toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }

    EvaluationRule<String> rule =
      ControlledVocabularyEvaluationRuleBuilder.builder().onTerm(DwcTerm.country)
        .useDictionaryAt(testFile.getAbsolutePath()).build();

    assertTrue(rule.evaluate("Spain").passed());

    // should not passed
    assertTrue(rule.evaluate("xyz").failed());
  }

  @Test
  public void evaluateControlledVocabularyFromSet() {

    Set<String> vocabulary = new HashSet<String>();
    vocabulary.add("PreservedSpecimen");

    EvaluationRule<String> rule =
      ControlledVocabularyEvaluationRuleBuilder.builder().onTerm(DwcTerm.basisOfRecord).useVocabularySet(vocabulary)
        .build();

    assertTrue(rule.evaluate("PreservedSpecimen").passed());

    // should not passed
    assertTrue(rule.evaluate("Gulo Gulo").failed());
  }

  @Test(expected = IllegalStateException.class)
  public void testBuilderBehavior() {
    ControlledVocabularyEvaluationRuleBuilder.builder().onTerm(DwcTerm.basisOfRecord).build();
  }

}
