package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.RegexCriterionConfiguration;

import com.google.common.base.Preconditions;

/**
 * Builder for BoundCriterion objects.
 * 
 * @author cgendreau
 */
@RecordCriterionBuilderKey("regexCriterion")
public class RegexCriterionBuilder implements RecordCriterionBuilder {

  private final RegexCriterionConfiguration configuration;

  public RegexCriterionBuilder() {
    configuration = new RegexCriterionConfiguration();
  }

  public RegexCriterionBuilder(RegexCriterionConfiguration configuration) {
    this.configuration = configuration;
  }

  public static RegexCriterionBuilder builder() {
    return new RegexCriterionBuilder();
  }

  /**
   * Set the Regular Expression on a term.
   * 
   * @param regex
   */
  public RegexCriterionBuilder regex(Term term, String regex) {
    configuration.setTerm(term);
    configuration.setRegex(regex);
    return this;
  }

  /**
   * Set the restriction on the rowType to avoid the evaluation to run on all rowType.
   * 
   * @param rowTypeRestriction
   * @return
   */
  public RegexCriterionBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
  }

  /**
   * Build RegexCriterion object.
   * 
   * @return immutable BoundCriteria object
   * @throws NullPointerException if the regex is null
   */
  @Override
  public RecordCriterion build() throws NullPointerException, IllegalStateException {

    Preconditions.checkNotNull(configuration.getRegex());

    return new RegexCriterion(configuration);
  }

}
