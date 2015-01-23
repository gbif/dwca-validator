package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;

/**
 * Container object holding ReferenceUniqueEvaluator configurations.
 * 
 * @author cgendreau
 */
@CriterionConfigurationKey("referenceUniqueCriteria")
public class ReferenceUniqueCriteriaConfiguration {

  private EvaluationContext evaluationContextRestriction;
  private String rowTypeRestriction;

  private Term term;
  private String multipleValuesSeparator;
  private File workingFolder;

  // private UniquenessEvaluatorConfiguration uniquenessEvaluatorConfiguration;

  public EvaluationContext getEvaluationContextRestriction() {
    return evaluationContextRestriction;
  }

  public void setEvaluationContextRestriction(EvaluationContext evaluationContextRestriction) {
    this.evaluationContextRestriction = evaluationContextRestriction;
  }

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Term getTerm() {
    return term;
  }

  public void setTerm(Term term) {
    this.term = term;
  }

  public String getMultipleValuesSeparator() {
    return multipleValuesSeparator;
  }

  public void setMultipleValuesSeparator(String multipleValuesSeparator) {
    this.multipleValuesSeparator = multipleValuesSeparator;
  }

  public File getWorkingFolder() {
    return workingFolder;
  }

  public void setWorkingFolder(File workingFolder) {
    this.workingFolder = workingFolder;
  }
}
