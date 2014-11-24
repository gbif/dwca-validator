package org.gbif.dwc.validator.evaluator.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;

/**
 * Container object holding UniquenessEvaluator configurations.
 * 
 * @author cgendreau
 */
@RecordEvaluatorConfigurationKey
public class UniquenessEvaluatorConfiguration {

  private EvaluationContext evaluationContextRestriction;
  private String rowTypeRestriction;

  private Term term;
  private File workingFolder;

  public Term getTerm() {
    return term;
  }

  public void setTerm(Term term) {
    this.term = term;
  }

  public File getWorkingFolder() {
    return workingFolder;
  }

  public void setWorkingFolder(File workingFolder) {
    this.workingFolder = workingFolder;
  }

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public EvaluationContext getEvaluationContextRestriction() {
    return evaluationContextRestriction;
  }

  public void setEvaluationContextRestriction(EvaluationContext evaluationContextRestriction) {
    this.evaluationContextRestriction = evaluationContextRestriction;
  }

}
