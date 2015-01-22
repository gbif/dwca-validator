package org.gbif.dwc.validator.result.evaluator;

import org.gbif.dwc.validator.result.type.ValidationTypeIF;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


public class EvaluatorResultElement {

  private final String evaluatorKey;
  private final ValidationTypeIF type;
  private final String explanation;
  private final Object resultedObject;

  public EvaluatorResultElement(String evaluatorKey, ValidationTypeIF type, String explanation) {
    this(evaluatorKey, type, explanation, null);
  }

  /**
   * @param evaluatorKey key of the validator who generated this entry
   * @param type
   * @param explanation
   * @param resultedObject object obtained while doing the evaluation (optional)
   */
  public EvaluatorResultElement(String evaluatorKey, ValidationTypeIF type, String explanation, Object resultedObject) {
    this.evaluatorKey = evaluatorKey;
    this.type = type;
    this.explanation = explanation;
    this.resultedObject = resultedObject;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    EvaluatorResultElement vre = (EvaluatorResultElement) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(evaluatorKey, vre.evaluatorKey)
      .append(type, vre.type).append(explanation, vre.explanation).append(resultedObject, vre.resultedObject)
      .isEquals();
  }

  public String getEvaluatorKey() {
    return evaluatorKey;
  }

  public String getExplanation() {
    return explanation;
  }

  public Object getResultedObject() {
    return resultedObject;
  }

  public ValidationTypeIF getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(7779, 8903).append(evaluatorKey).append(type).append(explanation).append(resultedObject)
      .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("evaluatorKey", evaluatorKey).append("type", type)
      .append("explanation", explanation).toString();
  }

}
