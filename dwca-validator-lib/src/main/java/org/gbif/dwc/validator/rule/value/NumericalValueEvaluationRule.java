package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple numerical value evaluation rule.
 * Check if the provided String is a number with optional decimals and optional minus sign.
 * All other notations are not supported (at least for now).
 * TODO: add support for lower and upper bounds evaluation
 * 
 * @author cgendreau
 */
public class NumericalValueEvaluationRule implements EvaluationRuleIF<String> {

  /**
   * Builder used to customized, if needed, the NumericalValueEvaluationRule.
   * Also ensure usage of immutable object.
   * 
   * @author cgendreau
   */
  public static class NumericalValueEvaluationRuleBuilder {


    private NumericalValueEvaluationRuleBuilder() {
    }

    /**
     * Creates a default NumericalValueEvaluationRuleBuilder.
     * 
     * @return
     */
    public static NumericalValueEvaluationRuleBuilder create() {
      return new NumericalValueEvaluationRuleBuilder();
    }

    /**
     * Build an immutable NumericalValueEvaluationRule instance
     * 
     * @return immutable NumericalValueEvaluationRule
     */
    public NumericalValueEvaluationRule build() {
      return new NumericalValueEvaluationRule();
    }
  }

  private static final Pattern NUMBER_CANDIDATE = Pattern.compile("^[-]?(\\d*[.])?\\d+$");

  /**
   * NumericalValueEvaluationRule are created using the builder NumericalValueEvaluationRuleBuilder.
   */
  private NumericalValueEvaluationRule() {
  }

  /**
   * Simple alias of NumericalValueEvaluationRule.create() for code readability so we can use
   * NumericalValueEvaluationRule.createRule() instead of
   * NumericalValueEvaluationRule.NumericalValueEvaluationRuleBuilder.create()
   * 
   * @return default NumericalValueEvaluationRule
   */
  public static NumericalValueEvaluationRuleBuilder createRule() {
    return NumericalValueEvaluationRuleBuilder.create();
  }

  @Override
  public ValidationResultElement evaluate(String str) {
    if (StringUtils.isNotBlank(str) && !NUMBER_CANDIDATE.matcher(str).matches()) {
      return new ValidationResultElement(ContentValidationType.RECORD_CONTENT, Result.WARNING,
        ArchiveValidatorConfig.getLocalizedString("rule.non_numerical", str));
    }
    return null;
  }

}
