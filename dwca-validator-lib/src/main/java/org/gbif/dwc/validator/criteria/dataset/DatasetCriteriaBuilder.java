package org.gbif.dwc.validator.criteria.dataset;

/**
 * Interface defining a DatasetCriteria builder.
 * 
 * @author cgendreau
 */
public interface DatasetCriteriaBuilder {


  /**
   * Build a concrete instance of DatasetCriteria.
   * 
   * @return
   * @throws IllegalStateException
   */
  DatasetCriteria build() throws IllegalStateException;
}
