package org.gbif.dwc.validator.rule.vocabulary;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.rule.EvaluationRuleBuilder;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleBuilderKey;
import org.gbif.dwc.validator.rule.configuration.ControlledVocabularyEvaluationRuleConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder for ControlledVocabularyEvaluationRule object.
 * 
 * @author cgendreau
 */
@EvaluationRuleBuilderKey("controlledVocabularyEvaluationRule")
public class ControlledVocabularyEvaluationRuleBuilder implements EvaluationRuleBuilder {

  private final ControlledVocabularyEvaluationRuleConfiguration configuration;

  /**
   * Private constructor, use builder() method.
   */
  private ControlledVocabularyEvaluationRuleBuilder() {
    configuration = new ControlledVocabularyEvaluationRuleConfiguration();
  }

  public ControlledVocabularyEvaluationRuleBuilder(ControlledVocabularyEvaluationRuleConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Creates a ControlledVocabularyEvaluationRuleBuilder.
   * 
   * @return
   */
  public static ControlledVocabularyEvaluationRuleBuilder builder() {
    return new ControlledVocabularyEvaluationRuleBuilder();
  }

  /**
   * Read the dictionary from dictionaryPath and return the content in a Set.
   * 
   * @return
   * @throws IOException
   */
  private Set<String> toVocabularySet() throws IOException {
    if (FilenameUtils.isExtension(configuration.getDictionaryPath(), "txt")) {
      File dictionaryFile = new File(configuration.getDictionaryPath());

      List<String> dictionaryList = FileUtils.readLines(dictionaryFile, Charsets.UTF_8);
      return new HashSet<String>(dictionaryList);
    }
    return null;
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

  /**
   * Build an immutable ControlledVocabularyEvaluationRule instance
   * 
   * @return immutable ControlledVocabularyEvaluationRule
   * @throws IllegalStateException
   */
  @Override
  public EvaluationRuleIF<String> build() throws IllegalStateException {
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

    // read dictionary
    if (dictionaryPathProvided) {
      try {
        configuration.setVocabularySet(toVocabularySet());
      } catch (IOException ioEx) {
        throw new IllegalStateException("Can't read dictionary file at " + configuration.getDictionaryPath(), ioEx);
      }
    }

    return new ControlledVocabularyEvaluationRule(configuration);
  }
}
