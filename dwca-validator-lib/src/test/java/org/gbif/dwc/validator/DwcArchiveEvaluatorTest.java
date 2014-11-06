package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests related to DwcArchiveEvaluator.
 * 
 * @author cgendreau
 */
public class DwcArchiveEvaluatorTest {

  /**
   * Simply test that we can extract an archive and call the appropriate method in ArchiveValidator.
   */
  @Test
  public void testValidateArchive() {

    File testDwcFolder = new File("test-dwca-" + System.currentTimeMillis());
    testDwcFolder.mkdir();

    FileEvaluator validator = Evaluators.defaultChain(testDwcFolder).build();

    try {
      File testDwca = new File(getClass().getResource("/dwca/vascan_dwca.zip").toURI());
      InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
      validator.evaluateFile(testDwca, resultAccumulator);

      // simply ensure we have no results
      assertTrue(resultAccumulator.getValidationResultList().isEmpty());
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
