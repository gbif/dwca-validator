package org.gbif.dwc.validator.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Utility class to load class and data associated with a specific annotation.
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

    Preconditions.checkNotNull(annotationClass);

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

  /**
   * Get all classes and matching annotation from a basePackage where the class is also implementing the specified
   * interface.
   * 
   * @param basePackage
   * @param implementedInterface
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T extends Annotation, I> Map<Class<I>, T> getClassAnnotation(String basePackage,
    Class<T> annotationClass, Class<I> implementedInterface) {

    Preconditions.checkNotNull(annotationClass);
    Preconditions.checkNotNull(implementedInterface);

    Preconditions.checkArgument(implementedInterface.isInterface(), "implementedInterface shall be an interface");

    Reflections reflections =
      new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(basePackage)));

    Set<Class<?>> recordEvaluatorClasses = reflections.getTypesAnnotatedWith(annotationClass);
    Iterator<Class<?>> annotatedIt = recordEvaluatorClasses.iterator();

    Map<Class<I>, T> classAnnotationMap = new HashMap<Class<I>, T>();
    T currAnnotationValue;
    Class<?> currClass;
    while (annotatedIt.hasNext()) {
      currClass = annotatedIt.next();
      currAnnotationValue = currClass.getAnnotation(annotationClass);

      if (implementedInterface.isAssignableFrom(implementedInterface)) {
        classAnnotationMap.put((Class<I>) currClass, currAnnotationValue);
      }
    }
    return classAnnotationMap;
  }
}
