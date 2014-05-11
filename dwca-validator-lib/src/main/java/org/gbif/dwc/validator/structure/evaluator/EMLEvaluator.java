package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.metadata.eml.Eml;
import org.gbif.metadata.eml.ValidatorFactory;

/**
 * @author melecoq
 */
public abstract class EMLEvaluator {

  /**
   * @author melecoq
   */
  public void doEval(Eml eml) {

    ValidatorFactory.getGbifValidator().validate(eml);

  }

  public abstract void handleEval(Eml eml);
}
