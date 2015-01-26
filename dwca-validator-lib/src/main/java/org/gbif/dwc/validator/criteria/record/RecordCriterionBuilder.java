package org.gbif.dwc.validator.criteria.record;


/**
 * Interface defining a RecordCriterion builder.
 * 
 * @author cgendreau
 */
public interface RecordCriterionBuilder {


  /**
   * Build a concrete instance of RecordCriterion.
   * 
   * @return
   * @throws IllegalStateException
   */
  RecordCriterion build() throws IllegalStateException;
}
