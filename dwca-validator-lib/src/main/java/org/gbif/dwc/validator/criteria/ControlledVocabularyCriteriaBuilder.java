package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.ControlledVocabularyCriteriaConfiguration;

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
@RecordCriterionBuilderKey("controlledVocabularyCriteria")
public class ControlledVocabularyCriteriaBuilder implements RecordCriteriaBuilder {

  private final ControlledVocabularyCriteriaConfiguration configuration;

  /**
   * Private constructor, use builder() method.
   */
  private ControlledVocabularyCriteriaBuilder() {
    configuration = new ControlledVocabularyCriteriaConfiguration();
  }

  public ControlledVocabularyCriteriaBuilder(ControlledVocabularyCriteriaConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Creates a ControlledVocabularyEvaluationRuleBuilder.
   * 
   * @return
   */
  public static ControlledVocabularyCriteriaBuilder builder() {
    return new ControlledVocabularyCriteriaBuilder();
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

  public ControlledVocabularyCriteriaBuilder onTerm(Term term) {
    configuration.setTerm(term);
    return this;
  }

  /**
   * Load the controlled vocabulary from a file (currently text file only).
   * 
   * @param dictonaryPath
   * @return
   */
  public ControlledVocabularyCriteriaBuilder useDictionaryAt(String dictonaryPath) {
    configuration.setDictionaryPath(dictonaryPath);
    return this;
  }

  /**
   * Used controlled vocabulary represented by the Set.
   * 
   * @param vocabularySet
   * @return
   */
  public ControlledVocabularyCriteriaBuilder useVocabularySet(Set<String> vocabularySet) {
    configuration.setVocabularySet(vocabularySet);
    return this;
  }

  /**
   * Build an immutable ControlledVocabularyCriteria instance
   * 
   * @return immutable ControlledVocabularyEvaluationRule
   * @throws IllegalStateException
   */
  @Override
  public RecordCriteria build() throws IllegalStateException {
    if (configuration.getTerm() == null) {
      throw new IllegalStateException("ControlledVocabularyEvaluationRule must be built on a Term.");
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

    return new ControlledVocabularyCriteria(configuration);
  }
}
