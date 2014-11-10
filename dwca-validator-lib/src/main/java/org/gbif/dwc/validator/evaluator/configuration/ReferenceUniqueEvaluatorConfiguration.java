package org.gbif.dwc.validator.evaluator.configuration;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;

/**
 * Container object holding ReferenceUniqueEvaluator configurations.
 * 
 * @author cgendreau
 */
@RecordEvaluatorConfigurationKey
public class ReferenceUniqueEvaluatorConfiguration {

  private EvaluationContext evaluationContextRestriction;
  private String rowTypeRestriction;

  private ConceptTerm term;
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

  public ConceptTerm getTerm() {
    return term;
  }

  public void setTerm(ConceptTerm term) {
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
