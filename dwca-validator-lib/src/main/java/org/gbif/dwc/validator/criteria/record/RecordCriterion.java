package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.validator.criteria.ValidationCriterion;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulator;


/**
 * Criterion interface for Record level.
 * 
 * @author cgendreau
 */
public abstract class RecordCriterion implements ValidationCriterion {

  /**
   * RecordCriterion should not use postIterate to record results.
   */
  @Override
  public void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    // no op
  }

}
