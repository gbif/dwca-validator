package org.gbif.dwc.validator.criteria.record;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.criteria.annotation.RecordCriterionKey;
import org.gbif.dwc.validator.criteria.configuration.ControlledVocabularyCriterionConfiguration;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;
import org.gbif.dwca.record.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;

/**
 * RecordCriterion used to ensure the value of a term is matching against a controlled vocabulary.
 * TODO: add ability to set 'preferred' and 'alternative' string
 * TODO: should we only compare lowerCase (at least by default)?
 * TODO: should we accept ascii folding setting? If yes, how it will (should) react with characters like 保存標本
 *
 * @author cgendreau
 */
@RecordCriterionKey(key = "controlledVocabularyCriterion")
class ControlledVocabularyCriterion extends RecordCriterion {

  private final String key = ControlledVocabularyCriterion.class.getAnnotation(RecordCriterionKey.class).key();

  private final Result level;
  private final Term rowTypeRestriction;

  private final Term term;
  private final Set<String> vocabularySet;

  ControlledVocabularyCriterion(ControlledVocabularyCriterionConfiguration configuration) {
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
  public String getCriterionKey() {
    return key;
  }

  @Override
  public Optional<ValidationResult> handleRecord(Record record, EvaluationContext evaluationContext) {
    // if we specified a rowType restriction, check that the record is also of this rowType
    if (rowTypeRestriction != null && !rowTypeRestriction.equals(record.rowType())) {
      return Optional.absent();
    }

    String str = record.value(term);
    if (str == null) {
      return Optional.absent();
    }

    List<ValidationResultElement> elementList = new ArrayList<ValidationResultElement>();
    if (!vocabularySet.contains(str)) {
      elementList.add(new ValidationResultElement(key, ContentValidationType.RECORD_CONTENT_VALUE, level,
        ValidatorConfig.getLocalizedString("criterion.controlled_vocabulary_criterion.controlled_vocabulary", str,
          term.simpleName())));
    }

    if (elementList != null && elementList.size() > 0) {
      return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName(),
        elementList));
    }
    return Optional.of(new ValidationResult(record.id(), evaluationContext, record.rowType().qualifiedName()));
  }

}
