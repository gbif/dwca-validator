package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.Result;

import java.util.Set;

/**
 * Container object holding ControlledVocabularyCriteria configurations.
 * 
 * @author cgendreau
 */
@CriterionConfigurationKey("controlledVocabularyCriteria")
public class ControlledVocabularyCriteriaConfiguration {

  private Result level = Result.ERROR;
  private String rowTypeRestriction;
  private Term term;
  private String dictionaryPath;
  private Set<String> vocabularySet;

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Result getLevel() {
    return level;
  }

  public void setLevel(Result level) {
    this.level = level;
  }

  public String getDictionaryPath() {
    return dictionaryPath;
  }

  public Term getTerm() {
    return term;
  }

  public Set<String> getVocabularySet() {
    return vocabularySet;
  }

  public void setDictionaryPath(String dictionaryPath) {
    this.dictionaryPath = dictionaryPath;
  }

  public void setTerm(Term term) {
    this.term = term;
  }

  public void setVocabularySet(Set<String> vocabularySet) {
    this.vocabularySet = vocabularySet;
  }
}
