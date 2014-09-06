package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;

import java.io.File;

/**
 * Main interface to initiate archive validation.
 * 
 * @author cgendreau
 */
public interface ArchiveValidatorIF {

  /**
   * Initiate validation of DwcA file or folder.
   * 
   * @param dwcaFile
   * @param resultAccumulator
   */
  void validateArchive(File dwcaFile, ResultAccumulatorIF resultAccumulator);
}
