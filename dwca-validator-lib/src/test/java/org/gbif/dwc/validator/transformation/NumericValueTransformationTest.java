package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.mock.MockRecordFactory;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test NumericValueTransformation behavior.
 * 
 * @author cgendreau
 */
public class NumericValueTransformationTest {

  private Record buildMockRecord(String occID, String lat) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID,
      new Term[] {DwcTerm.decimalLatitude}, new String[] {lat});
  }

  @Test
  public void testTransformation() {

    ValueTransformation<Number> transformation = ValueTransformations.toNumeric(DwcTerm.decimalLatitude);

    // valid transformation
    ValueTransformationResult<Number> result = transformation.transform(buildMockRecord("1", "1"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "1.2"));
    assertTrue(result.isTransformed());
    assertTrue(result.getData().doubleValue() == 1.2d);

    result = transformation.transform(buildMockRecord("1", "-8.3"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", ""));
    assertTrue(result.isSkipped());

    // not valid transformation
    result = transformation.transform(buildMockRecord("1", "1.1.1"));
    assertTrue(result.isNotTransformed());

    result = transformation.transform(buildMockRecord("1", "0.-9"));
    assertTrue(result.isNotTransformed());

    result = transformation.transform(buildMockRecord("1", "w"));
    assertTrue(result.isNotTransformed());
  }

}
