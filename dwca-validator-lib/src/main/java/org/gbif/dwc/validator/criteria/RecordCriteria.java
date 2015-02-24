package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.record.BoundCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.CompletenessCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.InvalidCharacterCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.MinMaxCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.TransformationBasedCriteriaBuilder;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.transformation.ValueTransformation;

/**
 * Main builder for record criteria.
 * 
 * @author cgendreau
 */
public class RecordCriteria {

  /**
   * Get a configured builder to check if the value of a term is between bounds.
   * 
   * @param term
   * @param lowerBound
   * @param upperBound
   * @return
   */
  public static BoundCriterionBuilder withinRange(Term term, Number lowerBound, Number upperBound) {
    return BoundCriterionBuilder.builder().termBoundedBy(term, lowerBound, upperBound);
  }

  public static MinMaxCriterionBuilder minMax(Term minTerm, Term maxTerm) {
    return MinMaxCriterionBuilder.builder().terms(minTerm, maxTerm);
  }

  public static InvalidCharacterCriterionBuilder checkForInvalidCharacter(Term term) {
    return InvalidCharacterCriterionBuilder.builder().onTerm(term);
  }

  /**
   * @param transformations
   * @return
   */
  public static TransformationBasedCriteriaBuilder tryTransformations(ValueTransformation<?>... transformations) {
    TransformationBasedCriteriaBuilder bldr = TransformationBasedCriteriaBuilder.builder();
    for (ValueTransformation<?> currTransformation : transformations) {
      bldr.appendTransformation(currTransformation);
    }
    return bldr;
  }

  /**
   * Get a builder configured to check that each records provide a value for the terms.
   * If no value is provided for a term, a the result will be flagged as Result.ERROR.
   * 
   * @param terms
   * @return
   */
  public static CompletenessCriterionBuilder required(Term... terms) {
    CompletenessCriterionBuilder bldr = CompletenessCriterionBuilder.builder();
    for (Term currTerm : terms) {
      bldr.checkTerm(currTerm);
    }
    bldr.setLevel(Result.ERROR);
    return bldr;
  }

  /**
   * Get a builder configured to check that each records provide a value for the terms.
   * If no value is provided for a term, a the result will be flagged as Result.WARNING.
   * 
   * @param terms
   * @return
   */
  public static CompletenessCriterionBuilder desired(Term... terms) {
    CompletenessCriterionBuilder bldr = CompletenessCriterionBuilder.builder();
    for (Term currTerm : terms) {
      bldr.checkTerm(currTerm);
    }
    bldr.setLevel(Result.WARNING);
    return bldr;
  }

}
