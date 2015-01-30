package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;

import org.apache.commons.lang3.StringUtils;
import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Year;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.ResolverStyle;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.TemporalAccessor;

/**
 * Transform the value associated with a Term into an ISO date.
 * Note that the provided 'raw' value must already be in ISO format and this class will simply transform
 * it into a TemporalAccessor object.
 * 
 * @author cgendreau
 */
public class ISODateTransformation implements ValueTransformation<TemporalAccessor> {

  private final Term term;

  private static final DateTimeFormatter ISO8601_BASIC_ISO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

  private static final DateTimeFormatter ISO8601_ISO_DATE = DateTimeFormatter.ISO_DATE;

  // ISO8601 Date with no leading zeros e.g. 2014-8-7
  private static final DateTimeFormatter ISO8601_ISO_DATE_ALLOW_NO_LZ = new DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4).appendLiteral("-").appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral("-")
    .appendValue(ChronoField.DAY_OF_MONTH).toFormatter().withResolverStyle(ResolverStyle.STRICT);

  // ISO8601 Partial Date with no leading zeros e.g. 2014-8
  public static final DateTimeFormatter ISO8601_PARTIAL_DATE_ALLOW_NO_LZ = new DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4).optionalStart().appendLiteral("-").appendValue(ChronoField.MONTH_OF_YEAR)
    .optionalStart().appendLiteral("-").appendValue(ChronoField.DAY_OF_MONTH, 2).optionalEnd().optionalEnd()
    .toFormatter().withResolverStyle(ResolverStyle.STRICT);

  // ISO8601 Partial Date e.g. 2014 or 2014-08
  public static final DateTimeFormatter ISO8601_PARTIAL_DATE = new DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4).optionalStart().appendLiteral("-").appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .optionalStart().appendLiteral("-").appendValue(ChronoField.DAY_OF_MONTH, 2).optionalEnd().optionalEnd()
    .toFormatter().withResolverStyle(ResolverStyle.STRICT);

  private final boolean allowPartialDate;
  private final DateTimeFormatter activeCompleteDateFormatter;
  private final DateTimeFormatter activePartialDateFormatter;

  ISODateTransformation(Term term, boolean allowPartialDate, boolean allowMissingLeadingZeros) {
    this.term = term;
    this.allowPartialDate = allowPartialDate;

    if (allowMissingLeadingZeros) {
      activeCompleteDateFormatter = ISO8601_ISO_DATE_ALLOW_NO_LZ;
      activePartialDateFormatter = ISO8601_PARTIAL_DATE_ALLOW_NO_LZ;
    } else {
      activeCompleteDateFormatter = ISO8601_ISO_DATE;
      activePartialDateFormatter = ISO8601_PARTIAL_DATE;
    }
  }

  private ValueTransformationResult<TemporalAccessor> createNonISOEvaluationRuleResult(String value) {
    return ValueTransformationResult.notTransformed(term, "",
      ValidatorConfig.getLocalizedString("transformation.date.non_ISO", value));
  }

  @Override
  public ValueTransformationResult<TemporalAccessor> transform(Record record) {

    String str = record.value(term);
    if (StringUtils.isBlank(str)) {
      return ValueTransformationResult.skipped(term, str);
    }

    // if we can parse it as complete ISO date, it's fine
    TemporalAccessor ta = tryParseLocalDate(str, ISO8601_BASIC_ISO_DATE, activeCompleteDateFormatter);
    if (ta != null) {
      return ValueTransformationResult.transformed(term, "", ta);
    }

    if (allowPartialDate) {
      try {
        ta = activePartialDateFormatter.parseBest(str, LocalDate.FROM, YearMonth.FROM, Year.FROM);
      } catch (DateTimeException dtEx) {
        return createNonISOEvaluationRuleResult(str);
      }
    } else {
      return createNonISOEvaluationRuleResult(str);
    }

    return ValueTransformationResult.transformed(term, "", ta);
  }

  /**
   * Can we parse the provided String in LocalDate with at least on DateTimeFormatter.
   * As soon as one DateTimeFormatter can parse the String, the method returns.
   * 
   * @param str
   * @param dtFormatters
   * @return null if can't parse str with provided DateTimeFormatter
   */
  private TemporalAccessor tryParseLocalDate(String str, DateTimeFormatter... dtFormatters) {
    TemporalAccessor ta = null;
    for (DateTimeFormatter dtf : dtFormatters) {
      try {
        ta = dtf.parse(str, LocalDate.FROM);
        return ta;
      } catch (DateTimeException dtEx) {
      }
    }
    return null;
  }

}
