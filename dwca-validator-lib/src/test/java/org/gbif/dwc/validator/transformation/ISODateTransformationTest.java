package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.mock.MockRecordFactory;

import org.junit.Test;
import org.threeten.bp.temporal.TemporalAccessor;

import static org.junit.Assert.assertTrue;

/**
 * Test ISODateTransformation behavior.
 * 
 * @author cgendreau
 */
public class ISODateTransformationTest {

  private Record buildMockRecord(String occID, String eventDate) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {DwcTerm.eventDate},
      new String[] {eventDate});
  }


  /**
   * Validate the allowMissingLeadingZeros option alone
   */
  @Test
  public void testAllowMissingLeadingZeros() {
    ValueTransformation<TemporalAccessor> transformation =
      ValueTransformations.toISODate(DwcTerm.eventDate, false, true);

    ValueTransformationResult<TemporalAccessor> result = transformation.transform(buildMockRecord("1", "2014-8-7"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "2014-08-7"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "2014-8-07"));
    assertTrue(result.isTransformed());

    // But we don't accept partial dates
    result = transformation.transform(buildMockRecord("1", "2014-8"));
    assertTrue(result.isNotTransformed());

    testAlwaysValidString(transformation);
    testNeverValidString(transformation);
  }

  /**
   * Validate the allowPartialDate option alone
   */
  @Test
  public void testAllowPartialDate() {
    ValueTransformation<TemporalAccessor> transformation =
      ValueTransformations.toISODate(DwcTerm.eventDate, true, false);

    ValueTransformationResult<TemporalAccessor> result = transformation.transform(buildMockRecord("1", "2014"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "2014-08"));
    assertTrue(result.isTransformed());

    // But we don't accept missing leading zero
    result = transformation.transform(buildMockRecord("1", "2014-8"));
    assertTrue(result.isNotTransformed());

    testAlwaysValidString(transformation);
    testNeverValidString(transformation);
  }

  /**
   * Validate both allowPartialDate and allowPartialDate options
   */
  @Test
  public void testAllowPartialDateAndMissingLeadingZeros() {
    ValueTransformation<TemporalAccessor> transformation =
      ValueTransformations.toISODate(DwcTerm.eventDate, true, true);

    ValueTransformationResult<TemporalAccessor> result = transformation.transform(buildMockRecord("1", "2014"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "2014-08"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "2014-8"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "2014-8-7"));
    assertTrue(result.isTransformed());

    testAlwaysValidString(transformation);
    testNeverValidString(transformation);
  }

  /**
   * Dates that must always be evaluated as valid.
   * 
   * @param rule
   */
  private void testAlwaysValidString(ValueTransformation<TemporalAccessor> transformation) {

    // should be valid
    ValueTransformationResult<TemporalAccessor> result = transformation.transform(buildMockRecord("1", "20141010"));
    assertTrue(result.isTransformed());

    result = transformation.transform(buildMockRecord("1", "2014-10-10"));
    assertTrue(result.isTransformed());

    // empty string is ignored
    result = transformation.transform(buildMockRecord("1", ""));
    assertTrue(result.isSkipped());
  }

  /**
   * Dates that must never be evaluated as valid.
   * 
   * @param rule
   */
  private void testNeverValidString(ValueTransformation<TemporalAccessor> transformation) {

    ValueTransformationResult<TemporalAccessor> result = transformation.transform(buildMockRecord("1", "201411"));
    assertTrue(result.isNotTransformed());

    result = transformation.transform(buildMockRecord("1", "21-10-2014"));
    assertTrue(result.isNotTransformed());

    // non existing month
    result = transformation.transform(buildMockRecord("1", "2014-13-08"));
    assertTrue(result.isNotTransformed());

    // non existing date
    result = transformation.transform(buildMockRecord("1", "2014-02-30"));
    assertTrue(result.isNotTransformed());
  }

}
