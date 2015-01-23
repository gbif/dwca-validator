package org.gbif.dwc.validator.criteria.dataset;

/**
 * Interface defining a DatasetCriterion builder.
 * 
 * @author cgendreau
 */
public interface DatasetCriterionBuilder {


  /**
   * Build a concrete instance of DatasetCriterion.
   * 
   * @return
   * @throws IllegalStateException
   */
  DatasetCriteria build() throws IllegalStateException;
}
