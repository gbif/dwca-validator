package org.gbif.dwc.validator.aggregation;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.accumulator.InMemoryResultAccumulator;
import org.gbif.dwca.record.Record;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Test RecordCountEvaluator implementation.
 *
 * @author cgendreau
 */
public class RecordCountEvaluatorTest {

  private Record buildMockOccurrenceRecord(String id, String sciName) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, id,
      new DwcTerm[] {DwcTerm.scientificName}, new String[] {sciName});
  }

  private Record buildMockVernacularNameRecord(String id, String vernacularName) {
    return MockRecordFactory.buildMockRecord(GbifTerm.VernacularName, DwcTerm.occurrenceID, id,
      new DwcTerm[] {DwcTerm.vernacularName}, new String[] {vernacularName});
  }

  @Test
  public void testRecordCountEvaluator() throws ResultAccumulationException {
    RecordCountEvaluator dcEvaluator = new RecordCountEvaluator();

    dcEvaluator.handleRecord(buildMockOccurrenceRecord("1", "Gulo gulo"), EvaluationContext.CORE);
    dcEvaluator.handleRecord(buildMockOccurrenceRecord("2", "Hippopotamus amphibius"), EvaluationContext.CORE);

    // test extension with the same rowType than the core
    dcEvaluator.handleRecord(buildMockOccurrenceRecord("2", "Hippopotamus amphibius"), EvaluationContext.EXT);

    // test with a vernacular name extension
    dcEvaluator.handleRecord(buildMockVernacularNameRecord("2", "Hippopotame"), EvaluationContext.EXT);

    InMemoryResultAccumulator inMemoryResultAccumulator = new InMemoryResultAccumulator();
    dcEvaluator.postIterate(inMemoryResultAccumulator);

    // 3 == core + 1 extension of Occurrence + 1 extension of VernaculName
    assertEquals(3, inMemoryResultAccumulator.getAggregationResultCount());
  }

}
