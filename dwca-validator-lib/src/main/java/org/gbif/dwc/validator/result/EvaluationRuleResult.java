package org.gbif.dwc.validator.result;

/**
 * EvaluationRule result object.
 * 
 * @author cgendreau
 */
public class EvaluationRuleResult {

  public enum RuleResult {
    SKIPPED, PASSED, FAILED
  };

  public static EvaluationRuleResult SKIPPED = new EvaluationRuleResult(RuleResult.SKIPPED, null, null);
  public static EvaluationRuleResult PASSED = new EvaluationRuleResult(RuleResult.PASSED, null, null);

  private final RuleResult result;

  private final String explanation;
  private final Object resultedObject;

  public EvaluationRuleResult(RuleResult result, String explanation, Object resultedObject) {
    this.result = result;
    this.explanation = explanation;
    this.resultedObject = resultedObject;
  }

  public EvaluationRuleResult(RuleResult result, String explanation) {
    this(result, explanation, null);
  }

  public boolean passed() {
    return (result == RuleResult.PASSED);
  }

  public boolean skipped() {
    return (result == RuleResult.SKIPPED);
  }

  public boolean failed() {
    return (result == RuleResult.FAILED);
  }

  public String getExplanation() {
    return explanation;
  }

  public Object getResultedObject() {
    return resultedObject;
  }

}
