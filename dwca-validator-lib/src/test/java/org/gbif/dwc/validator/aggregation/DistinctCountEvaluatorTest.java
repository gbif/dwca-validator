package org.gbif.dwc.validator.aggregation;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.mock.MockRecordFactory;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.accumulator.InMemoryResultAccumulator;
import org.gbif.dwca.record.Record;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test DistinctCountEvaluator implementation.
 *
 * @author cgendreau
 */
public class DistinctCountEvaluatorTest {

  private Record buildMockRecord(String id, String sciName) {
    return MockRecordFactory.buildMockOccurrenceRecord(DwcTerm.occurrenceID, id,
      new DwcTerm[] {DwcTerm.scientificName}, new String[] {sciName});
  }

  @Test
  public void testDistinctCountEvaluator() throws ResultAccumulationException {
    DistinctCountEvaluator dcEvaluator = new DistinctCountEvaluator(DwcTerm.scientificName);

    dcEvaluator.handleRecord(buildMockRecord("1", "Gulo gulo"), EvaluationContext.CORE);
    dcEvaluator.handleRecord(buildMockRecord("2", "Hippopotamus amphibius"), EvaluationContext.CORE);
    dcEvaluator.handleRecord(buildMockRecord("3", "Gulo gulo"), EvaluationContext.CORE);

    InMemoryResultAccumulator inMemoryResultAccumulator = new InMemoryResultAccumulator();
    dcEvaluator.postIterate(inMemoryResultAccumulator);

    assertEquals(2, inMemoryResultAccumulator.getAggregationResultCount());
  }

}
