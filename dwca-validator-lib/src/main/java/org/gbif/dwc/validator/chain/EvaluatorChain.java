package org.gbif.dwc.validator.chain;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriterion;
import org.gbif.dwc.validator.criteria.record.RecordCriterion;
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
 * This class is responsible to manage the evaluation chain.
 * The class is immutable but the RecordEvaluator immutability can not be enforced.
 * 
 * @author cgendreau
 */
public class EvaluatorChain {

  private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorChain.class);

  private final List<RecordCriterion> recordCriteriaList;
  private final List<DatasetCriterion> datasetCriteria;

  public EvaluatorChain(List<RecordCriterion> recordCriteriaList, List<DatasetCriterion> datasetCriteria) {
    this.recordCriteriaList = ImmutableList.copyOf(recordCriteriaList);
    this.datasetCriteria = ImmutableList.copyOf(datasetCriteria);
  }

  public void evaluateRecord(Record record, EvaluationContext evaluationContext, ResultAccumulator resultAccumulator)
    throws ResultAccumulationException {

    Optional<ValidationResult> result;
    for (RecordCriterion currRecordCriteria : recordCriteriaList) {
      result = currRecordCriteria.handleRecord(record, evaluationContext);
      if (result.isPresent()) {
        resultAccumulator.accumulate(result.get());
      }
    }

    for (DatasetCriterion currRecordCriteria : datasetCriteria) {
      currRecordCriteria.onRecord(record, evaluationContext);
    }
  }

  public void evaluateDataset(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    for (DatasetCriterion currRecordCriteria : datasetCriteria) {
      currRecordCriteria.postIterate(resultAccumulator);
    }
  }

  public void cleanup() throws IOException {
    for (DatasetCriterion currRecordCriteria : datasetCriteria) {
      currRecordCriteria.close();
    }
  }
}
