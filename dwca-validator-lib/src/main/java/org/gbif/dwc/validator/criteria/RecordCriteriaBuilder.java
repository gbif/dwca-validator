package org.gbif.dwc.validator.criteria;

/**
 * Interface defining a RecordCriteria builder.
 * 
 * @author cgendreau
 */
public interface RecordCriteriaBuilder {


  /**
   * Build a concrete instance of RecordCriteria.
   * 
   * @return
   * @throws IllegalStateException
   */
  RecordCriteria build() throws IllegalStateException;
}
