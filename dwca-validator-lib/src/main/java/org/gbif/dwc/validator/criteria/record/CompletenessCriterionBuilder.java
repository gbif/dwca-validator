package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionBuilderKey;
import org.gbif.dwc.validator.criteria.configuration.CompletenessCriterionConfiguration;
import org.gbif.dwc.validator.result.Result;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder of CompletenessCriterion objects.
 * 
 * @author cgendreau
 */
@RecordCriterionBuilderKey("completenessCriterion")
public class CompletenessCriterionBuilder implements RecordCriterionBuilder {

  private final CompletenessCriterionConfiguration configuration;

  public CompletenessCriterionBuilder() {
    configuration = new CompletenessCriterionConfiguration();
  }

  public CompletenessCriterionBuilder(CompletenessCriterionConfiguration configuration) {
    this.configuration = configuration;
  }

  public static CompletenessCriterionBuilder builder() {
    return new CompletenessCriterionBuilder();
  }

  /**
   * Build CompletenessCriterion object.
   * 
   * @return immutable CompletenessCriterion object
   * @throws IllegalStateException if no terms were specified
   */
  @Override
  public RecordCriterion build() throws IllegalStateException {

    Preconditions.checkState(configuration.getTerm() != null, "A term must be set");

    Preconditions.checkState(configuration.getLevel() == Result.ERROR || configuration.getLevel() == Result.WARNING,
      "Level must be set to ERROR or WARNING");

    // we need a RowTypeRestriction otherwise all extension records could be flagged as incomplete
    // even if the don't use the term by definition.
    Preconditions.checkState(StringUtils.isNotBlank(configuration.getRowTypeRestriction()),
      "A RowTypeRestriction must be provided");

    return new CompletenessCriterion(configuration);
  }

  /**
   * Check the provided Term for completion.
   * 
   * @param term
   * @return
   */
  public CompletenessCriterionBuilder checkTerm(Term term) {
    configuration.setTerm(term);
    return this;
  }

  /**
   * Check the provided Term for completion and specify 1 or more absence synonyms.
   * 
   * @param term
   * @param absenceSynonyms strings that should be considered as an absence of value e.g. "null", "na"
   * @return
   */
  public CompletenessCriterionBuilder checkTerm(Term term, String... absenceSynonyms) {
    configuration.setTerm(term);

    for (String as : absenceSynonyms) {
      configuration.addAbsenceSynonym(as);
    }
    return this;
  }

  public CompletenessCriterionBuilder setLevel(Result level) {
    configuration.setLevel(level);
    return this;
  }

  /**
   * Set the restriction on the rowType to avoid the evaluation to run on all rowType.
   * 
   * @param rowTypeRestriction
   * @return
   */
  public CompletenessCriterionBuilder onRowType(Term rowTypeRestriction) {
    configuration.setRowTypeRestriction(rowTypeRestriction.qualifiedName());
    return this;
  }
}
