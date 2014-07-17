package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.validator.evaluator.impl.UniquenessEvaluator;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator.ValueEvaluatorBuilder;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRule;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRule;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of EvaluationChainProviderIF using the default builder.
 * 
 * @author cgendreau
 */
public class DefaultEvaluationChainProvider implements EvaluationChainProviderIF {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEvaluationChainProvider.class);

  /**
   * Build a default ValueEvaluator.
   * 
   * @return
   */
  private ValueEvaluator buildDefaultValueEvaluator() {

    ValueEvaluatorBuilder rulesBuilder = ValueEvaluatorBuilder.create();

    NumericalValueEvaluationRule numericalValueEvaluationRule = NumericalValueEvaluationRule.createRule().build();

    rulesBuilder.addRule(DwcTerm.scientificName, InvalidCharacterEvaluationRule.createRule().build());
    rulesBuilder.addRule(DwcTerm.decimalLatitude, numericalValueEvaluationRule);
    rulesBuilder.addRule(DwcTerm.decimalLongitude, numericalValueEvaluationRule);

    return rulesBuilder.build();
  }

  /**
   * TODO, set workingFolder, receive ArchiveFile to avoid creating unnecessary Evaluators.
   */
  @Override
  public ChainableRecordEvaluator getCoreChain(ArchiveFile archiveFile) {

    try {
      // Check uniqueness on 'coreId'
      UniquenessEvaluator uniquenessEvaluator = UniquenessEvaluator.create().build();

      ValueEvaluator valueEvaluator = buildDefaultValueEvaluator();

      // Taxon only evaluators
      if (DwcTerm.Taxon.qualifiedName().equals(archiveFile.getRowType())) {
        // for taxon only
        // if(archiveFile.getField(DwcTerm.acceptedNameUsageID) != null){
        // ReferentialIntegrityEvaluator referentialIntegrityEvaluator =
        // ReferentialIntegrityEvaluator.create(ValidationContext.CORE, DwcTerm.parentNameUsageID)
        // .referTo(ValidationContext.CORE, DwcTerm.taxonID, uniquenessEvaluator.getSortedIdFile()).build();
        // }
      }

      return DefaultChainableRecordEvaluatorBuilder.create(valueEvaluator).linkTo(uniquenessEvaluator).build();
    } catch (IllegalStateException e) {
      LOGGER.error("Can't create core chain", e);
    } catch (IOException ioEx) {
      LOGGER.error("Can't create core chain", ioEx);
    }
    return null;
  }

}
