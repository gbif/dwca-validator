package org.gbif.dwc.validator.aggregation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.RecordEvaluator;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.aggregation.AggregationResult;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.mutable.MutableInt;


/**
 * Aggregation example, work in progress.
 * NOT thread safe
 *
 * @author cgendreau
 */
public class DistinctCountEvaluator implements RecordEvaluator {

  // TODO replace by EvaluatorKey annotation
  private static final String KEY = "distinctCountEvaluator";

  private final int MAX_DISTINCT_VALUES = 1000;

  private final Term term;
  private final Map<String, MutableInt> distinctCount = Maps.newHashMap();

  /**
   * For testing purpose only, to be replaced by a Buidler
   *
   * @param term
   */
  public DistinctCountEvaluator(Term term) {
    this.term = term;
  }

  @Override
  public Optional<? extends EvaluationResult> handleRecord(Record record, EvaluationContext evaluationContext) {
    String value = record.value(term);
    if (!distinctCount.containsKey(value)) {
      distinctCount.put(value, new MutableInt());
    }
    distinctCount.get(value).increment();
    return Optional.absent();
  }

  @Override
  public void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    // TODO fixed context and probably add rowTypeRestriction
    for (String currKey : distinctCount.keySet()) {
      resultAccumulator.accumulate(new AggregationResult<Integer>(currKey, KEY, EvaluationContext.CORE, distinctCount
        .get(currKey).intValue()));
    }
  }

}
