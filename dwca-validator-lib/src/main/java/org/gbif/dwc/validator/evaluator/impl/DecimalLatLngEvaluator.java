package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluator;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.validation.ValidationResult;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Decimal latitude/longitude evaluator.
 * 
 * @author cgendreau
 */
@RecordEvaluator(key = "decimalLatLngEvaluator")
public class DecimalLatLngEvaluator implements RecordEvaluatorIF {

  public static class DecimalLatLngEvaluatorBuilder {

    private final String key = DecimalLatLngEvaluator.class.getAnnotation(RecordEvaluator.class).key();
    private NumericalValueEvaluationRule latNumericalValueEvaluationRule;
    private NumericalValueEvaluationRule lngNumericalValueEvaluationRule;

    private DecimalLatLngEvaluatorBuilder() {
      // build lat/long default NumericalValueEvaluationRule
      latNumericalValueEvaluationRule =
        NumericalValueEvaluationRule.createRule().boundedBy(MIN_LATITUDE, MAX_LATITUDE).build();
      lngNumericalValueEvaluationRule =
        NumericalValueEvaluationRule.createRule().boundedBy(MIN_LONGITUDE, MAX_LONGITUDE).build();
    }

    /**
     * Create with default value. Using coreId, ValidationContext.CORE
     * 
     * @return
     */
    public static DecimalLatLngEvaluatorBuilder create() {
      return new DecimalLatLngEvaluatorBuilder();
    }

    /**
     * Build DecimalLatLngEvaluator.
     * 
     * @return immutable DecimalLatLngEvaluator object
     */
    public DecimalLatLngEvaluator build() {
      return new DecimalLatLngEvaluator(key, latNumericalValueEvaluationRule, lngNumericalValueEvaluationRule);
    }

    /**
     * Change the default NumericalValueEvaluationRule used to evaluate the value of lat/long fields.
     * This could be used to restrict the accepted extent.
     * 
     * @param latNumericalValueEvaluationRule
     * @param lngNumericalValueEvaluationRule
     */
    public void using(NumericalValueEvaluationRule latNumericalValueEvaluationRule,
      NumericalValueEvaluationRule lngNumericalValueEvaluationRule) {
      this.latNumericalValueEvaluationRule = latNumericalValueEvaluationRule;
      this.lngNumericalValueEvaluationRule = lngNumericalValueEvaluationRule;
    }

  }

  private final String key;

  // decimalLatitude : Legal values lie between -90 and 90, inclusive.
  public static final double MIN_LATITUDE = -90d;
  public static final double MAX_LATITUDE = 90d;

  // decimalLongitude : Legal values lie between -180 and 180, inclusive.
  public static final double MIN_LONGITUDE = -180d;
  public static final double MAX_LONGITUDE = 180d;

  private final NumericalValueEvaluationRule latNumericalValueEvaluationRule;
  private final NumericalValueEvaluationRule lngNumericalValueEvaluationRule;

  public DecimalLatLngEvaluator(String key, NumericalValueEvaluationRule latNumericalValueEvaluationRule,
    NumericalValueEvaluationRule lngNumericalValueEvaluationRule) {
    this.key = key;
    this.latNumericalValueEvaluationRule = latNumericalValueEvaluationRule;
    this.lngNumericalValueEvaluationRule = lngNumericalValueEvaluationRule;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public void handleEval(Record record, ResultAccumulatorIF resultAccumulator) {
    String lat = record.value(DwcTerm.decimalLatitude);
    String lng = record.value(DwcTerm.decimalLongitude);
    String id = record.id();

    // if both are empty simply return, nothing to evaluate.
    if (StringUtils.isBlank(lat) && StringUtils.isBlank(lng)) {
      return;
    }

    // validate if it's a number and within the defined bounds
    ValidationResultElement latResultElement = latNumericalValueEvaluationRule.evaluate(lat);
    ValidationResultElement lngResultElement = lngNumericalValueEvaluationRule.evaluate(lng);

    if (latResultElement != null || lngResultElement != null) {
      List<ValidationResultElement> evaluationResultElementList = new ArrayList<ValidationResultElement>();
      if (latResultElement != null) {
        evaluationResultElementList.add(latResultElement);
      }
      if (lngResultElement != null) {
        evaluationResultElementList.add(lngResultElement);
      }

      // try to swap lat and lng and re-evaluate
      if (latNumericalValueEvaluationRule.evaluate(lng) == null
        && lngNumericalValueEvaluationRule.evaluate(lat) == null) {
        evaluationResultElementList.add(new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE,
          Result.WARNING, ArchiveValidatorConfig.getLocalizedString("evaluator.decimal_lat_lng.inverted", lat, lng)));
      }
      resultAccumulator.accumulate(new ValidationResult(id, key, EvaluationContext.CORE, evaluationResultElementList));
      // stop here since at least one NumericalValueEvaluation rule failed
      return;
    }

    Double dLat = Double.parseDouble(lat);
    Double dLng = Double.parseDouble(lng);

    if (dLat.doubleValue() == 0d || dLng.doubleValue() == 0d) {
      resultAccumulator.accumulate(new ValidationResult(id, key, EvaluationContext.CORE, new ValidationResultElement(
        ContentValidationType.RECORD_CONTENT_VALUE, Result.WARNING, ArchiveValidatorConfig.getLocalizedString(
          "evaluator.decimal_lat_lng.zero", lat, lng))));
    }

    // TODO validate precision, number of digits


  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    // no op
  }

}
