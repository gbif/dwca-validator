package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.mock.MockDataGenerator;
import org.gbif.dwc.validator.result.impl.FileWriterResultAccumulator;
import org.gbif.dwc.validator.result.impl.ThresholdResultAccumulator;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test FileWriterResultAccumulator in a multi-thread context.
 * 
 * @author cgendreau
 */
public class ResultAccumulatorMultiThreadTest {

  private static final int NUMBER_OF_DATA = 10000;

  @Test
  public void testFileWriterResultAccumulator16Threads() throws InterruptedException, ExecutionException {
    long t = System.currentTimeMillis();
    ResultAccumulatorIF fwra = null;
    String fileName = "test_FileWriterResultAccumulator16Threads.txt";
    try {
      fwra = new FileWriterResultAccumulator(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }

    testThread(fwra, 16);
    System.out.println("FileWriterResultAccumulator took " + (System.currentTimeMillis() - t) + " ms");
    // clean up
    new File(fileName).delete();
  }

  /**
   * Create and run testing thread(s)
   * 
   * @param threadCount
   * @throws InterruptedException
   * @throws ExecutionException
   */
  private void testThread(final ResultAccumulatorIF resultAccumulator, final int threadCount)
    throws InterruptedException, ExecutionException {

    List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
    for (int i = 0; i < threadCount; i++) {
      // new set of random data for each thread but same FileWriterResultAccumulator.
      final List<String> dummyIdList = MockDataGenerator.newRandomDataList(NUMBER_OF_DATA, 4);

      Callable<Boolean> task = new Callable<Boolean>() {

        @Override
        public Boolean call() {
          boolean success = true;
          for (String currDummyId : dummyIdList) {
            success =
              (success && resultAccumulator.accumulate(new ValidationResult(currDummyId, "testResultAccumulator", null,
                new ArrayList<ValidationResultElement>())));
          }
          return success;
        }
      };
      tasks.add(task);
    }

    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    // call all threads and wait for completion
    List<Future<Boolean>> futures = executorService.invokeAll(tasks);

    resultAccumulator.close();
    // Validate
    Assert.assertEquals(futures.size(), threadCount);
    Assert.assertEquals(threadCount * NUMBER_OF_DATA, resultAccumulator.getCount());
  }

  @Test
  public void testThresholdResultAccumulator16Threads() throws InterruptedException, ExecutionException {
    long t = System.currentTimeMillis();
    ResultAccumulatorIF ra = null;
    String fileName = "test_ThresholdResultAccumulator16Threads.txt";
    try {
      ra = new ThresholdResultAccumulator(fileName, 1000);
    } catch (IOException e) {
      e.printStackTrace();
    }

    testThread(ra, 16);
    System.out.println("ThresholdResultAccumulator took " + (System.currentTimeMillis() - t) + " ms");
    // clean up
    new File(fileName).delete();
  }
}
