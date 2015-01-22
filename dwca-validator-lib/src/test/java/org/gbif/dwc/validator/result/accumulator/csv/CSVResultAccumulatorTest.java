package org.gbif.dwc.validator.result.accumulator.csv;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test ensures we generate the CSV result file as expected.
 * 
 * @author cgendreau
 */
public class CSVResultAccumulatorTest {

  private static final String key = "mockCriteria";

  @Test
  public void testCSVResultAccumulator() {
    String validationResultFileName = "test_CSVResultAccumulator_Validation.txt";
    File validationResultFile = new File(validationResultFileName);

    List<ValidationResultElement> resultElements = new ArrayList<ValidationResultElement>();

    ValidationResultElement vre1 =
      new ValidationResultElement(key, ContentValidationType.FIELD_UNIQUENESS, Result.ERROR, "not unique");
    ValidationResultElement vre2 =
      new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, Result.ERROR,
        "contains invalid characters");

    resultElements.add(vre1);
    resultElements.add(vre2);

    ResultAccumulator ra = new CSVResultAccumulator(validationResultFileName);
    try {
      ra.accumulate(new ValidationResult("8", EvaluationContext.CORE, DwcTerm.Occurrence.qualifiedName(),
        resultElements));
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }

    try {
      ra.close();
    } catch (ResultAccumulationException e) {
      e.printStackTrace();
      fail();
    }

    try {
      System.out.println(FileUtils.readFileToString(validationResultFile));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      fail();
    }

    try {
      File expectedCsv = new File(getClass().getResource("/accumulator/expectedCsv.txt").toURI());
      try {
        assertTrue(FileUtils.contentEqualsIgnoreEOL(expectedCsv, validationResultFile, Charsets.UTF_8.name()));
      } catch (IOException e) {
        e.printStackTrace();
        fail();
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }
    validationResultFile.delete();
  }
}
