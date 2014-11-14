package org.gbif.dwc.validator.config;

import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;
import org.gbif.dwc.validator.rule.EvaluationRuleBuilder;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleBuilderKey;

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
   * @param ruleBuilderClassList EvaluationRuleBuilder implementations must have the EvaluationRuleBuilderKey annotation
   *        set. The value of
   *        the annotation will be used for the alias name.
   * @param evaluatorBuilderClassList RecordEvaluatorBuilder implementations must have the RecordEvaluatorBuilderKey
   *        annotation set. The value of
   *        the annotation will be used for the alias name.
   */
  public ValidatorYamlContructor(Collection<Class<EvaluationRuleBuilder>> ruleBuilderClassList,
    Collection<Class<RecordEvaluatorBuilder>> evaluatorBuilderClassList) {
    super();

    String tagName;
    for (Class<EvaluationRuleBuilder> currClass : ruleBuilderClassList) {
      if (currClass.getAnnotation(EvaluationRuleBuilderKey.class) != null) {
        tagName = "!" + currClass.getAnnotation(EvaluationRuleBuilderKey.class).value();

        // register the alias to the class
        addTypeDescription(new TypeDescription(currClass, tagName));

        // register the class that will allow to use the builder
        this.yamlConstructors.put(new Tag(tagName), new ConstructFromEvaluationRuleBuilder());
      }
    }

    for (Class<RecordEvaluatorBuilder> currClass : evaluatorBuilderClassList) {
      if (currClass.getAnnotation(RecordEvaluatorBuilderKey.class) != null) {
        tagName = "!" + currClass.getAnnotation(RecordEvaluatorBuilderKey.class).value();

        // register the alias to the class
        addTypeDescription(new TypeDescription(currClass, tagName));

        // register the class that will allow to use the builder
        this.yamlConstructors.put(new Tag(tagName), new ConstructFromRecordEvaluatorBuilder());
      }
    }

  }

  /**
   * Private class to return an object from the build method of an EvaluationRuleBuilder.
   * 
   * @author cgendreau
   */
  private class ConstructFromEvaluationRuleBuilder extends ConstructYamlObject {

    @Override
    public Object construct(Node node) {
      Object obj = super.construct(node);
      return ((EvaluationRuleBuilder) obj).build();
    }
  }

  /**
   * Private class to return an object from the build method of an RecordEvaluatorBuilder.
   * 
   * @author cgendreau
   */
  private class ConstructFromRecordEvaluatorBuilder extends ConstructYamlObject {

    @Override
    public Object construct(Node node) {
      Object obj = super.construct(node);
      return ((RecordEvaluatorBuilder) obj).build();
    }
  }

}
