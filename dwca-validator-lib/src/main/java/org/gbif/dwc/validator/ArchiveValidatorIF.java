package org.gbif.dwc.validator;

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
   */
  public void validateArchive(File dwcaFile);
}
