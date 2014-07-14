package org.gbif.dwc.validator.rule.vocabulary;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.Collections;
import java.util.Set;

/**
 * Rule use to ensure a String is matching against a controlled vocabulary.
 * TODO: add ability to set 'preferred' and 'alternative' string
 * TODO: should we only compare lowerCase (at least by default)?
 * TODO: should we accept ascii folding setting? If yes, how it will (should) react with characters like 保存標本
 * 
 * @author cgendreau
 */
public class ControlledVocabularyEvaluationRule implements EvaluationRuleIF<String> {

  /**
   * Builder used to customized, if needed, the ControlledVocabularyEvaluation.
   * Also ensure usage of immutable object.
   * 
   * @author cgendreau
   */
  public static class ControlledVocabularyEvaluationRuleBuilder {

    private final ConceptTerm term;
    private final Set<String> vocabularySet;

    /**
     * Private constructor, use create() method.
     */
    private ControlledVocabularyEvaluationRuleBuilder(ConceptTerm term, Set<String> vocabularySet) {
      this.term = term;
      this.vocabularySet = vocabularySet;
    }

    /**
     * Creates a ControlledVocabularyEvaluationRuleBuilder.
     * Use build to get the ControlledVocabularyEvaluationRule instance.
     * 
     * @param term
     * @param vocabularySet
     * @return
     */
    public static ControlledVocabularyEvaluationRuleBuilder create(ConceptTerm term, Set<String> vocabularySet) {
      return new ControlledVocabularyEvaluationRuleBuilder(term, vocabularySet);
    }

    /**
     * Build an immutable ControlledVocabularyEvaluationRule instance
     * 
     * @return immutable ControlledVocabularyEvaluationRule
     * @throws IllegalStateException
     */
    public ControlledVocabularyEvaluationRule build() throws IllegalStateException {
      if (term == null || vocabularySet == null || vocabularySet.size() < 1) {
        throw new IllegalStateException(
          "ControlledVocabularyEvaluationRule must be built on a ConceptTerm and at least one vocabulary entry.");
      }
      return new ControlledVocabularyEvaluationRule(term, vocabularySet);
    }

  }

  private final ConceptTerm term;
  private final Set<String> vocabularySet;

  private ControlledVocabularyEvaluationRule(ConceptTerm term, Set<String> vocabularySet) {
    this.term = term;
    this.vocabularySet = Collections.unmodifiableSet(vocabularySet);
  }

  /**
   * Simple alias of ControlledVocabularyEvaluationRuleBuilder.create() for code readability so we can use
   * ControlledVocabularyEvaluationRuleBuilder.createRule() instead of
   * ControlledVocabularyEvaluationRule.ControlledVocabularyEvaluationRuleBuilder.create()
   * 
   * @param term
   * @param vocabularySet
   * @return default InvalidCharacterEvaluationRuleBuilder
   */
  public static ControlledVocabularyEvaluationRuleBuilder createRule(ConceptTerm term, Set<String> vocabularySet) {
    return ControlledVocabularyEvaluationRuleBuilder.create(term, vocabularySet);
  }

  @Override
  public ValidationResultElement evaluate(String str) {
    if (str == null) {
      return null;
    }

    if (!vocabularySet.contains(str)) {
      return new ValidationResultElement(ContentValidationType.RECORD_CONTENT, Result.ERROR,
        ArchiveValidatorConfig.getLocalizedString("rule.controlled_vocabulary", str, term.simpleName()));
    }
    return null;
  }
}
