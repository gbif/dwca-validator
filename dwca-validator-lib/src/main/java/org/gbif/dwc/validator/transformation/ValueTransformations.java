package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.terms.Term;

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

  public static ValueTransformation<TemporalAccessor> toISODate(Term term, boolean allowPartialDate,
    boolean allowMissingLeadingZeros) {
    return new ISODateTransformation(term, allowPartialDate, allowMissingLeadingZeros);
  }

}
