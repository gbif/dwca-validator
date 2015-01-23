package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.ControlledVocabularyCriteriaConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * Rule use to ensure a String is matching against a controlled vocabulary.
 * ControlledVocabularyEvaluationRule objects are immutable.
 * TODO: add ability to set 'preferred' and 'alternative' string
 * TODO: should we only compare lowerCase (at least by default)?
 * TODO: should we accept ascii folding setting? If yes, how it will (should) react with characters like 保存標本
 * 
 * @author cgendreau
 */
@RecordCriterionKey(key = "controlledVocabularyCriteria")
class ControlledVocabularyCriteria implements RecordCriteria {

  private final String key = ControlledVocabularyCriteria.class.getAnnotation(RecordCriterionKey.class).key();

  private final Result level;
  private final String rowTypeRestriction;

  private final Term term;
  private final Set<String> vocabularySet;

  ControlledVocabularyCriteria(ControlledVocabularyCriteriaConfiguration configuration) {
    this.level = configuration.getLevel();
    this.rowTypeRestriction = configuration.getRowTypeRestriction();
    this.term = configuration.getTerm();

    if (configuration.getVocabularySet() != null) {
      this.vocabularySet = Collections.unmodifiableSet(configuration.getVocabularySet());
    } else {
      vocabularySet = null;
    }
  }

  @Override
  public Optional<ValidationResult> validate(Record record, EvaluationContext evaluationContext) {
    // if we specified a rowType restriction, check that the record is also of this rowType
    if (StringUtils.isNotBlank(rowTypeRestriction) && !rowTypeRestriction.equalsIgnoreCase(record.rowType())) {
      return Optional.absent();
    }

    String str = record.value(term);
    if (str == null) {
      return Optional.absent();
    }

    List<ValidationResultElement> elementList = new ArrayList<ValidationResultElement>();
    if (!vocabularySet.contains(str)) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("rule.controlled_vocabulary", str, term.simpleName())));
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
