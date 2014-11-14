package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.term.RecordCompletionEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.term.ValueEvaluatorBuilder;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRuleBuilder;

import java.util.Arrays;

/**
 * Collection of builders related to validation of the value attached to a terms (ConceptTerm).
 * 
 * @author cgendreau
 */
public class TermsValidators {

  public static RecordCompletionEvaluatorBuilder required(ConceptTerm... terms) {
    RecordCompletionEvaluatorBuilder bldr = RecordCompletionEvaluatorBuilder.builder();
    for (ConceptTerm currTerm : terms) {
      bldr.checkTerm(currTerm);
    }
    return bldr;
  }

  public static ValueEvaluatorBuilder rule(EvaluationRuleIF<String> rule, ConceptTerm... terms) {
    return ValueEvaluatorBuilder.builder().addRule(Arrays.asList(terms), rule);
  }

  /**
   * Get a ValueEvaluatorBuilder configured to build an Evaluator that ensures the value
   * of the provided term is numerical and inside to provided bounds.
   * 
   * @param term
   * @param lowerBound
   * @param upperBound
   * @return
   */
  public static ValueEvaluatorBuilder withinRange(ConceptTerm term, Number lowerBound, Number upperBound) {
    return ValueEvaluatorBuilder.builder().addRule(term,
      NumericalValueEvaluationRuleBuilder.builder().boundedBy(lowerBound, upperBound).build());
  }

}
