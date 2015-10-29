package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.ResultAccumulator;

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
   * @param source or folder to validate
   * @param resultAccumulator
   */
  void evaluateFile(File source, ResultAccumulator resultAccumulator);
}
