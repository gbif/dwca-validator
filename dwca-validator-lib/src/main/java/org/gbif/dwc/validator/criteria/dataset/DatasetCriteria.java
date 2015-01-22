package org.gbif.dwc.validator.criteria.dataset;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulator;

import java.io.Closeable;

/**
 * A DatasetCriteria record data at the record level and generates result after iteration through all records.
 * 
 * @author cgendreau
 */
public interface DatasetCriteria extends Closeable {

  String getCriteriaKey();

  // should throw error when failed
  void onRecord(Record record, EvaluationContext evaluationContext);

  /**
   * This function use a ResultAccumulator to avoid returning a massive object in case all record failed.
   * 
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  void validateDataset(ResultAccumulator resultAccumulator) throws ResultAccumulationException;

}
