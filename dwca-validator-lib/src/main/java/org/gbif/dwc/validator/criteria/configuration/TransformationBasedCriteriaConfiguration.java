package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.validator.criteria.annotation.CriteriaConfigurationKey;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.transformation.ValueTransformation;

import java.util.ArrayList;
import java.util.List;


/**
 * Container object holding TransformationBasedCriteria configurations.
 * 
 * @author cgendreau
 */
@CriteriaConfigurationKey("transformationBasedCriteria")
public class TransformationBasedCriteriaConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;

  private List<ValueTransformation<?>> transformations;

  public TransformationBasedCriteriaConfiguration() {
    transformations = new ArrayList<ValueTransformation<?>>();
  }

  public void addTransformation(ValueTransformation<?> transformation) {
    transformations.add(transformation);
  }

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

  public List<ValueTransformation<?>> getTransformations() {
    return transformations;
  }

  public void setTransformations(List<ValueTransformation<?>> transformations) {
    this.transformations = transformations;
  }

}
