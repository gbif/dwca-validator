package org.gbif.dwc.validator.evaluator.integrity;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.StatefulRecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;
import org.gbif.dwc.validator.evaluator.configuration.ReferenceUniqueEvaluatorConfiguration;
import org.gbif.dwc.validator.evaluator.configuration.UniquenessEvaluatorConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder of ReferenceUniqueEvaluator object.
 * Returned ReferenceUniqueEvaluator is NOT immutable due to file access.
 * 
 * @author cgendreau
 */
@RecordEvaluatorBuilderKey("referenceUniqueEvaluator")
public class ReferenceUniqueEvaluatorBuilder implements RecordEvaluatorBuilder {

  private final ReferenceUniqueEvaluatorConfiguration configuration;
  private final UniquenessEvaluatorConfiguration uniquenessEvaluatorConfiguration;

  private ReferenceUniqueEvaluatorBuilder() {
    this.configuration = new ReferenceUniqueEvaluatorConfiguration();
    this.uniquenessEvaluatorConfiguration = new UniquenessEvaluatorConfiguration();
  }

  public ReferenceUniqueEvaluatorBuilder(ReferenceUniqueEvaluatorConfiguration configuration,
    UniquenessEvaluatorConfiguration uniquenessEvaluatorConfiguration) {
    this.configuration = configuration;
    this.uniquenessEvaluatorConfiguration = uniquenessEvaluatorConfiguration;
  }

  /**
   * Create with default value. Using coreId.
   * 
   * @return
   */
  public static ReferenceUniqueEvaluatorBuilder builder() {
    return new ReferenceUniqueEvaluatorBuilder();
  }

  /**
   * Build UniquenessEvaluator object.
   * 
   * @return
   * @throws IllegalStateException
   */
  @Override
  public StatefulRecordEvaluator build() throws IllegalStateException {

    // if no 'term' is set the id will be used to test as 'star' records
    if (configuration.getTerm() != null) {
      Preconditions.checkState(StringUtils.isNotBlank(configuration.getRowTypeRestriction()),
        "rowTypeRestriction must be provided if a specific term is specified.");
      Preconditions.checkState(uniquenessEvaluatorConfiguration != null,
        "uniquenessEvaluatorConfiguration must be provided if a specific term is specified.");
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

    // UniquenessEvaluatorBuilder will validate UniquenessEvaluator pre conditions.
    // Always build our own instance to ensure it will not be reused.
    UniquenessEvaluator uniquenessEvaluator;
    if (uniquenessEvaluatorConfiguration == null) {
      // Build uniquenessEvaluator on coreId
      uniquenessEvaluator =
        UniquenessEvaluatorBuilder.builder().workingFolder(configuration.getWorkingFolder()).innerBuild();
    } else {
      if (uniquenessEvaluatorConfiguration.getWorkingFolder() == null) {
        uniquenessEvaluatorConfiguration.setWorkingFolder(configuration.getWorkingFolder());
      }
      uniquenessEvaluator = UniquenessEvaluatorBuilder.builder(uniquenessEvaluatorConfiguration).innerBuild();
    }

    return new ReferenceUniqueEvaluator(configuration, uniquenessEvaluator);

  }

  public ReferenceUniqueEvaluatorBuilder termRefersToUnique(Term term,
    EvaluationContext evaluationContextRestriction, String rowTypeRestriction, Term referedTerm,
    EvaluationContext referedEvaluationContextRestriction, String referedRowTypeRestriction) {
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
  public ReferenceUniqueEvaluatorBuilder supportMultipleValues(String multipleValuesSeparator) {
    this.configuration.setMultipleValuesSeparator(multipleValuesSeparator);
    return this;
  }

  /**
   * Set working folder to save temporary files.
   * 
   * @param workingFolder
   * @return
   */
  public ReferenceUniqueEvaluatorBuilder workingFolder(File workingFolder) {
    this.configuration.setWorkingFolder(workingFolder);
    this.uniquenessEvaluatorConfiguration.setWorkingFolder(workingFolder);
    return this;
  }
}
