package org.gbif.dwc.validator.rule.vocabulary;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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

    EvaluationRuleIF<String> rule =
      ControlledVocabularyEvaluationRuleBuilder.builder().onTerm(DwcTerm.country)
        .useDictionaryAt(testFile.getAbsolutePath()).build();

    assertEquals(Result.PASSED, rule.evaluate("Spain").getResult());

    // should not passed
    assertTrue(rule.evaluate("xyz").resultIsOneOf(Result.WARNING, Result.ERROR));
  }

  @Test
  public void evaluateControlledVocabularyFromSet() {

    Set<String> vocabulary = new HashSet<String>();
    vocabulary.add("PreservedSpecimen");

    EvaluationRuleIF<String> rule =
      ControlledVocabularyEvaluationRuleBuilder.builder().onTerm(DwcTerm.basisOfRecord).useVocabularySet(vocabulary)
        .build();

    assertEquals(Result.PASSED, rule.evaluate("PreservedSpecimen").getResult());

    // should not passed
    assertTrue(rule.evaluate("Gulo Gulo").resultIsOneOf(Result.WARNING, Result.ERROR));
  }

  @Test(expected = IllegalStateException.class)
  public void testBuilderBehavior() {
    ControlledVocabularyEvaluationRuleBuilder.builder().onTerm(DwcTerm.basisOfRecord).build();
  }

}
