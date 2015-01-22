package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ControlledVocabularyCriteriaTest {

  private Record buildMockRecord(String occID, String country, String basisOfRecord) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {DwcTerm.country,
      DwcTerm.basisOfRecord}, new String[] {country, basisOfRecord});
  }

  @Test
  public void testControlledVocabularyFromFile() {

    File testFile = null;
    try {
      testFile = new File(this.getClass().getResource("/dictionary/european_union_country.txt").toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }

    RecordCriteria criteria =
      ControlledVocabularyCriteriaBuilder.builder().onTerm(DwcTerm.country).useDictionaryAt(testFile.getAbsolutePath())
        .build();

    Optional<ValidationResult> result =
      criteria.validate(buildMockRecord("1", "Spain", "PreservedSpecimen"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // should not passed
    result = criteria.validate(buildMockRecord("1", "xyz", "PreservedSpecimen"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));
  }

  @Test
  public void testControlledVocabularyFromSet() {

    Set<String> vocabulary = new HashSet<String>();
    vocabulary.add("PreservedSpecimen");

    RecordCriteria criteria =
      ControlledVocabularyCriteriaBuilder.builder().onTerm(DwcTerm.basisOfRecord).useVocabularySet(vocabulary).build();

    Optional<ValidationResult> result =
      criteria.validate(buildMockRecord("1", "Spain", "PreservedSpecimen"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationPassed(result));

    // should not passed
    result = criteria.validate(buildMockRecord("1", "Spain", "Gulo Gulo"), EvaluationContext.CORE);
    assertTrue(TestEvaluationResultHelper.validationFailed(result));
  }

  @Test(expected = IllegalStateException.class)
  public void testBuilderBehavior() {
    ControlledVocabularyCriteriaBuilder.builder().onTerm(DwcTerm.basisOfRecord).build();
  }

}
