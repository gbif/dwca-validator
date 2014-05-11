package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.metadata.MetadataException;
import org.gbif.metadata.MetadataFactory;

import java.io.File;

/**
 * @author melecoq
 */
public class MetaDescriptorEvaluator {

  /**
   * @author melecoq
   * @throws MetadataException
   */
  public void doEval(File metaXML) throws MetadataException {
    handleEval(metaXML);
  }

  protected void handleEval(File metaXML) throws MetadataException {
    MetadataFactory metadataValidatator = new MetadataFactory();
    metadataValidatator.read(metaXML);
  }
}
