package org.gbif.dwc.validator.criteria.dataset;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.DatasetCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.UniquenessCriterionConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder of UniquenessCriterion object.
 * Returned UniquenessCriterion is NOT totally immutable due to file access.
 * 
 * @author cgendreau
 */
@DatasetCriterionBuilderKey("uniquenessCriterion")
public class UniquenessCriterionBuilder implements DatasetCriterionBuilder {

  private final UniquenessCriterionConfiguration configuration;

  private UniquenessCriterionBuilder() {
    this.configuration = new UniquenessCriterionConfiguration();
  }

  public UniquenessCriterionBuilder(UniquenessCriterionConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Create with default value. Using coreId.
   * 
   * @return
   */
  public static UniquenessCriterionBuilder builder() {
    return new UniquenessCriterionBuilder();
  }

  /**
   * Get a new builder instance using the provided UniquenessCriterionConfiguration.
   * 
   * @param configuration
   * @return
   */
  public static UniquenessCriterionBuilder builder(UniquenessCriterionConfiguration configuration) {
    return new UniquenessCriterionBuilder(configuration);
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
  UniquenessCriterion innerBuild() throws IllegalStateException {

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
      return new UniquenessCriterion(configuration);
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
  public UniquenessCriterionBuilder on(Term term) {
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
  public UniquenessCriterionBuilder on(Term term, EvaluationContext evaluationContext, String rowType) {
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
  public UniquenessCriterionBuilder workingFolder(File workingFolder) {
    configuration.setWorkingFolder(workingFolder);
    return this;
  }
}
