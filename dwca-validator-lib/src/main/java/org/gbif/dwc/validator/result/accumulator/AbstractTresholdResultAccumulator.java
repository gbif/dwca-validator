package org.gbif.dwc.validator.result.accumulator;

import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;
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
public abstract class AbstractTresholdResultAccumulator implements ResultAccumulator {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTresholdResultAccumulator.class);
  protected static final int DEFAULT_THRESHOLD = 1000;

  private final boolean recordValidationResult;
  private final boolean recordAggregationResult;

  private final ConcurrentLinkedQueue<ValidationResult> validationQueue;
  private final ConcurrentLinkedQueue<AggregationResult<?>> aggregationQueue;

  private final int threshold;
  private final AtomicInteger validationCount, aggregationCount;
  private final AtomicBoolean flushing = new AtomicBoolean();

  protected AbstractTresholdResultAccumulator(boolean recordValidationResult, boolean recordAggregationResult) {
    this(recordValidationResult, recordAggregationResult, DEFAULT_THRESHOLD);
  }

  protected AbstractTresholdResultAccumulator(boolean recordValidationResult, boolean recordAggregationResult,
    int threshold) {

    this.recordValidationResult = recordValidationResult;
    this.recordAggregationResult = recordAggregationResult;
    this.threshold = threshold;

    validationQueue = new ConcurrentLinkedQueue<ValidationResult>();
    aggregationQueue = new ConcurrentLinkedQueue<AggregationResult<?>>();
    validationCount = new AtomicInteger(0);
    aggregationCount = new AtomicInteger(0);
  }

  /**
   * This method is non-blocking except when a flush needs to be perform.
   * 
   * @param result
   * @return
   * @throws ResultAccumulationException
   */
  @Override
  public boolean accumulate(ValidationResult result) throws ResultAccumulationException {
    if (!recordValidationResult) {
      throw new ResultAccumulationException("This ResultAccumulator was not configured to record ValidationResult");
    }
    validationQueue.add(result);

    if (validationCount.incrementAndGet() == threshold && flushing.compareAndSet(false, true)) {
      try {
        flush(validationQueue, threshold);
      } catch (IOException ioEx) {
        throw new ResultAccumulationException(ioEx);
      }
    }
    return true;
  }

  @Override
  public boolean accumulate(AggregationResult<?> result) throws ResultAccumulationException {
    if (!recordAggregationResult) {
      throw new ResultAccumulationException("This ResultAccumulator was not configured to record AggregationResult");
    }
    aggregationQueue.add(result);

    if (aggregationCount.incrementAndGet() == threshold && flushing.compareAndSet(false, true)) {
      try {
        flush(aggregationQueue, threshold);
      } catch (IOException ioEx) {
        throw new ResultAccumulationException(ioEx);
      }
    }
    return true;
  }

  /**
   * Close the underlying writer used to write results.
   * 
   * @throws IOException
   */
  protected abstract void closeWriter() throws IOException;

  /**
   * Write a ValidationResult.
   * 
   * @param currentResult
   * @throws IOException
   */
  protected abstract void write(ValidationResult currentResult) throws IOException;

  /**
   * Write a ValidationResult.
   * 
   * @param currentResult
   * @throws IOException
   */
  protected abstract void write(AggregationResult<?> currentResult) throws IOException;

  /**
   * Fallback write method
   * 
   * @param evaluationResult
   * @throws IOException
   */
  protected void write(EvaluationResult evaluationResult) throws IOException {
    // we should think about something better than that
    if (evaluationResult instanceof ValidationResult) {
      write((ValidationResult) evaluationResult);
    } else if (evaluationResult instanceof AggregationResult) {
      write((AggregationResult<?>) evaluationResult);
    }
    // define fall back
  }

  @Override
  public void close() {
    flushAll(validationQueue);
    flushAll(aggregationQueue);
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
   * @throws IOException
   */
  private <T extends EvaluationResult> void flush(ConcurrentLinkedQueue<T> queue, int howMany) throws IOException {
    int numberWritten = 0;
    T currentResult = queue.poll();
    while (currentResult != null && (numberWritten < howMany)) {
      write(currentResult);
      numberWritten++;
      currentResult = queue.poll();
    }
    flushing.set(false);
  }

  /**
   * Flush all remaining content of the queue to the file.
   */
  private <T extends EvaluationResult> void flushAll(ConcurrentLinkedQueue<T> queue) {
    Iterator<T> queueIterator = queue.iterator();
    T currentResult = null;
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
  public int getValidationResultCount() {
    return validationCount.get();
  }

  @Override
  public int getAggregationResultCount() {
    return aggregationCount.get();
  }

}
