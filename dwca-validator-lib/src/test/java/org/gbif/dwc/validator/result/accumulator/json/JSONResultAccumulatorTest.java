package org.gbif.dwc.validator.result.accumulator.json;

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
 * This test ensures we generate the json result file as expected.
 * 
 * @author cgendreau
 */
public class JSONResultAccumulatorTest {

  private static final String key = "mockCriteria";

  @Test
  public void testCSVResultAccumulator() {
    String validationResultFileName = "test_JSONResultAccumulator_Results.json";
    File validationResultFile = new File(validationResultFileName);

    List<ValidationResultElement> resultElements = new ArrayList<ValidationResultElement>();

    ValidationResultElement vre1 =
      new ValidationResultElement(key, ContentValidationType.FIELD_UNIQUENESS, Result.ERROR, "not unique");
    ValidationResultElement vre2 =
      new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, Result.ERROR,
        "contains invalid characters");

    resultElements.add(vre1);
    resultElements.add(vre2);

    ResultAccumulator ra = new JSONResultAccumulator(validationResultFileName);
    try {
      ra.accumulate(new ValidationResult("8", EvaluationContext.CORE, DwcTerm.Occurrence.qualifiedName(),
        resultElements));

      // mock results to test the json array
      ra.accumulate(new ValidationResult("9", EvaluationContext.CORE, DwcTerm.Occurrence.qualifiedName(),
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

    // Compare with expected file
    try {
      File expectedCsv = new File(getClass().getResource("/accumulator/expectedJson.json").toURI());
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
