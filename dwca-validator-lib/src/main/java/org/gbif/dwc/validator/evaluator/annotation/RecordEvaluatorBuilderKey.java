package org.gbif.dwc.validator.evaluator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to identify RecordEvaluator builder class.
 * 
 * @author cgendreau
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RecordEvaluatorBuilderKey {

  /**
   * @return the key to use to uniquely identify a record evaluator builder
   */
  String value();
}
