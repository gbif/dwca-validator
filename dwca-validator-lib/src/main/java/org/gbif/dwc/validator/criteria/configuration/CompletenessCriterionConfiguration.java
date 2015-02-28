package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;
import org.gbif.dwc.validator.result.Result;

import java.util.List;

import com.google.common.collect.Lists;


/**
 * Container object holding CompletenessCriterion configurations.
 * 
 * @author cgendreau
 */
@CriterionConfigurationKey("completenessCriterion")
public class CompletenessCriterionConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;
  private List<String> absenceSynonyms;
  private Term term;

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

  public void addAbsenceSynonym(String absenceSynonym) {
    if (absenceSynonyms == null) {
      absenceSynonyms = Lists.newArrayList();
    }
    absenceSynonyms.add(absenceSynonym);
  }

  public List<String> getAbsenceSynonyms() {
    return absenceSynonyms;
  }

  public Result getLevel() {
    return level;
  }

  public void setLevel(Result level) {
    this.level = level;
  }
}
