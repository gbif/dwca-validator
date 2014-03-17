package org.gbif.dwc.validator;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.validator.handler.ArchiveStructureHandler;
import org.gbif.dwc.validator.impl.ArchiveValidator;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.metadata.eml.Eml;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests related to ArchiveValidator.
 * 
 * @author cgendreau
 */
public class ArchiveValidatorTest {

  private static class MockArchiveStructureHandler extends ArchiveStructureHandler {

    private boolean inspectArchiveContentCalled = false;
    private boolean inspectEMLCalled = false;
    private boolean inspectMetaXMLCalled = false;

    public boolean allInspectMethodsCalled() {
      return (inspectArchiveContentCalled && inspectEMLCalled && inspectMetaXMLCalled);
    }

    @Override
    public void inspectArchiveContent(Archive archive, ResultAccumulatorIF resultAccumulator) {
      super.inspectArchiveContent(archive,resultAccumulator);
      inspectArchiveContentCalled = true;
    }

    @Override
    public void inspectEML(Eml eml, ResultAccumulatorIF resultAccumulator) {
      super.inspectEML(eml,resultAccumulator);
      inspectEMLCalled = true;
    }

    @Override
    public void inspectMetaXML(File metaXML, ResultAccumulatorIF resultAccumulator) {
      super.inspectMetaXML(metaXML,resultAccumulator);
      inspectMetaXMLCalled = true;
    }
  }

  /**
   * Simply test that we can extract anm archive and call the appropriate method in ArchiveValidator.
   */
  @Test
  public void testValidateArchive() {
    ArchiveValidator validator = new ArchiveValidator();
    MockArchiveStructureHandler structureHandler = new MockArchiveStructureHandler();

    File testDwcFolder = new File("test-dwca-" + System.currentTimeMillis());
    testDwcFolder.mkdir();
    validator.setWorkingFolder(testDwcFolder.getAbsolutePath());
    validator.setStructureHandler(structureHandler);

    try {
      File testDwca = new File(getClass().getResource("/dwca/vascan_dwca.zip").toURI());
      validator.validateArchive(testDwca);
      // this archive does not contain EML file
      assertTrue(structureHandler.inspectArchiveContentCalled && structureHandler.inspectMetaXMLCalled);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }

    // delete test folder
    try {
      FileUtils.deleteDirectory(testDwcFolder);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
