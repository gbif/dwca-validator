package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.text.ArchiveFile;


/**
 * The EvaluationChainProviderIF allows to hide the technique used to link all the chain related dependencies and
 * configurations.
 * 
 * @author cgendreau
 */
public interface EvaluationChainProviderIF {

  /**
   * Return the validation chain that should be used against the core.
   * 
   * @param archiveFile used to only includes necessary elements in the validation chain for a specific archive
   * @return head of the validation chain
   */
  public ChainableRecordEvaluator getCoreChain(ArchiveFile archiveFile);

}
