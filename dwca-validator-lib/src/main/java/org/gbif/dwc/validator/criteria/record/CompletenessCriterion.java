package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.CompletenessCriterionConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwca.record.Record;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

/**
 * CompletenessCriterion allows to check if the value of a term is complete.
 * Complete means that the value is not blank (not null and not empty).
 * It is also possible to specify a list of strings that should be considered as an absence of value (e.g. "null", "na")
 *
 * @author cgendreau
 */
@RecordCriterionKey(key = "completenessCriterion")
class CompletenessCriterion extends RecordCriterion {

  private final String key = CompletenessCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final Term term;
  private final List<String> absenceSynonyms;

  private final Result level;
  private final Term rowTypeRestriction;

  CompletenessCriterion(CompletenessCriterionConfiguration completenessCriterionConfiguration) {
    this.term = completenessCriterionConfiguration.getTerm();
    this.rowTypeRestriction = completenessCriterionConfiguration.getRowTypeRestriction();
    this.level = completenessCriterionConfiguration.getLevel();

    if (completenessCriterionConfiguration.getAbsenceSynonyms() != null) {
      this.absenceSynonyms = Lists.newArrayList(completenessCriterionConfiguration.getAbsenceSynonyms());
    } else {
      this.absenceSynonyms = null;
    }
  }

  @Override
  public String getCriterionKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext) {
    // if we specified a rowType restriction, check that the record is also of this rowType
    if (rowTypeRestriction != null && !rowTypeRestriction.equals(record.rowType())) {
      return Optional.absent();
    }

    List<ValidationResultElement> elementList = null;

    String str = record.value(term);
    boolean isPresent = StringUtils.isNotBlank(str);
    if (isPresent && absenceSynonyms != null) {
      isPresent = !absenceSynonyms.contains(str);
    }

    if (!isPresent) {
      elementList = Lists.newArrayListWithCapacity(1);
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("criterion.completeness_criterion.incomplete", term)));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName(),
        elementList));
    }

    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName()));
  }

}
