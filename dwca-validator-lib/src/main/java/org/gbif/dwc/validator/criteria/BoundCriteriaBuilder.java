package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.configuration.BoundCriteriaConfiguration;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import com.google.common.base.Preconditions;

/**
 * Builder for BoundCriteria objects.
 * 
 * @author cgendreau
 */
public class BoundCriteriaBuilder implements RecordCriteriaBuilder {

  private final BoundCriteriaConfiguration configuration;

  public BoundCriteriaBuilder() {
    configuration = new BoundCriteriaConfiguration();
  }

  public BoundCriteriaBuilder(BoundCriteriaConfiguration configuration) {
    this.configuration = configuration;
  }

  public static BoundCriteriaBuilder builder() {
    return new BoundCriteriaBuilder();
  }

  /**
   * Build BoundCriteria object.
   * 
   * @return immutable BoundCriteria object
   * @throws NullPointerException if the lowerBound or upperBound is null
   *         IllegalStateException if lower bound is greater than upperBound.
   */
  @Override
  public RecordCriteria build() throws NullPointerException, IllegalStateException {

    Preconditions.checkNotNull(configuration.getLowerBound());
    Preconditions.checkNotNull(configuration.getUpperBound());
    Preconditions.checkState(
      (configuration.getLowerBound().doubleValue() < configuration.getUpperBound().doubleValue()),
      "lower and upper bounds are in wrong order");


    // we need a RowTypeRestriction otherwise all extension records could be flagged as incomplete
    // even if the don't use the term by definition.
    // Preconditions.checkState(StringUtils.isNotBlank(configuration.getRowTypeRestriction()),
    // "A RowTypeRestriction must be provided");

    return new BoundCriteria(configuration);
  }

  /**
   * Set a lower and upper inclusive bounds.
   * 
   * @param term term on which bounds should be checked
   * @param lowerBound lower inclusive bound
   * @param upperBound upper inclusive bound
   */
  public BoundCriteriaBuilder termBoundedBy(Term term, Number lowerBound, Number upperBound) {
    configuration.setValueTransformation(ValueTransformations.toNumeric(term));
    configuration.setLowerBound(lowerBound);
    configuration.setUpperBound(upperBound);
    return this;
  }

  /**
   * Set the restriction on the rowType to avoid the evaluation to run on all rowType.
   * 
   * @param rowTypeRestriction
   * @return
   */
  public BoundCriteriaBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
  }

}
