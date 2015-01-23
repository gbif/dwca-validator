package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.BoundCriterionConfiguration;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Builder for BoundCriteria objects.
 * 
 * @author cgendreau
 */
@RecordCriterionBuilderKey("boundCriterion")
public class BoundCriterionBuilder implements RecordCriteriaBuilder {

  private final BoundCriterionConfiguration configuration;

  public BoundCriterionBuilder() {
    configuration = new BoundCriterionConfiguration();
  }

  public BoundCriterionBuilder(BoundCriterionConfiguration configuration) {
    this.configuration = configuration;
  }

  public static BoundCriterionBuilder builder() {
    return new BoundCriterionBuilder();
  }

  /**
   * Set a lower and upper inclusive bounds.
   * 
   * @param term term on which bounds should be checked
   * @param lowerBound lower inclusive bound
   * @param upperBound upper inclusive bound
   */
  public BoundCriterionBuilder termBoundedBy(Term term, Number lowerBound, Number upperBound) {
    configuration.setTerm(term);
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
  public BoundCriterionBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
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

    Preconditions.checkState((BooleanUtils.xor(new boolean[] {configuration.getTerm() == null,
      configuration.getValueTransformation() == null})), "A term OR a transformation must be specified");

    // if no transformation is defined, set the default one
    if (configuration.getValueTransformation() == null) {
      configuration.setValueTransformation(ValueTransformations.toNumeric(configuration.getTerm()));
    }

    // we need a RowTypeRestriction otherwise all extension records could be flagged as incomplete
    // even if the don't use the term by definition.
    // Preconditions.checkState(StringUtils.isNotBlank(configuration.getRowTypeRestriction()),
    // "A RowTypeRestriction must be provided");

    return new BoundCriterion(configuration);
  }

}
