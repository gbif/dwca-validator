package org.gbif.dwc.validator.criteria;

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
  RecordCriterionIF build() throws IllegalStateException;
}
