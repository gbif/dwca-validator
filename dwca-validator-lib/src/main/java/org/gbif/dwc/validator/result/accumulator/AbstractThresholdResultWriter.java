package org.gbif.dwc.validator.result.accumulator;

import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationResult;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract threshold based writer for a single EvaluationResult implementation.
 * 
 * @author cgendreau
 * @param <T>
 */
public abstract class AbstractThresholdResultWriter<T extends EvaluationResult> {

  protected static final int DEFAULT_THRESHOLD = 1000;

  private final int threshold;
  private final AtomicInteger count;
  private final ConcurrentLinkedQueue<T> queue;
  private final AtomicBoolean flushing;

  public AbstractThresholdResultWriter(int threshold) {
    this.threshold = threshold;

    queue = new ConcurrentLinkedQueue<T>();
    count = new AtomicInteger(0);

    flushing = new AtomicBoolean();
  }

  protected abstract void write(T result) throws IOException;;

  protected abstract void closeWriter() throws IOException;

  public boolean accumulate(T result) throws ResultAccumulationException {
    queue.add(result);

    if (count.incrementAndGet() == threshold && flushing.compareAndSet(false, true)) {
      try {
        flush(threshold);
      } catch (IOException ioEx) {
        throw new ResultAccumulationException(ioEx);
      }
    }
    return true;
  }

  public void close() throws ResultAccumulationException {
    try {
      flushAll();
      closeWriter();
    } catch (IOException ioEx) {
      throw new ResultAccumulationException(ioEx);
    }
  }

  /**
   * Flush a specific number of ValidationResult records to the file.
   * 
   * @param howMany
   * @throws IOException
   */
  private void flush(int howMany) throws IOException {
    int numberWritten = 0;
    T currentResult = queue.poll();
    while (currentResult != null && (numberWritten < howMany)) {
      write(currentResult);
      numberWritten++;
      currentResult = queue.poll();
    }
    flushing.set(false);
  }

  private void flushAll() throws IOException {
    Iterator<T> queueIterator = queue.iterator();
    T currentResult = null;
    while (queueIterator.hasNext()) {
      currentResult = queueIterator.next();
      queueIterator.remove();
      write(currentResult);
    }
  }

  public int getCount() {
    return count.get();
  }
}
