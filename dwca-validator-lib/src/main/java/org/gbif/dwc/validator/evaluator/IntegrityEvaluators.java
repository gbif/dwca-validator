package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.validator.evaluator.integrity.UniquenessEvaluatorBuilder;

/**
 * Collection of builders related to integrity validations.
 * 
 * @author cgendreau
 */
public class IntegrityEvaluators {

  /**
   * Check the uniqueness of the coreId.
   * 
   * @return
   */
  public static UniquenessEvaluatorBuilder uniqueness() {
    return UniquenessEvaluatorBuilder.builder();
  }
}
