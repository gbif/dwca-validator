package org.gbif.dwc.validator.annotation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Preconditions;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Utility class to load classes associated with a specific annotation.
 * 
 * @author cgendreau
 */
public class AnnotationLoader {

  /**
   * Get all classes matching annotation from a basePackage.
   * 
   * @param basePackage
   * @return
   */
  public static <T extends Annotation> Set<Class<?>> getAnnotatedClasses(String basePackage, Class<T> annotationClass) {

    Preconditions.checkNotNull(annotationClass);

    Reflections reflections =
      new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(basePackage)));

    return reflections.getTypesAnnotatedWith(annotationClass);
  }

  /**
   * Get all classes matching annotation from a basePackage where the class is also implementing the specified
   * interface.
   * 
   * @param basePackage
   * @param implementedInterface
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T extends Annotation, I> Set<Class<I>> getAnnotatedClasses(String basePackage,
    Class<T> annotationClass, Class<I> implementedInterface) {

    Preconditions.checkNotNull(annotationClass);
    Preconditions.checkNotNull(implementedInterface);

    Preconditions.checkArgument(implementedInterface.isInterface(), "implementedInterface shall be an interface");

    Reflections reflections =
      new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(basePackage)));

    Set<Class<?>> recordEvaluatorClasses = reflections.getTypesAnnotatedWith(annotationClass);
    Iterator<Class<?>> annotatedIt = recordEvaluatorClasses.iterator();

    Set<Class<I>> annotatedClassSet = new HashSet<Class<I>>();
    Class<?> currClass;
    while (annotatedIt.hasNext()) {
      currClass = annotatedIt.next();
      if (implementedInterface.isAssignableFrom(currClass)) {
        annotatedClassSet.add((Class<I>) currClass);
      }
    }
    return annotatedClassSet;
  }
}
