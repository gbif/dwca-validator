package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.terms.Term;

import java.util.List;

import org.threeten.bp.temporal.TemporalAccessor;

/**
 * Returns ValueTransformation instances.
 * 
 * @author cgendreau
 */
public class ValueTransformations {

  public static ValueTransformation<Number> toNumeric(Term term) {
    return new NumericValueTransformation(term);
  }

  public static ValueTransformation<Boolean> toPresence(Term term) {
    return new PresenceValueTransformation(term);
  }

  /**
   * @param term
   * @param absenceSynonym list of Strings that should be considered as an absence e.g. "null", "na"
   * @return
   */
  public static ValueTransformation<Boolean> toPresence(Term term, List<String> absenceSynonym) {
    return new PresenceValueTransformation(term, absenceSynonym);
  }


  public static ValueTransformation<TemporalAccessor> toISODate(Term term, boolean allowPartialDate,
    boolean allowMissingLeadingZeros) {
    return new ISODateTransformation(term, allowPartialDate, allowMissingLeadingZeros);
  }

}
