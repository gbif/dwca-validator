package org.gbif.dwc.validator.rule.value;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.type.UndefinedValidationType;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;

import org.apache.commons.lang3.StringUtils;
import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Year;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAccessor;

/**
 * Rule used to ensure a String represents a date according to the ISO 8601 standard.
 * It is possible to configure the rule to accept slight differences in notation.
 * 
 * @author cgendreau
 */
public class ISODateValueEvaluationRule implements EvaluationRuleIF<String> {

  /**
   * Container object holding ISODateValueEvaluationRule configurations.
   * 
   * @author cgendreau
   */
  public static class Configuration {

    private boolean allowPartialDate = false;
    private boolean allowMissingLeadingZeros = false;

    public boolean isAllowMissingLeadingZeros() {
      return allowMissingLeadingZeros;
    }

    public boolean isAllowPartialDate() {
      return allowPartialDate;
    }

    public void setAllowMissingLeadingZeros(boolean allowMissingLeadingZeros) {
      this.allowMissingLeadingZeros = allowMissingLeadingZeros;
    }

    public void setAllowPartialDate(boolean allowPartialDate) {
      this.allowPartialDate = allowPartialDate;
    }
  }

  /**
   * Builder used to customized, if needed, the ISODateValueEvaluationRule.
   * 
   * @author cgendreau
   */
  public static class ISODateValueEvaluationRuleBuilder {

    private final Configuration configuration = new Configuration();

    /**
     * Creates a default ISODateValueEvaluationRuleBuilder.
     * 
     * @return
     */
    public static ISODateValueEvaluationRuleBuilder create() {
      return new ISODateValueEvaluationRuleBuilder();
    }

    /**
     * Allow ISO dates with no leading zeros(e.g. 2014-9-4).
     * 
     * @return
     */
    public ISODateValueEvaluationRuleBuilder allowMissingLeadingZeros() {
      configuration.setAllowMissingLeadingZeros(true);
      return this;
    }

    /**
     * Allow partial ISO dates (e.g. 2014 or 2014-08).
     * 
     * @return
     */
    public ISODateValueEvaluationRuleBuilder allowPartialDate() {
      configuration.setAllowPartialDate(true);
      return this;
    }

    /**
     * Build an immutable ISODateValueEvaluationRule instance
     * 
     * @return immutable ISODateValueEvaluationRule
     */
    public ISODateValueEvaluationRule build() {
      return new ISODateValueEvaluationRule(configuration);
    }

  }

  private static final DateTimeFormatter ISO8601_BASIC_ISO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

  private static final DateTimeFormatter ISO8601_ISO_DATE = DateTimeFormatter.ISO_DATE;
  private static final DateTimeFormatter ISO8601_ISO_DATE_ALLOW_NO_LZ = DateTimeFormatter.ofPattern("yyyy-M-d");

  // version that allows non leading zeros
  private static final DateTimeFormatter ISO8601_PARTIAL_DATE_ALLOW_NO_LZ = DateTimeFormatter.ofPattern("yyyy[-M[-d]]");

  private static final DateTimeFormatter ISO8601_PARTIAL_DATE = DateTimeFormatter.ofPattern("yyyy[-MM[-dd]]");

  private final boolean allowPartialDate;
  private final DateTimeFormatter activeCompleteDateFormatter;
  private final DateTimeFormatter activePartialDateFormatter;

  public ISODateValueEvaluationRule(Configuration configuration) {
    this.allowPartialDate = configuration.allowPartialDate;

    if (configuration.allowMissingLeadingZeros) {
      activeCompleteDateFormatter = ISO8601_ISO_DATE_ALLOW_NO_LZ;
      activePartialDateFormatter = ISO8601_PARTIAL_DATE_ALLOW_NO_LZ;
    } else {
      activeCompleteDateFormatter = ISO8601_ISO_DATE;
      activePartialDateFormatter = ISO8601_PARTIAL_DATE;
    }
  }

  private ValidationResultElement createNonISOValidationResultElement(String value) {
    return new ValidationResultElement(ContentValidationType.RECORD_CONTENT_VALUE, Result.WARNING,
      ArchiveValidatorConfig.getLocalizedString("rule.date.non_ISO", value));
  }

  private ValidationResultElement createSuccessValidationResultElement(TemporalAccessor ta) {
    return new ValidationResultElement(UndefinedValidationType.UNDEFINED, Result.PASSED, "", ta);
  }

  @Override
  public ValidationResultElement evaluate(String str) {

    if (StringUtils.isBlank(str)) {
      return ValidationResultElement.SKIPPED;
    }

    // if we can parse it as complete ISO date, it's fine
    TemporalAccessor ta = tryParse(str, ISO8601_BASIC_ISO_DATE, activeCompleteDateFormatter);
    if (ta != null) {
      return createSuccessValidationResultElement(ta);
    }

    if (allowPartialDate) {
      try {
        ta = activePartialDateFormatter.parseBest(str, LocalDate.FROM, YearMonth.FROM, Year.FROM);
      } catch (DateTimeException dtEx) {
        return createNonISOValidationResultElement(str);
      }
    } else {
      return createNonISOValidationResultElement(str);
    }

    return createSuccessValidationResultElement(ta);
  }

  /**
   * Can we parse the provided String with at least on DateTimeFormatter.
   * As soon as one DateTimeFormatter can parse the String, the method returns.
   * 
   * @param str
   * @param dtFormatters
   * @return null if can't parse str with provided DateTimeFormatter
   */
  private TemporalAccessor tryParse(String str, DateTimeFormatter... dtFormatters) {
    TemporalAccessor ta = null;
    for (DateTimeFormatter dtf : dtFormatters) {
      try {
        ta = dtf.parse(str);
        return ta;
      } catch (DateTimeException dtEx) {
      }
    }
    return null;
  }
}
