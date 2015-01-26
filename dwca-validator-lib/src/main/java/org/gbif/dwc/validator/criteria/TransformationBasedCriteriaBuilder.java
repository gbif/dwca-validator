package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.configuration.TransformationBasedCriteriaConfiguration;
import org.gbif.dwc.validator.transformation.ValueTransformation;

import com.google.common.base.Preconditions;

/**
 * Builder for BoundCriteria objects.
 * 
 * @author cgendreau
 */
public class TransformationBasedCriteriaBuilder implements RecordCriterionBuilder {

  private final TransformationBasedCriteriaConfiguration configuration;

  public TransformationBasedCriteriaBuilder() {
    configuration = new TransformationBasedCriteriaConfiguration();
  }

  public TransformationBasedCriteriaBuilder(TransformationBasedCriteriaConfiguration configuration) {
    this.configuration = configuration;
  }

  public static TransformationBasedCriteriaBuilder builder() {
    return new TransformationBasedCriteriaBuilder();
  }

  /**
   * Build TransformationBasedCriteria object.
   * 
   * @return immutable TransformationBasedCriteria object
   * @throws NullPointerException if the getTransformations is null
   */
  @Override
  public RecordCriterionIF build() throws NullPointerException, IllegalStateException {

    Preconditions.checkNotNull(configuration.getTransformations());
    Preconditions.checkState(!configuration.getTransformations().isEmpty());

    return new TransformationBasedCriteria(configuration);
  }

  /**
   * Append a transformation to the transformation list.
   * 
   * @param transformation
   */
  public TransformationBasedCriteriaBuilder appendTransformation(ValueTransformation<?> transformation) {
    configuration.addTransformation(transformation);
    return this;
  }

  /**
   * Set the restriction on the rowType to avoid the evaluation to run on all rowType.
   * 
   * @param rowTypeRestriction
   * @return
   */
  public TransformationBasedCriteriaBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
  }

}
