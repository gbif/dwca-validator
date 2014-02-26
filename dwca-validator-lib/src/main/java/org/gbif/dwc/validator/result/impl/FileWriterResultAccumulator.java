package org.gbif.dwc.validator.result.impl;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.ValidationResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResultAccumulatorIF implementation using a FileWriter.
 * 
 * @author cgendreau
 */
public class FileWriterResultAccumulator implements ResultAccumulatorIF {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileWriterResultAccumulator.class);
  private final FileWriter fw;
  private final AtomicInteger count;

  public FileWriterResultAccumulator(String filePath) throws IOException {
    fw = new FileWriter(new File(filePath));
    count = new AtomicInteger(0);
  }

  @Override
  public boolean accumulate(ValidationResult result) {
    // TODO of course, this is incomplete.
    String resultLine = result.getId();
    try {
      fw.write(resultLine);
      count.incrementAndGet();
    } catch (IOException ioEx) {
      LOGGER.error("Can't write to file using FileWriter", ioEx);
      return false;
    }
    return true;
  }

  @Override
  public void close() {
    if (fw != null) {
      try {
        fw.close();
      } catch (IOException ioEx) {
        LOGGER.error("Can't close FileWriter", ioEx);
      }
    }
  }

  @Override
  public int getCount() {
    return count.get();
  }

}
