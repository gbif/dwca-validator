package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.validator.criteria.configuration.ValueCriterionConfiguration;
import org.gbif.dwc.validator.transformation.ValueTransformation;

import com.google.common.base.Predicate;

/**
 * Builder for ValueCriterion objects.
 *
 * @author cgendreau
 */
public class ValueCriterionBuilder<T> implements RecordCriterionBuilder {

  private final ValueCriterionConfiguration<T> configuration;

  public ValueCriterionBuilder() {
    configuration = new ValueCriterionConfiguration<T>();
  }

  /**
   * Get a ValueCriterionBuilder for String
   *
   * @return
   */
  public static ValueCriterionBuilder<String> builderString() {
    return new ValueCriterionBuilder<String>();
  }

  /**
   * Get a ValueCriterionBuilder for Number
   *
   * @return
   */
  public static ValueCriterionBuilder<Number> builderNumber() {
    return new ValueCriterionBuilder<Number>();
  }

  public ValueCriterionBuilder<T> checkValue(Predicate<T> predicate, ValueTransformation<T> transformation) {
    configuration.setPredicate(predicate);
    configuration.setTransformation(transformation);
    return this;
  }

  public ValueCriterionBuilder<T> checkValue(Predicate<String> predicate) {

    return this;
  }

  @Override
  public RecordCriterion build() throws IllegalStateException {
    return new ValueCriterion<T>(configuration);
  }

}
