package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.mock.MockRecordFactory;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class PresenceValueTransformationTest {

  private Record buildMockRecord(String occID, String eventDate) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, occID, new Term[] {DwcTerm.eventDate},
      new String[] {eventDate});
  }


  /**
   * Validate the allowMissingLeadingZeros option alone
   */
  @Test
  public void testPresenceTransformation() {
    ValueTransformation<Boolean> transformation = ValueTransformations.toPresence(DwcTerm.eventDate);

    ValueTransformationResult<Boolean> result = transformation.transform(buildMockRecord("1", "2014-8-7"));
    assertTrue(result.isTransformed());
    assertTrue(result.getData());

    result = transformation.transform(buildMockRecord("1", ""));
    assertTrue(result.isTransformed());
    assertFalse(result.getData());
  }

}
