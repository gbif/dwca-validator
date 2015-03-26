package org.gbif.dwc.validator.transformation;



public class ValueTransformationResult<T> {

  public enum TransformationStatus {
    SKIPPED, TRANSFORMED, NOT_TRANSFORMED
  };

  private final TransformationStatus status;

  private final String originalValue;

  private final T dataObject;
  private final String explanation;

  public static <T> ValueTransformationResult<T> skipped(String originalValue) {
    return new ValueTransformationResult<T>(originalValue, TransformationStatus.SKIPPED, null, null);
  }

  public static <T> ValueTransformationResult<T> notTransformed(String originalValue, String explanation) {
    return new ValueTransformationResult<T>(originalValue, TransformationStatus.NOT_TRANSFORMED, null, explanation);
  }

  public static <T> ValueTransformationResult<T> transformed(String originalValue, T dataObject) {
    return new ValueTransformationResult<T>(originalValue, TransformationStatus.TRANSFORMED, dataObject, null);
  }

  public ValueTransformationResult(String originalValue, TransformationStatus status, T dataObject, String explanation) {
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

}
