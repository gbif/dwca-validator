package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.configuration.MinMaxCriteriaConfiguration;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import com.google.common.base.Preconditions;

/**
 * Builder for BoundCriteria objects.
 * 
 * @author cgendreau
 */
public class MinMaxCriteriaBuilder implements RecordCriteriaBuilder {

  private final MinMaxCriteriaConfiguration configuration;

  public MinMaxCriteriaBuilder() {
    configuration = new MinMaxCriteriaConfiguration();
  }

  public MinMaxCriteriaBuilder(MinMaxCriteriaConfiguration configuration) {
    this.configuration = configuration;
  }

  public static MinMaxCriteriaBuilder builder() {
    return new MinMaxCriteriaBuilder();
  }

  /**
   * Build MinMaxCriteria object.
   * 
   * @return immutable MinMaxCriteria object
   * @throws NullPointerException if the minValueTransformation or maxValueTransformation is null
   */
  @Override
  public RecordCriteria build() throws NullPointerException, IllegalStateException {

    Preconditions.checkNotNull(configuration.getMinValueTransformation());
    Preconditions.checkNotNull(configuration.getMaxValueTransformation());

    return new MinMaxCriteria(configuration);
  }

  /**
   * Sets terms who should contain minimum and maximum values.
   * 
   * @param minTerm lower inclusive bound
   * @param maxTerm upper inclusive bound
   */
  public MinMaxCriteriaBuilder terms(Term minTerm, Term maxTerm) {
    configuration.setMinValueTransformation(ValueTransformations.toNumeric(minTerm));
    configuration.setMaxValueTransformation(ValueTransformations.toNumeric(maxTerm));
    return this;
  }

  /**
   * Set the restriction on the rowType to avoid the evaluation to run on all rowType.
   * 
   * @param rowTypeRestriction
   * @return
   */
  public MinMaxCriteriaBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
  }

}
