package org.gbif.dwc.validator.result.accumulator;

import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.ThresholdResultAccumulator;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of a TresholdResultAccumulator that allows to support different
 * serializations format using the same threshold mechanism.
 * 
 * @author cgendreau
 */
public abstract class AbstractTresholdResultAccumulator implements ResultAccumulatorIF {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThresholdResultAccumulator.class);
  protected static final int DEFAULT_THRESHOLD = 1000;
  private final ConcurrentLinkedQueue<ValidationResult> queue;

  private final int threshold;
  private AtomicInteger count = new AtomicInteger();
  private final AtomicBoolean flushing = new AtomicBoolean();

  public AbstractTresholdResultAccumulator() {
    this(DEFAULT_THRESHOLD);
  }

  public AbstractTresholdResultAccumulator(int threshold) {
    this.threshold = threshold;
    queue = new ConcurrentLinkedQueue<ValidationResult>();
    count = new AtomicInteger(0);
  }

  /**
   * This method is non-blocking except when a flush needs to be perform.
   * 
   * @param result
   * @return
   */
  @Override
  public boolean accumulate(ValidationResult result) {
    queue.add(result);

    if (count.incrementAndGet() == threshold && flushing.compareAndSet(false, true)) {
      flush(threshold);
    }
    return true;
  }

  /**
   * Open the underlying writer used to write results.
   * 
   * @throws IOException
   */
  protected abstract void openWriter() throws IOException;

  /**
   * Close the underlying writer used to write results.
   * 
   * @throws IOException
   */
  protected abstract void closeWriter() throws IOException;

  /**
   * Write a ValidationResult when the threshold is reached.
   * 
   * @param currentResult
   * @throws IOException
   */
  protected abstract void write(ValidationResult currentResult) throws IOException;

  @Override
  public void close() {
    flushAll();
    try {
      closeWriter();
    } catch (IOException ioEx) {
      LOGGER.error("Can't close AbstractTresholdResultAccumulator", ioEx);
    }
  }

  /**
   * Flush a specific number of records to the file.
   * 
   * @param howMany
   */
  private void flush(int howMany) {
    int numberWritten = 0;
    ValidationResult currentResult = queue.poll();
    try {
      while (currentResult != null && (numberWritten < howMany)) {
        write(currentResult);
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
    Iterator<ValidationResult> queueIterator = queue.iterator();
    ValidationResult currentResult = null;
    try {
      while (queueIterator.hasNext()) {
        currentResult = queueIterator.next();
        queueIterator.remove();
        write(currentResult);
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
