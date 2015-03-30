package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.CriterionConfigurationKey;

import java.util.List;

import com.google.common.collect.Lists;


/**
 * Container object holding CompletenessCriterion configurations.
 *
 * @author cgendreau
 */
@CriterionConfigurationKey("completenessCriterion")
public class CompletenessCriterionConfiguration extends AbstractRecordCriterionConfiguration {

  private List<String> absenceSynonyms;
  private Term term;

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

}
