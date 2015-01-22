package org.gbif.dwc.validator.criteria.dataset;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.DatasetCriteriaBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.UniquenessCriteriaConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder of UniquenessCriteria object.
 * Returned UniquenessCriteria is NOT totally immutable due to file access.
 * 
 * @author cgendreau
 */
@DatasetCriteriaBuilderKey("uniquenessCriteria")
public class UniquenessCriteriaBuilder implements DatasetCriteriaBuilder {

  private final UniquenessCriteriaConfiguration configuration;

  private UniquenessCriteriaBuilder() {
    this.configuration = new UniquenessCriteriaConfiguration();
  }

  public UniquenessCriteriaBuilder(UniquenessCriteriaConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Create with default value. Using coreId.
   * 
   * @return
   */
  public static UniquenessCriteriaBuilder builder() {
    return new UniquenessCriteriaBuilder();
  }

  /**
   * Get a new builder instance using the provided UniquenessEvaluatorConfiguration.
   * 
   * @param configuration
   * @return
   */
  public static UniquenessCriteriaBuilder builder(UniquenessCriteriaConfiguration configuration) {
    return new UniquenessCriteriaBuilder(configuration);
  }

  /**
   * Build UniquenessEvaluator object.
   * 
   * @return
   * @throws IllegalStateException
   * @throws IOException
   */
  @Override
  public DatasetCriteria build() throws IllegalStateException {
    return innerBuild();
  }

  /**
   * innerBuild allows same package builders to get the StatefulRecordEvaluator as concrete class.
   * Mainly used for evaluator composition.
   */
  UniquenessCriteria innerBuild() throws IllegalStateException {

    // use CORE context as default value
    if (configuration.getEvaluationContextRestriction() == null) {
      this.configuration.setEvaluationContextRestriction(EvaluationContext.CORE);
    } else if (configuration.getEvaluationContextRestriction() == EvaluationContext.EXT) {
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
      return new UniquenessCriteria(configuration);
    } catch (IOException e) {
      // Not sure it's the best solution
      throw new IllegalStateException(e);
    }
  }

  /**
   * Set on which Term the evaluation should be made on core file.
   * 
   * @param term
   * @return
   */
  public UniquenessCriteriaBuilder on(Term term) {
    configuration.setTerm(term);
    return this;
  }

  /**
   * Set on which Term and rowType the evaluation should be made on extension.
   * 
   * @param term
   * @param evaluationContext
   * @param rowType
   * @return
   */
  public UniquenessCriteriaBuilder on(Term term, EvaluationContext evaluationContext, String rowType) {
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
  public UniquenessCriteriaBuilder workingFolder(File workingFolder) {
    configuration.setWorkingFolder(workingFolder);
    return this;
  }
}
