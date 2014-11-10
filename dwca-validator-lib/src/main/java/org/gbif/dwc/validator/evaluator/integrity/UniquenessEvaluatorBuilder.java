package org.gbif.dwc.validator.evaluator.integrity;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.configuration.UniquenessEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder of UniquenessEvaluator object.
 * Return UniquenessEvaluator is NOT totally immutable due to file access.
 * 
 * @author cgendreau
 */
public class UniquenessEvaluatorBuilder implements RecordEvaluatorBuilder {

  private final UniquenessEvaluatorConfiguration configuration;

  private UniquenessEvaluatorBuilder() {
    this.configuration = new UniquenessEvaluatorConfiguration();
    this.configuration.setEvaluationContextRestriction(EvaluationContext.CORE);
  }

  public UniquenessEvaluatorBuilder(UniquenessEvaluatorConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Create with default value. Using coreId.
   * 
   * @return
   */
  public static UniquenessEvaluatorBuilder builder() {
    return new UniquenessEvaluatorBuilder();
  }

  /**
   * Get a new builder instance using the provided UniquenessEvaluatorConfiguration.
   * 
   * @param configuration
   * @return
   */
  public static UniquenessEvaluatorBuilder builder(UniquenessEvaluatorConfiguration configuration) {
    return new UniquenessEvaluatorBuilder(configuration);
  }

  /**
   * Build UniquenessEvaluator object.
   * 
   * @return
   * @throws IllegalStateException
   * @throws IOException
   */
  @Override
  public UniquenessEvaluator build() throws IllegalStateException {

    Preconditions.checkState(configuration.getEvaluationContextRestriction() != null,
      "EvaluationContextRestriction must be provided");

    if (configuration.getEvaluationContextRestriction() == EvaluationContext.EXT) {
      Preconditions.checkState(StringUtils.isNotBlank(configuration.getRowTypeRestriction()),
        "RowTypeRestriction must be provided for extension");
      Preconditions.checkState(configuration.getTerm() != null, "A Term must be provided for extension");
    }

    // maybe working folder should be mandatory?
    if (configuration.getWorkingFolder() != null) {
      Preconditions.checkState(configuration.getWorkingFolder().exists()
        && configuration.getWorkingFolder().isDirectory(), "workingFolder must exist as a directory");
    } else {
      configuration.setWorkingFolder(new File("."));
    }

    try {
      return new UniquenessEvaluator(configuration);
    } catch (IOException e) {
      // Not sure it's the best solution
      throw new IllegalStateException(e);
    }
  }

  /**
   * Set on which ConceptTerm the evaluation should be made on core file.
   * 
   * @param term
   * @return
   */
  public UniquenessEvaluatorBuilder on(ConceptTerm term) {
    configuration.setTerm(term);
    return this;
  }

  /**
   * Set on which ConceptTerm and rowType the evaluation should be made on extension.
   * 
   * @param term
   * @param evaluationContext
   * @param rowType
   * @return
   */
  public UniquenessEvaluatorBuilder on(ConceptTerm term, EvaluationContext evaluationContext, String rowType) {
    configuration.setTerm(term);
    configuration.setEvaluationContextRestriction(evaluationContext);
    configuration.setRowTypeRestriction(rowType);
    return this;
  }

  /**
   * Set working folder to save temporary files.
   * 
   * @param workingFolder
   * @return
   */
  public UniquenessEvaluatorBuilder workingFolder(File workingFolder) {
    configuration.setWorkingFolder(workingFolder);
    return this;
  }
}
