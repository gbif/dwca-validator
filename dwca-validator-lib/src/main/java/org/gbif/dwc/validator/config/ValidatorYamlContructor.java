package org.gbif.dwc.validator.config;

import org.gbif.dwc.validator.rule.EvaluationRuleBuilder;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleBuilderKey;

import java.util.Map;

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
   * @param ruleBuilderClassMap
   */
  public ValidatorYamlContructor(Map<Class<EvaluationRuleBuilder>, EvaluationRuleBuilderKey> ruleBuilderClassMap) {
    super();
    String tagName;
    for (Class<?> currClass : ruleBuilderClassMap.keySet()) {
      tagName = "!" + ruleBuilderClassMap.get(currClass).value();

      // register the alias to the class
      addTypeDescription(new TypeDescription(currClass, tagName));

      // register the class that will allow to use the builder
      this.yamlConstructors.put(new Tag(tagName), new ConstructFromRuleBuilder());
    }
  }

  /**
   * Private class to return an object from the build method of an EvaluationRuleBuilder.
   * 
   * @author cgendreau
   */
  private class ConstructFromRuleBuilder extends ConstructYamlObject {

    @Override
    public Object construct(Node node) {
      Object obj = super.construct(node);
      return ((EvaluationRuleBuilder) obj).build();
    }
  }

}
