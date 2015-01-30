package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;

import org.apache.commons.lang3.StringUtils;

/**
 * Transform the value associated with a Term into a Double.
 * 
 * @author cgendreau
 * @param <T>
 */
class NumericValueTransformation implements ValueTransformation<Number> {

  private final Term term;

  NumericValueTransformation(Term term) {
    this.term = term;
  }

  @Override
  public ValueTransformationResult<Number> transform(Record record) {
    String str = record.value(term);
    if (StringUtils.isBlank(str)) {
      return ValueTransformationResult.skipped(term, str);
    }

    Double value = null;
    try {
      value = Double.parseDouble(str);
    } catch (NumberFormatException nfEx) {
      return ValueTransformationResult.notTransformed(term, str,
        ValidatorConfig.getLocalizedString("transformation.numeric.non_numerical", str, term));
    }
    return ValueTransformationResult.transformed(term, str, (Number) value);
  }

}
