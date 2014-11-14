package org.gbif.dwc.validator.rule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to identify EvaluationRule builder class.
 * 
 * @author cgendreau
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EvaluationRuleBuilderKey {

  /**
   * @return the key to use to uniquely identify a rule evaluation builder
   */
  String value();
}