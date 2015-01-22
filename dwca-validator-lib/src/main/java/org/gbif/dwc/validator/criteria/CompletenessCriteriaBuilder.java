package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriteriaBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.CompletenessCriteriaConfiguration;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder of CompletenessCriteria objects.
 * 
 * @author cgendreau
 */
@RecordCriteriaBuilderKey("completenessCriteria")
public class CompletenessCriteriaBuilder implements RecordCriteriaBuilder {

  private final CompletenessCriteriaConfiguration configuration;

  public CompletenessCriteriaBuilder() {
    configuration = new CompletenessCriteriaConfiguration();
  }

  public CompletenessCriteriaBuilder(CompletenessCriteriaConfiguration configuration) {
    this.configuration = configuration;
  }

  public static CompletenessCriteriaBuilder builder() {
    return new CompletenessCriteriaBuilder();
  }

  /**
   * Build RecordCompletionEvaluator object.
   * 
   * @return immutable RecordCompletionEvaluator object
   * @throws NullPointerException evaluatorContext, terms or blankValueEvaluationRule is null
   * @throws IllegalStateException if no terms were specified
   */
  @Override
  public RecordCriteria build() throws IllegalStateException {

    Preconditions.checkState(configuration.getValueTransformations() != null || configuration.getTerms() != null);

    // we need a RowTypeRestriction otherwise all extension records could be flagged as incomplete
    // even if the don't use the term by definition.
    Preconditions.checkState(StringUtils.isNotBlank(configuration.getRowTypeRestriction()),
      "A RowTypeRestriction must be provided");

    // if configuration is holding terms without transformation, create a default one
    if (configuration.getTerms().size() > 0) {
      for (Term currTerm : configuration.getTerms()) {
        configuration.addValueTransformation(ValueTransformations.toPresence(currTerm));
      }
    }

    Preconditions.checkState(configuration.getValueTransformations().size() > 0, "At least one term must be set");

    return new CompletenessCriteria(configuration);
  }

  /**
   * Add a term to check for completion.
   * 
   * @param term
   * @return
   */
  public CompletenessCriteriaBuilder checkTerm(Term term) {
    configuration.addTerm(term);
    return this;
  }

  /**
   * Set the restriction on the rowType to avoid the evaluation to run on all rowType.
   * 
   * @param rowTypeRestriction
   * @return
   */
  public CompletenessCriteriaBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
  }
}
