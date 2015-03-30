package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.result.Result;

/**
 * Provide basic support for record based criterion configuration.
 *
 * @author cgendreau
 */
public abstract class AbstractRecordCriterionConfiguration {

  private Term rowTypeRestriction;
  private Result level = Result.ERROR;

  public Term getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(Term rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Result getLevel() {
    return level;
  }

  public void setLevel(Result level) {
    this.level = level;
  }

}
