package org.gbif.dwc.validator.rule.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleConfigurationKey;

import java.util.Set;

/**
 * Container object holding ControlledVocabularyEvaluationRule configurations.
 * 
 * @author cgendreau
 */
@EvaluationRuleConfigurationKey
public class ControlledVocabularyEvaluationRuleConfiguration {

  private Term term;
  private String dictionaryPath;
  private Set<String> vocabularySet;

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
