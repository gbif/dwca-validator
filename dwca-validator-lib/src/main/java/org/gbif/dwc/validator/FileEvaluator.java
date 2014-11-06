package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;

import java.io.File;

/**
 * Main interface to initiate file evaluation.
 * 
 * @author cgendreau
 */
public interface FileEvaluator {

  /**
   * Initiate validation of a file or folder.
   * 
   * @param file to validate
   * @param resultAccumulator
   */
  void evaluateFile(File dwcaFile, ResultAccumulatorIF resultAccumulator);
}
