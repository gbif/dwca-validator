package org.gbif.dwc.validator.criteria.dataset;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.DatasetCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.ReferenceUniqueCriterionConfiguration;
import org.gbif.dwc.validator.criteria.configuration.UniquenessCriterionConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder of ReferenceUniqueCriteria object.
 * Returned ReferenceUniqueCriteria is NOT immutable due to file access.
 * 
 * @author cgendreau
 */
@DatasetCriterionBuilderKey("referenceUniqueCriterion")
public class ReferenceUniqueCriterionBuilder implements DatasetCriterionBuilder {

  private final ReferenceUniqueCriterionConfiguration configuration;
  private final UniquenessCriterionConfiguration uniquenessCriterionConfiguration;

  private ReferenceUniqueCriterionBuilder() {
    this.configuration = new ReferenceUniqueCriterionConfiguration();
    this.uniquenessCriterionConfiguration = new UniquenessCriterionConfiguration();
  }

  public ReferenceUniqueCriterionBuilder(ReferenceUniqueCriterionConfiguration configuration,
    UniquenessCriterionConfiguration uniquenessCriterionConfiguration) {
    this.configuration = configuration;
    this.uniquenessCriterionConfiguration = uniquenessCriterionConfiguration;
  }

  /**
   * Create with default value. Using coreId.
   * 
   * @return
   */
  public static ReferenceUniqueCriterionBuilder builder() {
    return new ReferenceUniqueCriterionBuilder();
  }

  /**
   * Build UniquenessEvaluator object.
   * 
   * @return
   * @throws IllegalStateException
   */
  @Override
  public DatasetCriteria build() throws IllegalStateException {

    // if no 'term' is set the id will be used to test as 'star' records
    if (configuration.getTerm() != null) {
      Preconditions.checkState(StringUtils.isNotBlank(configuration.getRowTypeRestriction()),
        "rowTypeRestriction must be provided if a specific term is specified.");
      Preconditions.checkState(uniquenessCriterionConfiguration != null,
        "uniquenessCriterionConfiguration must be provided if a specific term is specified.");
    }

    // use EXT context as default value
    if (configuration.getEvaluationContextRestriction() == null) {
      this.configuration.setEvaluationContextRestriction(EvaluationContext.EXT);
    }

    if (configuration.getWorkingFolder() != null) {
      Preconditions.checkState(configuration.getWorkingFolder().exists()
        && configuration.getWorkingFolder().isDirectory(), "workingFolder must exist as a directory");
    } else {
      configuration.setWorkingFolder(new File("."));
    }

    // UniquenessCriterionBuilder will validate UniquenessCriterion pre conditions.
    // Always build our own instance to ensure it will not be reused.
    UniquenessCriterion uniquenessCriteria;
    if (uniquenessCriterionConfiguration == null) {
      // Build uniquenessEvaluator on coreId
      uniquenessCriteria =
        UniquenessCriterionBuilder.builder().workingFolder(configuration.getWorkingFolder()).innerBuild();
    } else {
      if (uniquenessCriterionConfiguration.getWorkingFolder() == null) {
        uniquenessCriterionConfiguration.setWorkingFolder(configuration.getWorkingFolder());
      }
      uniquenessCriteria = UniquenessCriterionBuilder.builder(uniquenessCriterionConfiguration).innerBuild();
    }

    return new ReferenceUniqueCriterion(configuration, uniquenessCriteria);

  }

  public ReferenceUniqueCriterionBuilder termRefersToUnique(Term term, EvaluationContext evaluationContextRestriction,
    String rowTypeRestriction, Term referedTerm, EvaluationContext referedEvaluationContextRestriction,
    String referedRowTypeRestriction) {
    this.configuration.setTerm(term);
    this.configuration.setEvaluationContextRestriction(evaluationContextRestriction);
    this.configuration.setRowTypeRestriction(rowTypeRestriction);

    this.uniquenessCriterionConfiguration.setTerm(referedTerm);
    this.uniquenessCriterionConfiguration.setEvaluationContextRestriction(referedEvaluationContextRestriction);
    this.uniquenessCriterionConfiguration.setRowTypeRestriction(referedRowTypeRestriction);

    return this;
  }

  /**
   * Should the evaluator accept multiple values using a defined separator.
   * e.g. 1234|2345
   * 
   * @param separator
   * @return
   */
  public ReferenceUniqueCriterionBuilder supportMultipleValues(String multipleValuesSeparator) {
    this.configuration.setMultipleValuesSeparator(multipleValuesSeparator);
    return this;
  }

  /**
   * Set working folder to save temporary files.
   * 
   * @param workingFolder
   * @return
   */
  public ReferenceUniqueCriterionBuilder workingFolder(File workingFolder) {
    this.configuration.setWorkingFolder(workingFolder);
    this.uniquenessCriterionConfiguration.setWorkingFolder(workingFolder);
    return this;
  }
}
