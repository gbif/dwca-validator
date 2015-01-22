package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;

import org.apache.commons.lang3.StringUtils;

/**
 * Transform the value associated with a Term into a presence Boolean.
 * 
 * @author cgendreau
 */
class PresenceValueTransformation implements ValueTransformation<Boolean> {

  private final Term term;

  PresenceValueTransformation(Term term) {
    this.term = term;
  }

  @Override
  public ValueTransformationResult<Boolean> transform(Record record) {
    String str = record.value(term);
    return ValueTransformationResult.transformed(term, str, StringUtils.isNotBlank(str));
  }

}
