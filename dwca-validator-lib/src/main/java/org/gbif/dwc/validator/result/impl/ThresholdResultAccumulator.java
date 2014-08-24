package org.gbif.dwc.validator.result.impl;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.EvaluationResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResultAccumulatorIF implementation that use a threshold to determine when to flush to a file.
 * 
 * @author cgendreau
 */
public class ThresholdResultAccumulator implements ResultAccumulatorIF {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThresholdResultAccumulator.class);
  protected static final int DEFAULT_THRESHOLD = 1000;
  private final ConcurrentLinkedQueue<EvaluationResult> queue;

  private final FileWriter fw;
  private final int threshold;
  private AtomicInteger count = new AtomicInteger();
  private final AtomicBoolean flushing = new AtomicBoolean();

  public ThresholdResultAccumulator(String filePath) throws IOException {
    this(filePath, DEFAULT_THRESHOLD);
  }

  public ThresholdResultAccumulator(String filePath, int threshold) throws IOException {
    fw = new FileWriter(new File(filePath));
    this.threshold = threshold;
    queue = new ConcurrentLinkedQueue<EvaluationResult>();
    count = new AtomicInteger(0);
  }

  /**
   * This method is non-blocking except when a flush needs to be perform.
   * 
   * @param result
   * @return
   */
  @Override
  public boolean accumulate(EvaluationResult result) {
    queue.add(result);

    if (count.incrementAndGet() == threshold && flushing.compareAndSet(false, true)) {
      flush(threshold);
    }
    return true;
  }

  @Override
  public void close() {
    try {

      flushAll();

      fw.flush();
      fw.close();
    } catch (IOException ioEx) {
      LOGGER.error("Can't close NonBlockingResultAccumulator properly", ioEx);
    }

  }

  /**
   * Flush a specific number of records to the file.
   * 
   * @param howMany
   */
  private void flush(int howMany) {
    int numberWritten = 0;
    EvaluationResult currentResult = queue.poll();
    String resultLine = null;
    try {
      while (currentResult != null && (numberWritten < howMany)) {
        resultLine = currentResult.getId();
        fw.write(resultLine);
        numberWritten++;

        currentResult = queue.poll();
      }
    } catch (IOException ioEx) {
      LOGGER.error("Can't flush to file using FileWriter", ioEx);
    }
    flushing.set(false);
  }

  /**
   * Flush all remaining content of the queue to the file.
   */
  private void flushAll() {
    Iterator<EvaluationResult> queueIterator = queue.iterator();
    EvaluationResult currentResult = null;
    try {
      while (queueIterator.hasNext()) {
        currentResult = queueIterator.next();
        queueIterator.remove();
        String resultLine = currentResult.getId();
        fw.write(resultLine);
      }
    } catch (IOException ioEx) {
      LOGGER.error("Can't flush to file using FileWriter", ioEx);
    }
  }

  @Override
  public int getCount() {
    return count.get();
  }

}
