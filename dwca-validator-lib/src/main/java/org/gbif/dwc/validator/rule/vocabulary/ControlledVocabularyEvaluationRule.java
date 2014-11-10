package org.gbif.dwc.validator.rule.vocabulary;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

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
   * Container object holding ControlledVocabularyEvaluationRule configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

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

  /**
   * Builder used to customized, if needed, the ControlledVocabularyEvaluation.
   * Also ensure usage of immutable object.
   * 
   * @author cgendreau
   */
  public static class ControlledVocabularyEvaluationRuleBuilder {

    private final Configuration configuration;

    /**
     * Private constructor, use create() method.
     */
    private ControlledVocabularyEvaluationRuleBuilder() {
      configuration = new Configuration();
    }

    /**
     * Creates a ControlledVocabularyEvaluationRuleBuilder.
     * 
     * @return
     */
    public static ControlledVocabularyEvaluationRuleBuilder create() {
      return new ControlledVocabularyEvaluationRuleBuilder();
    }

    protected static Set<String> toVocabularySet(String dictionaryPath) {
      if (FilenameUtils.isExtension(dictionaryPath, "txt")) {
        File dictionaryFile = new File(dictionaryPath);
        try {
          List<String> dictionaryList = FileUtils.readLines(dictionaryFile, Charsets.UTF_8);
          return new HashSet<String>(dictionaryList);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    /**
     * Build an immutable ControlledVocabularyEvaluationRule instance
     * 
     * @return immutable ControlledVocabularyEvaluationRule
     * @throws IllegalStateException
     */
    public ControlledVocabularyEvaluationRule build() throws IllegalStateException {
      if (configuration.getTerm() == null) {
        throw new IllegalStateException("ControlledVocabularyEvaluationRule must be built on a ConceptTerm.");
      }
      boolean vocabularySetProvided =
        (configuration.getVocabularySet() != null && !configuration.getVocabularySet().isEmpty());
      boolean dictionaryPathProvided = StringUtils.isNotBlank(configuration.getDictionaryPath());

      if (vocabularySetProvided == dictionaryPathProvided) {
        throw new IllegalStateException(
          "ControlledVocabularyEvaluationRule must be built on at least one vocabulary entry or a dictionnary file.");
      }
      return new ControlledVocabularyEvaluationRule(configuration);
    }

    public ControlledVocabularyEvaluationRuleBuilder onTerm(ConceptTerm term) {
      configuration.setTerm(term);
      return this;
    }

    /**
     * Load the controlled vocabulary from a file (currently text file only).
     * 
     * @param dictonaryPath
     * @return
     */
    public ControlledVocabularyEvaluationRuleBuilder useDictionaryAt(String dictonaryPath) {
      configuration.setDictionaryPath(dictonaryPath);
      return this;
    }

    /**
     * Used controlled vocabulary represented by the Set.
     * 
     * @param vocabularySet
     * @return
     */
    public ControlledVocabularyEvaluationRuleBuilder useVocabularySet(Set<String> vocabularySet) {
      configuration.setVocabularySet(vocabularySet);
      return this;
    }
  }

  private final ConceptTerm term;
  private final Set<String> vocabularySet;

  private ControlledVocabularyEvaluationRule(Configuration configuration) {
    this.term = configuration.getTerm();
    if (configuration.getVocabularySet() != null) {
      this.vocabularySet = Collections.unmodifiableSet(configuration.vocabularySet);
    } else {
      if (StringUtils.isNotBlank(configuration.getDictionaryPath())) {
        this.vocabularySet =
          ControlledVocabularyEvaluationRuleBuilder.toVocabularySet(configuration.getDictionaryPath());
      } else {
        this.vocabularySet = null;
      }
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
