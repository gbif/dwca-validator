package org.gbif.dwc.validator.evaluator.integrity;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.configuration.ReferenceUniqueEvaluatorConfiguration;
import org.gbif.dwc.validator.evaluator.configuration.UniquenessEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;

/**
 * Builder of ReferentialIntegrityEvaluator object.
 * Return ReferentialIntegrityEvaluator is NOT immutable due to file access.
 * 
 * @author cgendreau
 */
public class ReferenceEvaluatorBuilder implements RecordEvaluatorBuilder {

  private final ReferenceUniqueEvaluatorConfiguration configuration;
  private final UniquenessEvaluatorConfiguration uniquenessEvaluatorConfiguration;

  private ReferenceEvaluatorBuilder() {
    this.configuration = new ReferenceUniqueEvaluatorConfiguration();
    this.uniquenessEvaluatorConfiguration = new UniquenessEvaluatorConfiguration();
  }

  /**
   * Create with default value. Using coreId.
   * 
   * @return
   */
  public static ReferenceEvaluatorBuilder builder() {
    return new ReferenceEvaluatorBuilder();
  }

  /**
   * Build UniquenessEvaluator object.
   * 
   * @return
   * @throws NullPointerException
   * @throws IOException
   * @throws IllegalStateException
   */
  @Override
  public ReferenceUniqueEvaluator build() throws IllegalStateException {
    Preconditions.checkNotNull(configuration.getTerm());
    Preconditions.checkNotNull(configuration.getEvaluationContextRestriction());
    Preconditions.checkNotNull(configuration.getRowTypeRestriction());
    Preconditions.checkNotNull(uniquenessEvaluatorConfiguration);

    if (configuration.getWorkingFolder() != null) {
      Preconditions.checkState(configuration.getWorkingFolder().exists()
        && configuration.getWorkingFolder().isDirectory(), "workingFolder must exist as a directory");
    } else {
      configuration.setWorkingFolder(new File("."));
      uniquenessEvaluatorConfiguration.setWorkingFolder(new File("."));
    }

    // UniquenessEvaluatorBuilder will validate UniquenessEvaluator pre conditions
    UniquenessEvaluator uniquenessEvaluator =
      UniquenessEvaluatorBuilder.builder(uniquenessEvaluatorConfiguration).build();

    try {
      return new ReferenceUniqueEvaluator(configuration, uniquenessEvaluator);
    } catch (IOException e) {
      // Not sure it's the best solution
      throw new IllegalStateException(e);
    }
  }

  public ReferenceEvaluatorBuilder termRefersToUnique(ConceptTerm term, EvaluationContext evaluationContextRestriction,
    String rowTypeRestriction, ConceptTerm referedTerm, EvaluationContext referedEvaluationContextRestriction,
    String referedRowTypeRestriction) {
    this.configuration.setTerm(term);
    this.configuration.setEvaluationContextRestriction(evaluationContextRestriction);
    this.configuration.setRowTypeRestriction(rowTypeRestriction);

    this.uniquenessEvaluatorConfiguration.setTerm(referedTerm);
    this.uniquenessEvaluatorConfiguration.setEvaluationContextRestriction(referedEvaluationContextRestriction);
    this.uniquenessEvaluatorConfiguration.setRowTypeRestriction(referedRowTypeRestriction);

    return this;
  }

  /**
   * Should the evaluator accept multiple values using a defined separator.
   * e.g. 1234|2345
   * 
   * @param separator
   * @return
   */
  public ReferenceEvaluatorBuilder supportMultipleValues(String multipleValuesSeparator) {
    this.configuration.setMultipleValuesSeparator(multipleValuesSeparator);
    return this;
  }

  /**
   * Set working folder to save temporary files.
   * 
   * @param workingFolder
   * @return
   */
  public ReferenceEvaluatorBuilder workingFolder(File workingFolder) {
    this.configuration.setWorkingFolder(workingFolder);
    this.uniquenessEvaluatorConfiguration.setWorkingFolder(workingFolder);
    return this;
  }
}
