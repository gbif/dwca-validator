package org.gbif.dwc.validator.aggregation;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.RecordEvaluator;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwca.record.Record;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Work in progress.
 * Counts records based on rowType generating one count for CORE and one per rowType for extensions.
 * NOT thread safe
 *
 * @author cgendreau
 */
public class RecordCountEvaluator implements RecordEvaluator<AggregationResult<Integer>> {

  // TODO replace by EvaluatorKey annotation
  private static final String KEY = "recordCountEvaluator";

  private Term coreTerm;
  private int coreRecordCount = 0;
  private final Map<Term, MutableInt> extensionRecordCount = Maps.newHashMap();

  @Override
  public Optional<AggregationResult<Integer>> handleRecord(Record record, EvaluationContext evaluationContext) {
    if (EvaluationContext.CORE == evaluationContext) {
      coreTerm = record.rowType();
      coreRecordCount++;
    } else {
      if (!extensionRecordCount.containsKey(record.rowType())) {
        extensionRecordCount.put(record.rowType(), new MutableInt());
      }
      extensionRecordCount.get(record.rowType()).increment();
    }
    return Optional.absent();
  }

  @Override
  public void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    resultAccumulator.accumulate(new AggregationResult<Integer>(coreTerm.simpleName(), KEY, EvaluationContext.CORE,
      coreRecordCount));
    for (Term currTerm : extensionRecordCount.keySet()) {
      resultAccumulator.accumulate(new AggregationResult<Integer>(currTerm.simpleName(), KEY, EvaluationContext.EXT,
        extensionRecordCount.get(currTerm).intValue()));
    }
  }

}
