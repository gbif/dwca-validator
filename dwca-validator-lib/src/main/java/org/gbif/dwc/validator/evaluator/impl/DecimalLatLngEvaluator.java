package org.gbif.dwc.validator.evaluator.impl;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorKey;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRule;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO generalize the class to InRangeEvaluator
 * Decimal latitude/longitude evaluator.
 * 
 * @author cgendreau
 */
@RecordEvaluatorKey(key = "decimalLatLngEvaluator")
public class DecimalLatLngEvaluator implements RecordEvaluator {

  /**
   * Container object holding DecimalLatLngEvaluator configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private NumericalValueEvaluationRule latNumericalValueEvaluationRule;
    private NumericalValueEvaluationRule lngNumericalValueEvaluationRule;

    public Configuration() {
      // set default lat/long NumericalValueEvaluationRule
      latNumericalValueEvaluationRule =
        NumericalValueEvaluationRule.createRule().boundedBy(MIN_LATITUDE, MAX_LATITUDE).build();
      lngNumericalValueEvaluationRule =
        NumericalValueEvaluationRule.createRule().boundedBy(MIN_LONGITUDE, MAX_LONGITUDE).build();
    }

    public NumericalValueEvaluationRule getLatNumericalValueEvaluationRule() {
      return latNumericalValueEvaluationRule;
    }

    public NumericalValueEvaluationRule getLngNumericalValueEvaluationRule() {
      return lngNumericalValueEvaluationRule;
    }

    public void setLatNumericalValueEvaluationRule(NumericalValueEvaluationRule latNumericalValueEvaluationRule) {
      this.latNumericalValueEvaluationRule = latNumericalValueEvaluationRule;
    }

    public void setLngNumericalValueEvaluationRule(NumericalValueEvaluationRule lngNumericalValueEvaluationRule) {
      this.lngNumericalValueEvaluationRule = lngNumericalValueEvaluationRule;
    }
  }

  public static class DecimalLatLngEvaluatorBuilder {

    private final Configuration configuration;

    private DecimalLatLngEvaluatorBuilder() {
      configuration = new Configuration();
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
      return new DecimalLatLngEvaluator(configuration);
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
      configuration.setLatNumericalValueEvaluationRule(latNumericalValueEvaluationRule);
      configuration.setLngNumericalValueEvaluationRule(lngNumericalValueEvaluationRule);
    }
  }

  private final String key = DecimalLatLngEvaluator.class.getAnnotation(RecordEvaluatorKey.class).key();

  // decimalLatitude : Legal values lie between -90 and 90, inclusive.
  public static final double MIN_LATITUDE = -90d;
  public static final double MAX_LATITUDE = 90d;

  // decimalLongitude : Legal values lie between -180 and 180, inclusive.
  public static final double MIN_LONGITUDE = -180d;
  public static final double MAX_LONGITUDE = 180d;

  private final NumericalValueEvaluationRule latNumericalValueEvaluationRule;
  private final NumericalValueEvaluationRule lngNumericalValueEvaluationRule;

  public DecimalLatLngEvaluator(Configuration configuration) {
    this.latNumericalValueEvaluationRule = configuration.getLatNumericalValueEvaluationRule();
    this.lngNumericalValueEvaluationRule = configuration.getLngNumericalValueEvaluationRule();
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleEval(Record record) {
    String lat = record.value(DwcTerm.decimalLatitude);
    String lng = record.value(DwcTerm.decimalLongitude);
    String id = record.id();

    // if both are empty simply return, nothing to evaluate.
    if (StringUtils.isBlank(lat) && StringUtils.isBlank(lng)) {
      return Optional.absent();
    }

    // validate if it's a number and within the defined bounds
    ValidationResultElement latResultElement = latNumericalValueEvaluationRule.evaluate(lat);
    ValidationResultElement lngResultElement = lngNumericalValueEvaluationRule.evaluate(lng);

    if (latResultElement.resultIsNot(Result.PASSED) || lngResultElement.resultIsNot(Result.PASSED)) {
      List<ValidationResultElement> evaluationResultElementList = new ArrayList<ValidationResultElement>();
      if (latResultElement.resultIsNot(Result.PASSED)) {
        evaluationResultElementList.add(latResultElement);
      }
      if (lngResultElement.resultIsNot(Result.PASSED)) {
        evaluationResultElementList.add(lngResultElement);
      }

      // this is not a validation but a suggestion, should be moved
      // try to swap lat and lng and re-evaluate
      // if (latNumericalValueEvaluationRule.evaluate(lng).resultIs(Result.PASSED)
      // && lngNumericalValueEvaluationRule.evaluate(lat).resultIs(Result.PASSED)) {
      // evaluationResultElementList.add(new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE,
      // Result.WARNING, ArchiveValidatorConfig.getLocalizedString("evaluator.decimal_lat_lng.inverted", lat, lng)));
      // }
      // stop here since at least one NumericalValueEvaluation rule failed
      return Optional.of(new ValidationResult(id, key, EvaluationContext.CORE, evaluationResultElementList));
    }

    Double dLat = Double.parseDouble(lat);
    Double dLng = Double.parseDouble(lng);

    if (dLat.doubleValue() == 0d || dLng.doubleValue() == 0d) {
      return Optional.of(new ValidationResult(id, key, EvaluationContext.CORE, new ValidationResultElement(
        ContentValidationType.RECORD_CONTENT_VALUE, Result.WARNING, ArchiveValidatorConfig.getLocalizedString(
          "evaluator.decimal_lat_lng.zero", lat, lng))));
    }

    // TODO validate precision, number of digits

    return Optional.absent();
  }

  @Override
  public void handlePostIterate(ResultAccumulatorIF resultAccumulator) {
    // no op
  }

}
