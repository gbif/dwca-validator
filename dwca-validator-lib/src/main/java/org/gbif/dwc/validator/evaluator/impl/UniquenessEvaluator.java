package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

/**
 * ChainableRecordEvaluator to check the uniqueness of specific fields.
 * This ChainableRecordEvaluator will only produce results on postIterate() call.
 * 
 * @author cgendreau
 */
public class UniquenessEvaluator implements RecordEvaluatorIF {

  public UniquenessEvaluator() {
  }

  @Override
  public void handleEval(Record record, ResultAccumulatorIF resultAccumulator) {
    // TODO Auto-generated method stub

  }

  @Override
  public void postIterate() {
    // TODO Auto-generated method stub

  }

}
