package org.gbif.dwc.validator.config;

import org.gbif.dwc.validator.criteria.annotation.DatasetCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.RecordCriterionBuilder;

import java.util.Collection;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Dwc-A Validator SnakeYAML Constructor override.
 * SnakeYAML deals with Java Beans but not with beans obtained from a builder.
 * 
 * @author cgendreau
 */
public class ValidatorYamlContructor extends Constructor {

  /**
   * @param recordCriteriaBuilderClasses RecordCriteriaBuilder implementations must have the RecordCriteriaBuilderKey
   *        annotation set. The value of the annotation will be used for the alias name.
   * @param datasetCriteriaBuilderClasses DatasetCriteriaBuilder implementations must have the DatasetCriteriaBuilderKey
   *        annotation set. The value of the annotation will be used for the alias name.
   */
  public ValidatorYamlContructor(Collection<Class<RecordCriterionBuilder>> recordCriteriaBuilderClasses,
    Collection<Class<DatasetCriterionBuilder>> datasetCriteriaBuilderClasses) {
    super();

    String tagName;
    for (Class<RecordCriterionBuilder> currClass : recordCriteriaBuilderClasses) {
      if (currClass.getAnnotation(RecordCriterionBuilderKey.class) != null) {
        tagName = "!" + currClass.getAnnotation(RecordCriterionBuilderKey.class).value();

        // register the alias to the class
        addTypeDescription(new TypeDescription(currClass, tagName));

        // register the class that will allow to use the builder
        this.yamlConstructors.put(new Tag(tagName), new ConstructFromRecordCriteriaBuilder());
      }
    }

    for (Class<DatasetCriterionBuilder> currClass : datasetCriteriaBuilderClasses) {
      if (currClass.getAnnotation(DatasetCriterionBuilderKey.class) != null) {
        tagName = "!" + currClass.getAnnotation(DatasetCriterionBuilderKey.class).value();

        // register the alias to the class
        addTypeDescription(new TypeDescription(currClass, tagName));

        // register the class that will allow to use the builder
        this.yamlConstructors.put(new Tag(tagName), new ConstructFromDatasetCriteriaBuilder());
      }
    }

  }

  /**
   * Private class to return an object from the build method of an EvaluationRuleBuilder.
   * 
   * @author cgendreau
   */
  private class ConstructFromRecordCriteriaBuilder extends ConstructYamlObject {

    @Override
    public Object construct(Node node) {
      Object obj = super.construct(node);
      return ((RecordCriterionBuilder) obj).build();
    }
  }

  /**
   * Private class to return an object from the build method of an RecordEvaluatorBuilder.
   * 
   * @author cgendreau
   */
  private class ConstructFromDatasetCriteriaBuilder extends ConstructYamlObject {

    @Override
    public Object construct(Node node) {
      Object obj = super.construct(node);
      return ((DatasetCriterionBuilder) obj).build();
    }
  }

}
