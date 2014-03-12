package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.metadata.eml.Eml;

/**
 * @author melecoq
 */
public abstract class EMLEvaluator {

  /**
   * @author melecoq
   */
  public void doEval(Eml eml) {

  }

  public abstract void handleEval(Eml eml);
}
