package org.gbif.dwc.validator.result;

import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.mock.MockDataGenerator;
import org.gbif.dwc.validator.result.accumulator.CSVValidationResultAccumulator;
import org.gbif.dwc.validator.result.accumulator.FileWriterResultAccumulator;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test regular usage of different ResultAccumulatorIF with a single thread.
 * 
 * @author cgendreau
 */
public class ResultAccumulatorTest {

  public static final int NUMBER_OF_RECORDS = 100000;

  private void testResultAccumulator(ResultAccumulator fwra, List<String> dummyIdList) {
    for (String currDummyId : dummyIdList) {
      try {
        fwra.accumulate(new ValidationResult(currDummyId, "testResultAccumulator", EvaluationContext.CORE, "",
          new ArrayList<ValidationResultElement>()));
        // also test AggregationResult
        fwra
          .accumulate(new AggregationResult<String>(currDummyId, "testResultAccumulator", EvaluationContext.CORE, "8"));
      } catch (ResultAccumulationException e) {
        e.printStackTrace();
        fail();
      }
    }
    fwra.close();
  }

  @Test
  public void testFileWriterResultAccumulator() {
    long t = System.currentTimeMillis();
    String fileName = "test_FileWriterResultAccumulator.txt";
    ResultAccumulator fwra = null;
    try {
      fwra = new FileWriterResultAccumulator(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<String> dummyIdList = MockDataGenerator.newRandomDataList(NUMBER_OF_RECORDS, 4);
    testResultAccumulator(fwra, dummyIdList);
    System.out.println("Using FileWriterResultAccumulator: " + (System.currentTimeMillis() - t) + " ms");

    assertEquals(NUMBER_OF_RECORDS, fwra.getValidationResultCount());
    assertEquals(NUMBER_OF_RECORDS, fwra.getAggregationResultCount());
    // clean up
    new File(fileName).delete();
  }

  @Test
  public void testThresholdResultAccumulator() {
    long t = System.currentTimeMillis();
    String validationResultFileName = "test_ThresholdResultAccumulator_Validation.txt";
    String aggregationResultFileName = "test_ThresholdResultAccumulator_Aggregation.txt";
    ResultAccumulator tra = null;

    tra = new CSVValidationResultAccumulator(validationResultFileName, aggregationResultFileName);
    List<String> dummyIdList = MockDataGenerator.newRandomDataList(NUMBER_OF_RECORDS, 4);
    testResultAccumulator(tra, dummyIdList);
    System.out.println("Using ThresholdResultAccumulator: " + (System.currentTimeMillis() - t) + " ms");
    assertEquals(NUMBER_OF_RECORDS, tra.getValidationResultCount());
    assertEquals(NUMBER_OF_RECORDS, tra.getAggregationResultCount());
    // clean up
    new File(validationResultFileName).delete();
    new File(aggregationResultFileName).delete();
  }

}
