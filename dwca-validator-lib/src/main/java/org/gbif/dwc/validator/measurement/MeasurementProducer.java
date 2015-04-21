package org.gbif.dwc.validator.measurement;

import org.gbif.dwc.validator.RecordEvaluator;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwca.record.Record;

import com.google.common.base.Optional;

/**
 * -- For future use --
 * Eventually, we could add MeasurementProducer to an Evaluation chain at the same level as the Criteria.
 * Criterion produces ValidationResult, MeasurementProducer produces MeasurementResult.
 * Measurement could be a Record(number of latitude digits) or Dataset(avg. of latitude digits) level
 * and could be wrapped inside a Criterion.
 *
 * @author cgendreau
 */
public class MeasurementProducer implements RecordEvaluator<MeasurementResult> {

  @Override
  public Optional<MeasurementResult> handleRecord(Record record, EvaluationContext evaluationContext) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void postIterate(ResultAccumulator resultAccumulator) throws ResultAccumulationException {
    // TODO Auto-generated method stub
  }

}
