package org.gbif.dwc.validator.chain;

import org.gbif.dwc.validator.RecordEvaluator;
import org.gbif.dwc.validator.criteria.ValidationCriterion;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriterion;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwca.record.Record;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to manage the evaluation chain for records.
 *
 * @author cgendreau
 */
public class EvaluatorChain {

  private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorChain.class);

  private final List<RecordEvaluator<? extends EvaluationResult>> evaluatorList = Lists.newArrayList();
  // we need to keep a reference on all DatasetCriterion to release resources
  private final List<DatasetCriterion> datasetCriteria;

  public EvaluatorChain(List<ValidationCriterion> recordCriteriaList, List<DatasetCriterion> datasetCriteria) {
    this.evaluatorList.addAll(ImmutableList.copyOf(recordCriteriaList));
    this.datasetCriteria = ImmutableList.copyOf(datasetCriteria);
    this.evaluatorList.addAll(datasetCriteria);
  }

  /**
   * Evaluate one record within a context.
   *
   * @param record
   * @param evaluationContext
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  public void evaluateRecord(Record record, EvaluationContext evaluationContext, ResultAccumulator resultAccumulator)
    throws ResultAccumulationException {

    Optional<? extends EvaluationResult> result;
    for (RecordEvaluator<? extends EvaluationResult> currRecordCriteria : evaluatorList) {
      result = currRecordCriteria.handleRecord(record, evaluationContext);
      if (result.isPresent()) {
        result.get().accept(resultAccumulator);
      }
    }
  }

  /**
   * Indicates that the iteration on all records is completed.
   *
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  public void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    for (RecordEvaluator<?> currRecordCriteria : evaluatorList) {
      currRecordCriteria.postIterate(resultAccumulator);
    }
  }

  public void cleanup() throws IOException {
    for (DatasetCriterion currRecordCriteria : datasetCriteria) {
      currRecordCriteria.close();
    }
  }
}
