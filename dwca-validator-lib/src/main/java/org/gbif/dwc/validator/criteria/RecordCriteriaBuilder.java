package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.record.BoundCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.InvalidCharacterCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.MinMaxCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.TransformationBasedCriteriaBuilder;
import org.gbif.dwc.validator.transformation.ValueTransformation;


public class RecordCriteriaBuilder {

  public static BoundCriterionBuilder withinRange(Term term, Number lowerBound, Number upperBound) {
    return BoundCriterionBuilder.builder().termBoundedBy(term, lowerBound, upperBound);
  }

  public static MinMaxCriterionBuilder minMax(Term minTerm, Term maxTerm) {
    return MinMaxCriterionBuilder.builder().terms(minTerm, maxTerm);
  }

  public static InvalidCharacterCriterionBuilder checkForInvalidCharacter(Term term) {
    return InvalidCharacterCriterionBuilder.builder().onTerm(term);
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
