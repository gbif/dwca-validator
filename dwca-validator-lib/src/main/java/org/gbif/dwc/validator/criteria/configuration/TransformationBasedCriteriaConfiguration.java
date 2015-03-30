package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.transformation.ValueTransformation;

import java.util.ArrayList;
import java.util.List;


/**
 * Container object holding TransformationBasedCriteria configurations.
 *
 * @author cgendreau
 */
@CriterionConfigurationKey("transformationBasedCriteria")
public class TransformationBasedCriteriaConfiguration extends AbstractRecordCriterionConfiguration {

  private List<ValueTransformation<?>> transformations;

  public TransformationBasedCriteriaConfiguration() {
    transformations = new ArrayList<ValueTransformation<?>>();
  }

  public void addTransformation(ValueTransformation<?> transformation) {
    transformations.add(transformation);
  }

  public List<ValueTransformation<?>> getTransformations() {
    return transformations;
  }

  public void setTransformations(List<ValueTransformation<?>> transformations) {
    this.transformations = transformations;
  }

}
