package org.gbif.dwc.validator.impl;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.gbif.dwc.validator.ArchiveValidatorIF;
import org.gbif.dwc.validator.handler.ArchiveContentHandler;
import org.gbif.dwc.validator.handler.ArchiveStructureHandler;
import org.gbif.metadata.eml.Eml;
import org.gbif.metadata.eml.EmlFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Main DarwinCore archive validation implementation.
 * 
 * @author cgendreau
 */
public class ArchiveValidator implements ArchiveValidatorIF {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveValidator.class);
  private static final String META_XML_FILE = "meta.xml";

  private String workingFolder = ".";

  private ArchiveStructureHandler structureHandler;

  // contentHandler is used to validate core and extensions
  private ArchiveContentHandler contentHandler;

  public void setStructureHandler(ArchiveStructureHandler structureHandler) {
    this.structureHandler = structureHandler;
  }

  public void setWorkingFolder(String workingFolder) {
    this.workingFolder = workingFolder;
  }

  @Override
  public void validateArchive(File dwcaFile) {
    File tmpFolder = new File(new File(workingFolder), UUID.randomUUID().toString());
    try {
      Archive dwc = ArchiveFactory.openArchive(dwcaFile, tmpFolder);
      structureHandler.inspectArchiveContent(dwc);

      File metaFile = new File(tmpFolder, META_XML_FILE);
      if (metaFile.exists()) {
        structureHandler.inspectMetaXML(metaFile);
      }

      if (dwc.getMetadataLocation() != null) {
        Eml eml = EmlFactory.build(new FileInputStream(dwc.getMetadataLocationFile()));
        structureHandler.inspectEML(eml);
      }

      // structureHandler.inspectEML(dwc.);
    } catch (UnsupportedArchiveException e) {
      LOGGER.error("Can't open archive", e);
    } catch (IOException e) {
      LOGGER.error("Can't open archive", e);
    } catch (SAXException e) {
      LOGGER.error("Can't read EML", e);
    }
  }

}
