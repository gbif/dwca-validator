package org.gbif.dwc.validator.rule;

/**
 * Interface defining a EvaluationRuleIF<String> builder.
 * 
 * @author cgendreau
 */
public interface EvaluationRuleBuilder {

  /**
   * Build a concrete instance of EvaluationRuleIF<String>.
   * 
   * @return
   */
  EvaluationRule<String> build();

}
