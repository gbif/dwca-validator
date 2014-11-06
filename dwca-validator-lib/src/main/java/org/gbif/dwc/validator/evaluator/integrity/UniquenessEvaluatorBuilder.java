package org.gbif.dwc.validator.evaluator.integrity;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.configuration.UniquenessEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;

/**
 * Builder of UniquenessEvaluator object.
 * Return UniquenessEvaluator is NOT totally immutable due to file access.
 * 
 * @author cgendreau
 */
public class UniquenessEvaluatorBuilder implements RecordEvaluatorBuilder {

  private final UniquenessEvaluatorConfiguration configuration = new UniquenessEvaluatorConfiguration();

  private UniquenessEvaluatorBuilder(EvaluationContext evaluatorContext) {
    this.configuration.setEvaluatorContext(evaluatorContext);
  }

  /**
   * Create with default value. Using coreId, ValidationContext.CORE
   * 
   * @return
   */
  public static UniquenessEvaluatorBuilder builder() {
    return new UniquenessEvaluatorBuilder(EvaluationContext.CORE);
  }

  /**
   * Build UniquenessEvaluator object.
   * 
   * @return
   * @throws NullPointerException
   * @throws IllegalStateException
   * @throws IOException
   */
  @Override
  public UniquenessEvaluator build() throws IllegalStateException {
    Preconditions.checkNotNull(configuration.getEvaluatorContext());

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
   * Set on which ConceptTerm the evaluation should be made.
   * Override default values.
   * 
   * @param term
   * @param evaluatorContext context of the provided term
   * @return
   */
  public UniquenessEvaluatorBuilder on(ConceptTerm term, EvaluationContext evaluatorContext) {
    configuration.setTerm(term);
    configuration.setEvaluatorContext(evaluatorContext);
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
