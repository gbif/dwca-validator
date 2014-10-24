package org.gbif.dwc.validator.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Load data associated with a specific annotation.
 * 
 * @author cgendreau
 */
public class AnnotationLoader {

  /**
   * Get all classes and matching annotation from a basePackage.
   * 
   * @param basePackage
   * @return
   */
  public static <T extends Annotation> Map<Class<?>, T>
    getClassAnnotation(String basePackage, Class<T> annotationClass) {

    Reflections reflections =
      new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(basePackage)));

    Set<Class<?>> recordEvaluatorClasses = reflections.getTypesAnnotatedWith(annotationClass);
    Iterator<Class<?>> annotatedIt = recordEvaluatorClasses.iterator();

    Map<Class<?>, T> classAnnotationMap = new HashMap<Class<?>, T>();
    T currAnnotationValue;
    Class<?> currClass;
    while (annotatedIt.hasNext()) {
      currClass = annotatedIt.next();
      currAnnotationValue = currClass.getAnnotation(annotationClass);
      classAnnotationMap.put(currClass, currAnnotationValue);
    }
    return classAnnotationMap;
  }
}
