package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.transformation.ValueTransformation;

@CriterionConfigurationKey("minMaxCriteria")
public class MinMaxCriteriaConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;

  private ValueTransformation<Number> minValueTransformation;
  private ValueTransformation<Number> maxValueTransformation;

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Result getLevel() {
    return level;
  }

  public void setLevel(Result level) {
    this.level = level;
  }

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


}
