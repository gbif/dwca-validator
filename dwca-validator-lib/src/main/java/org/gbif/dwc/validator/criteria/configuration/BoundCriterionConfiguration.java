package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.transformation.ValueTransformation;

@CriterionConfigurationKey("boundCriteria")
public class BoundCriterionConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;

  private Number lowerBound;
  private Number upperBound;
  private Term term;
  private ValueTransformation<Number> valueTransformation;

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

  public Number getLowerBound() {
    return lowerBound;
  }

  public void setLowerBound(Number lowerBound) {
    this.lowerBound = lowerBound;
  }

  public Number getUpperBound() {
    return upperBound;
  }

  public void setUpperBound(Number upperBound) {
    this.upperBound = upperBound;
  }

  public ValueTransformation<Number> getValueTransformation() {
    return valueTransformation;
  }

  public void setValueTransformation(ValueTransformation<Number> valueTransformation) {
    this.valueTransformation = valueTransformation;
  }

  public Term getTerm() {
    return term;
  }

  public void setTerm(Term term) {
    this.term = term;
  }

}
