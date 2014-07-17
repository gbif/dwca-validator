package org.gbif.utils;

import org.gbif.util.ToBeMovedFileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * THIS FILE NEEDS TO BE MOVED TO org.gbif.utils.file.FileUtils after review.
 * 
 * @author cgendreau
 */
public class ToBeMovedFileUtilsTest {

  @Test
  public void testDiffFileInJava() {
    boolean success = false;
    try {
      File referenceFile = new File(this.getClass().getResource("/files/referenceFile.txt").toURI());
      File inputFile = new File(this.getClass().getResource("/files/inputFile.txt").toURI());

      File resultFile = File.createTempFile("testResult", "txt");

      ToBeMovedFileUtils fu = new ToBeMovedFileUtils();
      success = fu.diffFileInJava(referenceFile, inputFile, resultFile);

      // compare expected file
      File expectedFile = new File(this.getClass().getResource("/files/expectedResultFile.txt").toURI());
      assertTrue(org.apache.commons.io.FileUtils.contentEqualsIgnoreEOL(expectedFile, resultFile, "UTF-8"));
      resultFile.delete();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    assertTrue(success);
  }

  @Test
  public void testDiffFileInUnix() {
    boolean success = false;
    try {
      File referenceFile = new File(this.getClass().getResource("/files/referenceFile.txt").toURI());
      File inputFile = new File(this.getClass().getResource("/files/inputFile.txt").toURI());

      File resultFile = File.createTempFile("testResult", "txt");

      ToBeMovedFileUtils fu = new ToBeMovedFileUtils();
      success = fu.diffFileInUnix(referenceFile, inputFile, resultFile);

      // compare expected file
      File expectedFile = new File(this.getClass().getResource("/files/expectedResultFile.txt").toURI());
      assertTrue(org.apache.commons.io.FileUtils.contentEqualsIgnoreEOL(expectedFile, resultFile, "UTF-8"));
      resultFile.delete();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    // do not fail if the test is running on non-unix OS
    assertTrue(success || !SystemUtils.IS_OS_UNIX);
  }
}
