package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriteriaKey;
import org.gbif.dwc.validator.criteria.configuration.InvalidCharacterCriteriaConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

@RecordCriteriaKey(key = "invalidCharacterCriteria")
class InvalidCharacterCriteria implements RecordCriteria {

  private final String key = InvalidCharacterCriteria.class.getAnnotation(RecordCriteriaKey.class).key();

  private final String rowTypeRestriction;
  private final Result level;
  private final Term term;

  private final CharMatcher charMatcher;

  InvalidCharacterCriteria(InvalidCharacterCriteriaConfiguration configuration) {
    this.level = configuration.getLevel();
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.term = configuration.getTerm();

    this.charMatcher = configuration.getCharMatcher();
  }

  @Override
  public Optional<ValidationResult> validate(Record record, EvaluationContext evaluationContext) {

    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(record.rowType())) {
      return Optional.absent();
    }

    String str = record.value(term);

    // skip if the value is null
    if (str == null) {
      return Optional.absent();
    }

    List<ValidationResultElement> elementList = new ArrayList<ValidationResultElement>();
    int indexIn = charMatcher.indexIn(str);
    if (indexIn > 0) {
      // TODO when moving to Java 7 use Character.getName(int codePoint)
      int charValue = str.charAt(indexIn);
      // Remove invalid character from the error message to avoid display issues (e.g. NULL char)
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("rule.invalid_character", charMatcher.removeFrom(str), indexIn, charValue)));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType(), elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType()));
  }

  @Override
  public String getCriteriaKey() {
    return key;
  }
}
