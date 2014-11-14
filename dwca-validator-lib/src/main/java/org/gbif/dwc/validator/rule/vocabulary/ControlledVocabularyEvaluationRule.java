package org.gbif.dwc.validator.rule.vocabulary;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRule;
import org.gbif.dwc.validator.rule.configuration.ControlledVocabularyEvaluationRuleConfiguration;

import java.util.Collections;
import java.util.Set;

/**
 * Rule use to ensure a String is matching against a controlled vocabulary.
 * ControlledVocabularyEvaluationRule objects are immutable.
 * TODO: add ability to set 'preferred' and 'alternative' string
 * TODO: should we only compare lowerCase (at least by default)?
 * TODO: should we accept ascii folding setting? If yes, how it will (should) react with characters like 保存標本
 * 
 * @author cgendreau
 */
class ControlledVocabularyEvaluationRule implements EvaluationRule<String> {

  private final ConceptTerm term;
  private final Set<String> vocabularySet;

  ControlledVocabularyEvaluationRule(ControlledVocabularyEvaluationRuleConfiguration configuration) {
    this.term = configuration.getTerm();

    if (configuration.getVocabularySet() != null) {
      this.vocabularySet = Collections.unmodifiableSet(configuration.getVocabularySet());
    } else {
      vocabularySet = null;
    }
  }

  @Override
  public ValidationResultElement evaluate(String str) {
    if (str == null) {
      return ValidationResultElement.SKIPPED;
    }

    if (!vocabularySet.contains(str)) {
      return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.ERROR,
        ValidatorConfig.getLocalizedString("rule.controlled_vocabulary", str, term.simpleName()));
    }
    return ValidationResultElement.PASSED;
  }
}
