package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.transformation.ValueTransformation;


public class RecordCriterion {

  public static BoundCriterionBuilder withinRange(Term term, Number lowerBound, Number upperBound) {
    return BoundCriterionBuilder.builder().termBoundedBy(term, lowerBound, upperBound);
  }

  public static MinMaxCriteriaBuilder minMax(Term minTerm, Term maxTerm) {
    return MinMaxCriteriaBuilder.builder().terms(minTerm, maxTerm);
  }

  public static InvalidCharacterCriteriaBuilder checkForInvalidCharacter(Term term) {
    return InvalidCharacterCriteriaBuilder.builder().onTerm(term);
  }

  public static TransformationBasedCriteriaBuilder tryTransformations(ValueTransformation<?>... transformations) {
    TransformationBasedCriteriaBuilder bldr = TransformationBasedCriteriaBuilder.builder();
    for (ValueTransformation<?> currTransformation : transformations) {
      bldr.appendTransformation(currTransformation);
    }
    return bldr;
  }

// public static RecordCompletionEvaluatorBuilder required(Term... terms) {
// RecordCompletionEvaluatorBuilder bldr = RecordCompletionEvaluatorBuilder.builder();
// for (Term currTerm : terms) {
// bldr.checkTerm(currTerm);
// }
// return bldr;
// }

}
