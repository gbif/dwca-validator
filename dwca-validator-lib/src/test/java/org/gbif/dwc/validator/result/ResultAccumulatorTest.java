package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.mock.MockDataGenerator;
import org.gbif.dwc.validator.result.impl.FileWriterResultAccumulator;
import org.gbif.dwc.validator.result.impl.ThresholdResultAccumulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test regular usage of different ResultAccumulatorIF with a single thread.
 * 
 * @author cgendreau
 */
public class ResultAccumulatorTest {

  public static final int NUMBER_OF_RECORDS = 100000;

  @Test
  public void testFileWriterResultAccumulator() {
    long t = System.currentTimeMillis();
    String fileName = "test_FileWriterResultAccumulator.txt";
    ResultAccumulatorIF fwra = null;
    try {
      fwra = new FileWriterResultAccumulator(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<String> dummyIdList = MockDataGenerator.newRandomDataList(NUMBER_OF_RECORDS, 4);
    testResultAccumulator(fwra, dummyIdList);
    System.out.println("Using FileWriterResultAccumulator: " + (System.currentTimeMillis() - t) + " ms");

    Assert.assertEquals(NUMBER_OF_RECORDS, fwra.getCount());
    // clean up
    new File(fileName).delete();
  }

  private void testResultAccumulator(ResultAccumulatorIF fwra, List<String> dummyIdList) {
    for (String currDummyId : dummyIdList) {
      fwra.accumulate(new ValidationResult(currDummyId, "testResultAccumulator", null,
        new ArrayList<ValidationResultElement>()));
    }
    fwra.close();
  }

  @Test
  public void testThresholdResultAccumulator() {
    long t = System.currentTimeMillis();
    String fileName = "test_ThresholdResultAccumulator.txt";
    ResultAccumulatorIF tra = null;
    try {
      tra = new ThresholdResultAccumulator(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<String> dummyIdList = MockDataGenerator.newRandomDataList(NUMBER_OF_RECORDS, 4);
    testResultAccumulator(tra, dummyIdList);
    System.out.println("Using ThresholdResultAccumulator: " + (System.currentTimeMillis() - t) + " ms");
    Assert.assertEquals(NUMBER_OF_RECORDS, tra.getCount());
    // clean up
    new File(fileName).delete();
  }

}
