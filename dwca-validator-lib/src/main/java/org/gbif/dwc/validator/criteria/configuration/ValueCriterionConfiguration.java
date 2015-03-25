package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.transformation.ValueTransformation;

import com.google.common.base.Predicate;


public class ValueCriterionConfiguration<T> extends AbstractRecordCriterionConfiguration {

  // in theory, we could support more than one term of the transformation returns a object build from multiple terms
  private Term term;
  private Predicate<T> predicate;
  private ValueTransformation<T> transformation;

  public Predicate<T> getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate<T> predicate) {
    this.predicate = predicate;
  }

  public ValueTransformation<T> getTransformation() {
    return transformation;
  }

  public void setTransformation(ValueTransformation<T> transformation) {
    this.transformation = transformation;
  }

  public Term getTerm() {
    return term;
  }

  public void setTerm(Term term) {
    this.term = term;
  }
}
