package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.evaluator.ChainableRecordEvaluator;

/**
 * ChainableRecordEvaluator to check the uniqueness of specific fields.
 * This ChainableRecordEvaluator will only produce results on postIterate() call.
 * 
 * @author cgendreau
 */
public class UniquenessEvaluator extends ChainableRecordEvaluator {

  @Override
  protected void handleEval(Record record) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void postIterate() {
    // TODO Auto-generated method stub

  }

}
