package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.RegexCriterionConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwca.record.Record;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

/**
 * Simple regex based criterion.
 * Check if the provided String can be matched against a specific Regular Expression.
 * Criterion is using Matcher(str).matches() meaning that it will 'Attempt to match the entire region against the
 * pattern.'
 *
 * @author cgendreau
 */
@RecordCriterionKey(key = "regexCriterion")
class RegexCriterion extends RecordCriterion {

  private final String key = RegexCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final Term rowTypeRestriction;
  private final Result level;

  private final Term term;
  private final Pattern pattern;
  private final String explanation;

  public RegexCriterion(RegexCriterionConfiguration configuration) {
    rowTypeRestriction = configuration.getRowTypeRestriction();
    level = configuration.getLevel();
    term = configuration.getTerm();
    pattern = Pattern.compile(configuration.getRegex());
    explanation = configuration.getExplanation();
  }

  @Override
  public Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext) {
    // if we specified a rowType restriction, check that the record is also of this rowType
    if (rowTypeRestriction != null && !rowTypeRestriction.equals(record.rowType())) {
      return Optional.absent();
    }

    String str = record.value(term);
    // skip if the value is null
    if (str == null) {
      return Optional.absent();
    }

    List<ValidationResultElement> elementList = null;
    if (!pattern.matcher(str).matches()) {
      elementList = Lists.newArrayList();
      String completeExplanation = ValidatorConfig.getLocalizedString("criterion.regex_criterion.value", str, term);
      completeExplanation +=
        StringUtils.defaultIfBlank(this.explanation,
          ValidatorConfig.getLocalizedString("criterion.regex_criterion.no_match"));
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        completeExplanation));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName(),
        elementList));
    }
    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName()));
  }

  @Override
  public String getCriterionKey() {
    return key;
  }

}
