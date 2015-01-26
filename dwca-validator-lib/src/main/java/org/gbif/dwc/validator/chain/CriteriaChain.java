package org.gbif.dwc.validator.chain;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.criteria.RecordCriterionIF;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriteria;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class to allow RecordEvaluatorIF to be chainable.
 * The class is immutable but the RecordEvaluator immutability can not be enforced.
 * 
 * @author cgendreau
 */
public class CriteriaChain {

  private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaChain.class);

  private final List<RecordCriterionIF> recordCriteriaList;
  private final List<DatasetCriteria> datasetCriteria;

  public CriteriaChain(List<RecordCriterionIF> recordCriteriaList, List<DatasetCriteria> datasetCriteria) {
    this.recordCriteriaList = ImmutableList.copyOf(recordCriteriaList);
    this.datasetCriteria = ImmutableList.copyOf(datasetCriteria);
  }

  public void evaluateRecord(Record record, EvaluationContext evaluationContext, ResultAccumulator resultAccumulator)
    throws ResultAccumulationException {

    Optional<ValidationResult> result;
    for (RecordCriterionIF currRecordCriteria : recordCriteriaList) {
      result = currRecordCriteria.validate(record, evaluationContext);
      if (result.isPresent()) {
        resultAccumulator.accumulate(result.get());
      }
    }

    for (DatasetCriteria currRecordCriteria : datasetCriteria) {
      currRecordCriteria.onRecord(record, evaluationContext);
    }
  }

  public void evaluateDataset(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    for (DatasetCriteria currRecordCriteria : datasetCriteria) {
      currRecordCriteria.validateDataset(resultAccumulator);
    }
  }

  public void cleanup() throws IOException {
    for (DatasetCriteria currRecordCriteria : datasetCriteria) {
      currRecordCriteria.close();
    }
  }
}
