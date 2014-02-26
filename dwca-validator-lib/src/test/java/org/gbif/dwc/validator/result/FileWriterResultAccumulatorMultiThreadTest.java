package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.result.impl.FileWriterResultAccumulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test FileWriterResultAccumulator in a multithreads context.
 * 
 * @author cgendreau
 */
public class FileWriterResultAccumulatorMultiThreadTest {

  private static final int NUMBER_OF_DATA = 10000;

  /**
   * Generate a list of random alphabetic strings
   * 
   * @param size
   * @param strLength
   * @return
   */
  public List<String> newRandomDataList(int size, int strLength) {
    List<String> dataList = new ArrayList<String>(size);
    for (int i = 0; i < size; i++) {
      dataList.add(RandomStringUtils.randomAlphabetic(strLength));
    }
    return dataList;
  }

  @Test
  public void testFileWriterResultAccumulator16Threads() throws InterruptedException, ExecutionException {
    testThread(16);
  }

  /**
   * Create and run testing thread(s)
   * 
   * @param threadCount
   * @throws InterruptedException
   * @throws ExecutionException
   */
  private void testThread(final int threadCount) throws InterruptedException, ExecutionException {
    FileWriterResultAccumulator initFwra = null;
    String fileName = "test_" + threadCount + ".txt";
    try {
      initFwra = new FileWriterResultAccumulator(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    final FileWriterResultAccumulator fwra = initFwra;

    List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
    for (int i = 0; i < threadCount; i++) {
      // new set of random data for each thread but same FileWriterResultAccumulator.
      final List<String> dummyIdList = newRandomDataList(NUMBER_OF_DATA, 4);
      final ValidationResult result = new ValidationResult();

      Callable<Boolean> task = new Callable<Boolean>() {

        @Override
        public Boolean call() {
          boolean success = true;
          for (String currDummyId : dummyIdList) {
            result.setId(currDummyId);
            success = (success && fwra.accumulate(result));
          }
          return success;
        }
      };
      tasks.add(task);
    }

    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    // call all threads and wait for completion
    List<Future<Boolean>> futures = executorService.invokeAll(tasks);

    fwra.close();
    new File(fileName).delete();
    // Validate
    Assert.assertEquals(futures.size(), threadCount);
    Assert.assertEquals(threadCount * NUMBER_OF_DATA, fwra.getCount());
  }
}
