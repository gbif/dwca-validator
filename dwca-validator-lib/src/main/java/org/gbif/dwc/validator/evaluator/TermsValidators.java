package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.evaluator.term.RecordCompletionEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.term.ValueEvaluatorBuilder;
import org.gbif.dwc.validator.rule.EvaluationRule;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRuleBuilder;

import java.util.Arrays;

/**
 * Collection of builders related to validation of the value attached to a terms (Term).
 * 
 * @author cgendreau
 */
public class TermsValidators {

  public static RecordCompletionEvaluatorBuilder required(Term... terms) {
    RecordCompletionEvaluatorBuilder bldr = RecordCompletionEvaluatorBuilder.builder();
    for (Term currTerm : terms) {
      bldr.checkTerm(currTerm);
    }
    return bldr;
  }

  public static ValueEvaluatorBuilder rule(EvaluationRule<String> rule, Term... terms) {
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
  public static ValueEvaluatorBuilder withinRange(Term term, Number lowerBound, Number upperBound) {
    return ValueEvaluatorBuilder.builder().addRule(term,
      NumericalValueEvaluationRuleBuilder.builder().boundedBy(lowerBound, upperBound).build());
  }

}
