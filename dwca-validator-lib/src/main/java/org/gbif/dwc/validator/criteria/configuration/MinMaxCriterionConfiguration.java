package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.transformation.ValueTransformation;

/**
 * Container object holding MinMaxCriterion configurations.
 *
 * @author cgendreau
 */
@CriterionConfigurationKey("minMaxCriterion")
public class MinMaxCriterionConfiguration extends AbstractRecordCriterionConfiguration {

  private Term minValueTerm;
  private Term maxValueTerm;

  private ValueTransformation<Number> minValueTransformation;
  private ValueTransformation<Number> maxValueTransformation;

  /**
   * Enforce minValue AND maxValue to be provided.
   * Default to false
   */
  private boolean enforceTwoTermsUse = false;

  public ValueTransformation<Number> getMinValueTransformation() {
    return minValueTransformation;
  }

  public void setMinValueTransformation(ValueTransformation<Number> minValueTransformation) {
    this.minValueTransformation = minValueTransformation;
  }

  public ValueTransformation<Number> getMaxValueTransformation() {
    return maxValueTransformation;
  }

  public void setMaxValueTransformation(ValueTransformation<Number> maxValueTransformation) {
    this.maxValueTransformation = maxValueTransformation;
  }

  public Term getMinValueTerm() {
    return minValueTerm;
  }

  public void setMinValueTerm(Term minValueTerm) {
    this.minValueTerm = minValueTerm;
  }

  public Term getMaxValueTerm() {
    return maxValueTerm;
  }

  public void setMaxValueTerm(Term maxValueTerm) {
    this.maxValueTerm = maxValueTerm;
  }

  public boolean isEnforceTwoTermsUse() {
    return enforceTwoTermsUse;
  }

  public void setEnforceTwoTermsUse(boolean enforceTwoTermsUse) {
    this.enforceTwoTermsUse = enforceTwoTermsUse;
  }
}
