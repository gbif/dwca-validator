package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;

import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

/**
 * Transform the value associated with a Term into a presence Boolean.
 * 
 * @author cgendreau
 */
class PresenceValueTransformation implements ValueTransformation<Boolean> {

  private final Term term;
  private final List<String> absenceSynonym;

  PresenceValueTransformation(Term term) {
    this(term, null);
  }

  /**
   * Create a PresenceValueTransformation with an absenceSynonym list.
   * 
   * @param term
   * @param absenceSynonym list of Strings that should be considered as an absence e.g. "null", "na"
   * @return
   */
  PresenceValueTransformation(Term term, List<String> absenceSynonym) {
    this.term = term;
    if (absenceSynonym != null) {
      this.absenceSynonym = Lists.newArrayList(absenceSynonym);
    } else {
      this.absenceSynonym = null;
    }
  }

  @Override
  public ValueTransformationResult<Boolean> transform(Record record) {
    String str = record.value(term);
    boolean isPresent = StringUtils.isNotBlank(str);

    // if the term contains a value and a synonym list is defined
    if (isPresent && absenceSynonym != null) {
      isPresent = !absenceSynonym.contains(str);
    }

    return ValueTransformationResult.transformed(term, str, isPresent);
  }

}
