package org.gbif.dwc.validator;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.validator.evaluator.chain.DefaultEvaluationChainProvider;
import org.gbif.dwc.validator.handler.ArchiveContentHandler;
import org.gbif.dwc.validator.handler.ArchiveStructureHandler;
import org.gbif.dwc.validator.impl.ArchiveValidator;
import org.gbif.dwc.validator.result.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

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
      super.inspectArchiveContent(archive, resultAccumulator);
      inspectArchiveContentCalled = true;
    }

    @Override
    public void inspectEML(File eml, ResultAccumulatorIF resultAccumulator) {
      super.inspectEML(eml, resultAccumulator);
      inspectEMLCalled = true;
    }

    @Override
    public void inspectMetaXML(File metaXML, ResultAccumulatorIF resultAccumulator) {
      super.inspectMetaXML(metaXML, resultAccumulator);
      inspectMetaXMLCalled = true;
    }
  }

  /**
   * Simply test that we can extract an archive and call the appropriate method in ArchiveValidator.
   */
  @Test
  public void testValidateArchive() {
    ArchiveValidator validator = new ArchiveValidator();
    MockArchiveStructureHandler structureHandler = new MockArchiveStructureHandler();

    File testDwcFolder = new File("test-dwca-" + System.currentTimeMillis());
    testDwcFolder.mkdir();
    validator.setWorkingFolder(testDwcFolder.getAbsolutePath());
    validator.setStructureHandler(structureHandler);
    validator.setContentHandler(new ArchiveContentHandler(new DefaultEvaluationChainProvider()));

    try {
      File testDwca = new File(getClass().getResource("/dwca/vascan_dwca.zip").toURI());

      validator.validateArchive(testDwca, new InMemoryResultAccumulator());
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
