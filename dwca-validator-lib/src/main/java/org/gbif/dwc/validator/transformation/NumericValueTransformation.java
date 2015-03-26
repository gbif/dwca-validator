package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

/**
 * Transform the value associated with a Term into a Double.
 *
 * @author cgendreau
 * @param <T>
 */
class NumericValueTransformation implements ValueTransformation<Number> {

  private final Term term;
  private final List<Term> termList;

  NumericValueTransformation(Term term) {
    this.term = term;
    // we only keep it in a list because getTerms() function
    this.termList = ImmutableList.of(term);
  }

  @Override
  public ValueTransformationResult<Number> transform(Record record) {
    String str = record.value(term);
    if (StringUtils.isBlank(str)) {
      return ValueTransformationResult.skipped(str);
    }

    Double value = null;
    try {
      value = Double.parseDouble(str);
    } catch (NumberFormatException nfEx) {
      return ValueTransformationResult.notTransformed(str,
        ValidatorConfig.getLocalizedString("transformation.numeric.non_numerical", str, term));
    }
    return ValueTransformationResult.transformed(str, (Number) value);
  }

  @Override
  public List<Term> getTerms() {
    return termList;
  }

}
