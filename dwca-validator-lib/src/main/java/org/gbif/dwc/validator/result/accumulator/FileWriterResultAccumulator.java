package org.gbif.dwc.validator.result.accumulator;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResultAccumulatorIF implementation using a FileWriter.
 * This implementation writes directly to the file on each 'accumulate' calls.
 * TODO: This writer is incomplete!
 * 
 * @author cgendreau
 */
public class FileWriterResultAccumulator implements ResultAccumulatorIF {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileWriterResultAccumulator.class);
  private final FileWriter fw;
  private final AtomicInteger validationCount;
  private final AtomicInteger aggregationCount;

  public FileWriterResultAccumulator(String filePath) throws IOException {
    fw = new FileWriter(new File(filePath));
    validationCount = new AtomicInteger(0);
    aggregationCount = new AtomicInteger(0);
  }

  @Override
  public boolean accumulate(ValidationResult result) {
    // TODO of course, this is incomplete.
    String resultLine = result.getId();
    try {
      fw.write(resultLine);
      validationCount.incrementAndGet();
    } catch (IOException ioEx) {
      LOGGER.error("Can't write to file using FileWriter", ioEx);
      return false;
    }
    return true;
  }

  @Override
  public boolean accumulate(AggregationResult<?> result) {
    String resultLine = result.getId();
    try {
      fw.write(resultLine);
      aggregationCount.incrementAndGet();
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
        fw.flush();
        fw.close();
      } catch (IOException ioEx) {
        LOGGER.error("Can't close FileWriter", ioEx);
      }
    }
  }

  @Override
  public int getValidationResultCount() {
    return validationCount.get();
  }

  @Override
  public int getAggregationResultCount() {
    return aggregationCount.get();
  }
}
