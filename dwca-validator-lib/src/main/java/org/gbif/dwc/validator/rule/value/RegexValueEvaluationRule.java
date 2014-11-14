package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.EvaluationRule;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple regex based value evaluation rule.
 * Check if the provided String can be matched against a specific Regular Expression.
 * Evaluation is using Matcher(str).matches() meaning that it will 'Attempt to match the entire region against the
 * pattern.'
 * 
 * @author cgendreau
 */
public class RegexValueEvaluationRule implements EvaluationRule<String> {

  /**
   * Container object holding RegexValueEvaluationRule configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private String regex;

    public String getRegex() {
      return regex;
    }

    public void setRegex(String regex) {
      this.regex = regex;
    }
  }

  /**
   * Builder used to customized, if needed, the RegexValueEvaluationRule.
   * 
   * @author cgendreau
   */
  public static class RegexValueEvaluationRuleBuilder {

    private final Configuration configuration;

    private RegexValueEvaluationRuleBuilder() {
      configuration = new Configuration();
    }

    public static RegexValueEvaluationRuleBuilder create() {
      return new RegexValueEvaluationRuleBuilder();
    }

    /**
     * Build an immutable RegexValueEvaluationRule instance
     * 
     * @return immutable RegexValueEvaluationRule
     */
    public RegexValueEvaluationRule build() {
      return new RegexValueEvaluationRule(configuration);
    }

    /**
     * Set the regex to match against values.
     * 
     * @param regex
     * @return
     */
    public RegexValueEvaluationRuleBuilder usingRegex(String regex) {
      configuration.setRegex(regex);
      return this;
    }
  }

  private final Pattern pattern;

  public RegexValueEvaluationRule(Configuration configuration) {
    pattern = Pattern.compile(configuration.getRegex());
  }

  @Override
  public ValidationResultElement evaluate(String str) {
    if (StringUtils.isBlank(str)) {
      return ValidationResultElement.SKIPPED;
    }

    if (!pattern.matcher(str).matches()) {
      return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.WARNING,
        ValidatorConfig.getLocalizedString("rule.regex.no_match", str));
    }
    return ValidationResultElement.PASSED;
  }

}
