package org.gbif.dwc.validator.handler;

import org.gbif.dwc.text.Archive;
import org.gbif.metadata.eml.Eml;

import java.io.File;

/**
 * Handler responsible for the validation of the structure of the archive.
 * Structure of the archive represents the archive structure itself, the metadata and the EML file.
 * 
 * @author cgendreau
 */
public class ArchiveStructureHandler {

  /**
   * Inspect the content of the archive to ensure all needed files are present.
   * 
   * @param archive
   */
  public void inspectArchiveContent(Archive archive) {

  }

  public void inspectEML(Eml eml) {

  }

  public void inspectMetaXML(File metaXML) {

  }


}
