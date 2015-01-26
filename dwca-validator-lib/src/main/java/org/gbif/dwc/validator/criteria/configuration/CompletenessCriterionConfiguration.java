package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.transformation.ValueTransformation;

import java.util.ArrayList;
import java.util.List;


/**
 * Container object holding CompletenessCriterion configurations.
 * 
 * @author cgendreau
 */
@CriterionConfigurationKey("completenessCriterion")
public class CompletenessCriterionConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;

  private List<ValueTransformation<Boolean>> valueTransformations;
  private List<Term> terms;

  public void addTerm(Term term) {
    if (terms == null) {
      terms = new ArrayList<Term>();
    }
    terms.add(term);
  }

  public void addValueTransformation(ValueTransformation<Boolean> transformation) {
    if (valueTransformations == null) {
      valueTransformations = new ArrayList<ValueTransformation<Boolean>>();
    }
    valueTransformations.add(transformation);
  }

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public List<ValueTransformation<Boolean>> getValueTransformations() {
    return valueTransformations;
  }

  public void setValueTransformations(List<ValueTransformation<Boolean>> valueTransformations) {
    this.valueTransformations = valueTransformations;
  }

  public List<Term> getTerms() {
    return terms;
  }

  public void setTerms(List<Term> terms) {
    this.terms = terms;
  }

  public Result getLevel() {
    return level;
  }

  public void setLevel(Result level) {
    this.level = level;
  }
}
