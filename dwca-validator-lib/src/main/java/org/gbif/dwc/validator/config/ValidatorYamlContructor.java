package org.gbif.dwc.validator.config;

import org.gbif.dwc.validator.criteria.RecordCriteriaBuilder;
import org.gbif.dwc.validator.criteria.annotation.DatasetCriteriaBuilderKey;
import org.gbif.dwc.validator.criteria.annotation.RecordCriteriaBuilderKey;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriteriaBuilder;

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
  public ValidatorYamlContructor(Collection<Class<RecordCriteriaBuilder>> recordCriteriaBuilderClasses,
    Collection<Class<DatasetCriteriaBuilder>> datasetCriteriaBuilderClasses) {
    super();

    String tagName;
    for (Class<RecordCriteriaBuilder> currClass : recordCriteriaBuilderClasses) {
      if (currClass.getAnnotation(RecordCriteriaBuilderKey.class) != null) {
        tagName = "!" + currClass.getAnnotation(RecordCriteriaBuilderKey.class).value();

        // register the alias to the class
        addTypeDescription(new TypeDescription(currClass, tagName));

        // register the class that will allow to use the builder
        this.yamlConstructors.put(new Tag(tagName), new ConstructFromRecordCriteriaBuilder());
      }
    }

    for (Class<DatasetCriteriaBuilder> currClass : datasetCriteriaBuilderClasses) {
      if (currClass.getAnnotation(DatasetCriteriaBuilderKey.class) != null) {
        tagName = "!" + currClass.getAnnotation(DatasetCriteriaBuilderKey.class).value();

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
      return ((RecordCriteriaBuilder) obj).build();
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
      return ((DatasetCriteriaBuilder) obj).build();
    }
  }

}
