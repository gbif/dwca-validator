package org.gbif.dwc.validator.rule.configuration;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleConfigurationKey;

import java.util.Set;

/**
 * Container object holding ControlledVocabularyEvaluationRule configurations.
 * 
 * @author cgendreau
 */
@EvaluationRuleConfigurationKey
public class ControlledVocabularyEvaluationRuleConfiguration {

  private ConceptTerm term;
  private String dictionaryPath;
  private Set<String> vocabularySet;

  public String getDictionaryPath() {
    return dictionaryPath;
  }

  public ConceptTerm getTerm() {
    return term;
  }

  public Set<String> getVocabularySet() {
    return vocabularySet;
  }

  public void setDictionaryPath(String dictionaryPath) {
    this.dictionaryPath = dictionaryPath;
  }

  public void setTerm(ConceptTerm term) {
    this.term = term;
  }

  public void setVocabularySet(Set<String> vocabularySet) {
    this.vocabularySet = vocabularySet;
  }
}
