package org.gbif.dwc.validator.criteria.configuration;

import org.gbif.dwc.validator.result.Result;


public abstract class AbstractRecordCriterionConfiguration {

  private String rowTypeRestriction;
  private Result level = Result.ERROR;

  public String getRowTypeRestriction() {
    return rowTypeRestriction;
  }

  public void setRowTypeRestriction(String rowTypeRestriction) {
    this.rowTypeRestriction = rowTypeRestriction;
  }

  public Result getLevel() {
    return level;
  }

  public void setLevel(Result level) {
    this.level = level;
  }

}
