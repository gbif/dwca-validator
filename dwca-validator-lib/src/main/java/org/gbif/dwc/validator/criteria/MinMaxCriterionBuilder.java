package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.MinMaxCriterionConfiguration;
import org.gbif.dwc.validator.transformation.ValueTransformations;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Builder for MinMaxCriterion objects.
 * 
 * @author cgendreau
 */
@RecordCriterionBuilderKey("minMaxCriterion")
public class MinMaxCriterionBuilder implements RecordCriterionBuilder {

  private final MinMaxCriterionConfiguration configuration;

  public MinMaxCriterionBuilder() {
    configuration = new MinMaxCriterionConfiguration();
  }

  public MinMaxCriterionBuilder(MinMaxCriterionConfiguration configuration) {
    this.configuration = configuration;
  }

  public static MinMaxCriterionBuilder builder() {
    return new MinMaxCriterionBuilder();
  }

  /**
   * Sets terms who should contain minimum and maximum values.
   * 
   * @param minTerm lower inclusive bound
   * @param maxTerm upper inclusive bound
   */
  public MinMaxCriterionBuilder terms(Term minTerm, Term maxTerm) {
    configuration.setMinValueTerm(minTerm);
    configuration.setMaxValueTerm(maxTerm);
    return this;
  }

  /**
   * Set the restriction on the rowType to avoid the evaluation to run on all rowType.
   * 
   * @param rowTypeRestriction
   * @return
   */
  public MinMaxCriterionBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
  }

  /**
   * Build MinMaxCriterion object.
   * 
   * @return immutable MinMaxCriterion object
   * @throws NullPointerException if the minValueTransformation or maxValueTransformation is null
   */
  @Override
  public RecordCriterionIF build() throws NullPointerException, IllegalStateException {

    boolean transformationsProvided =
      configuration.getMinValueTransformation() != null && configuration.getMaxValueTransformation() != null;
    boolean termsProvided = configuration.getMinValueTerm() != null && configuration.getMaxValueTerm() != null;

    Preconditions.checkState(BooleanUtils.xor(new boolean[] {transformationsProvided, termsProvided}),
      "min and max terms OR min and max transformations must be provided");

    if (termsProvided) {
      configuration.setMinValueTransformation(ValueTransformations.toNumeric(configuration.getMinValueTerm()));
      configuration.setMaxValueTransformation(ValueTransformations.toNumeric(configuration.getMaxValueTerm()));
    }

    return new MinMaxCriterion(configuration);
  }

}
