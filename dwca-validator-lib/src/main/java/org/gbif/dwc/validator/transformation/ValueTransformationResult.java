package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.terms.Term;


public class ValueTransformationResult<T> {

  public enum TransformationStatus {
    SKIPPED, TRANSFORMED, NOT_TRANSFORMED
  };

  private final TransformationStatus status;

  // not sure if we should carry that if we want to support composed values later
  private final String originalValue;
  private final Term term;

  private final T dataObject;
  private final String explanation;

  public static <T> ValueTransformationResult<T> skipped(Term term, String originalValue) {
    return new ValueTransformationResult<T>(term, originalValue, TransformationStatus.SKIPPED, null, null);
  }

  public static <T> ValueTransformationResult<T> notTransformed(Term term, String originalValue, String explanation) {
    return new ValueTransformationResult<T>(term, originalValue, TransformationStatus.NOT_TRANSFORMED, null,
      explanation);
  }

  public static <T> ValueTransformationResult<T> transformed(Term term, String originalValue, T dataObject) {
    return new ValueTransformationResult<T>(term, originalValue, TransformationStatus.TRANSFORMED, dataObject, null);
  }

  public ValueTransformationResult(Term term, String originalValue, TransformationStatus status, T dataObject,
    String explanation) {
    this.term = term;
    this.originalValue = originalValue;
    this.status = status;
    this.dataObject = dataObject;
    this.explanation = explanation;
  }

  public String getExplanation() {
    return explanation;
  }

  public T getData() {
    return dataObject;
  }

  public boolean isTransformed() {
    return (status == TransformationStatus.TRANSFORMED);
  }

  public boolean isSkipped() {
    return (status == TransformationStatus.SKIPPED);
  }

  public boolean isNotTransformed() {
    return (status == TransformationStatus.NOT_TRANSFORMED);
  }

  public String getOriginalValue() {
    return originalValue;
  }

  public Term getTerm() {
    return term;
  }
}
