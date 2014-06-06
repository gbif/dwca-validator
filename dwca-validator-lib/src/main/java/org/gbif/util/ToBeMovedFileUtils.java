package org.gbif.util;

import org.gbif.utils.file.InputStreamUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * THIS FILE NEEDS TO BE MOVED TO org.gbif.utils.file.FileUtils after review.
 * 
 * @author cgendreau
 */
public class ToBeMovedFileUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ToBeMovedFileUtils.class);

  /**
   * Write to result all the lines from the input file that were NOT found in reference file.
   * referenceFile and inputFile must be sorted.
   * 
   * @param referenceFile sorted file
   * @param inputFile sorted file
   * @param result file where to write results
   * @return
   */
  public boolean diffFileInJava(File referenceFile, File inputFile, File result) {
    PrintWriter resultWriter = null;
    BufferedReader inputFileBr = null;
    BufferedReader refFileBr = null;
    boolean success = false;

    try {
      String inputFileLine;
      String refFileLine;

      inputFileBr = new BufferedReader(new FileReader(inputFile));
      refFileBr = new BufferedReader(new FileReader(referenceFile));
      resultWriter = new PrintWriter(new FileWriter(result));

      inputFileLine = inputFileBr.readLine();
      refFileLine = refFileBr.readLine();

      while (inputFileLine != null) {
        while (refFileLine != null) {
          if (!refFileLine.equals(inputFileLine)) {
            // compare the String to determine if it's not found or if we are not there yet
            if (refFileLine.compareTo(inputFileLine) > 0) {
              resultWriter.println(inputFileLine);
              break;
            } else {
              refFileLine = refFileBr.readLine();
            }
          } else {
            // if found, break the refFileLine loop
            break;
          }
        }

        inputFileLine = inputFileBr.readLine();
      }
      success = true;
    } catch (IOException e) {
      LOG.warn("Caught Exception", e);
    } finally {
      IOUtils.closeQuietly(inputFileBr);
      IOUtils.closeQuietly(refFileBr);
      IOUtils.closeQuietly(resultWriter);
    }

    return success;
  }

  /**
   * Write to result all the lines from the input file that were NOT found in reference file.
   * referenceFile and inputFile must be sorted.
   * This is done using 'grep' on unix-based system.
   * 
   * @param referenceFile sorted file
   * @param inputFile sorted file
   * @param result file where to write results
   * @return
   * @throws IOException
   */
  public boolean diffFileInUnix(File referenceFile, File inputFile, File result) throws IOException {

    boolean success = false;
    try {
      LinkedList<String> cmds = new LinkedList<String>();
      cmds.add("/bin/sh");
      cmds.add("-c");
      ProcessBuilder pb = new ProcessBuilder(cmds);
      Map<String, String> env = pb.environment();
      env.clear();
      // make sure we use the C locale (not sure it is useful for grep)
      env.put("LC_ALL", "C");

      String command =
        "grep -Fxvf " + referenceFile.getAbsolutePath() + ' ' + inputFile.getAbsolutePath() + " > "
          + result.getAbsolutePath();

      cmds.add(command);
      Process process = pb.start();
      // get the stdout and stderr from the command that was run
      InputStream err = process.getErrorStream();
      int exitValue = process.waitFor();
      if (exitValue == 0) {
        success = true;
      } else {
        LOG.warn("Error sorting file with unix grep");
        InputStreamUtils isu = new InputStreamUtils();
        System.err.append(isu.readEntireStream(err));
      }
    } catch (Exception e) {
      LOG.warn("Caught Exception", e);
    }
    return success;
  }

}
