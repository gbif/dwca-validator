package org.gbif.dwc.validator.structure.evaluator;

import java.io.File;

/**
 * @author melecoq
 */
public abstract class MetaDescriptorEvaluator {

  /**
   * @author melecoq
   */
  public void doEval(File metaXML) {

  }

  public abstract void handleEval(File metaXML);
}
