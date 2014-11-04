package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.validator.evaluator.ReferentialIntegrityEvaluator;
import org.gbif.dwc.validator.evaluator.UniquenessEvaluator;
import org.gbif.dwc.validator.evaluator.ValueEvaluator;
import org.gbif.dwc.validator.evaluator.ValueEvaluator.ValueEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.chain.builder.ChainableRecordEvaluatorBuilderIF;
import org.gbif.dwc.validator.evaluator.chain.builder.DefaultChainableRecordEvaluatorBuilder;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRule.InvalidCharacterEvaluationRuleBuilder;
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

  // decimalLatitude : Legal values lie between -90 and 90, inclusive.
  public static final double MIN_LATITUDE = -90d;
  public static final double MAX_LATITUDE = 90d;

  // decimalLongitude : Legal values lie between -180 and 180, inclusive.
  public static final double MIN_LONGITUDE = -180d;
  public static final double MAX_LONGITUDE = 180d;

  /**
   * Build a default ValueEvaluator.
   * 
   * @return
   */
  private ValueEvaluator buildDefaultValueEvaluator() {

    ValueEvaluatorBuilder rulesBuilder = ValueEvaluatorBuilder.create();

    NumericalValueEvaluationRule latNumericalValueEvaluationRule =
      NumericalValueEvaluationRule.createRule().boundedBy(MIN_LATITUDE, MAX_LATITUDE).build();
    NumericalValueEvaluationRule lngNumericalValueEvaluationRule =
      NumericalValueEvaluationRule.createRule().boundedBy(MIN_LONGITUDE, MAX_LONGITUDE).build();

    rulesBuilder.addRule(DwcTerm.scientificName, InvalidCharacterEvaluationRuleBuilder.create().build());
    rulesBuilder.addRule(DwcTerm.decimalLatitude, latNumericalValueEvaluationRule);
    rulesBuilder.addRule(DwcTerm.decimalLongitude, lngNumericalValueEvaluationRule);

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

      ChainableRecordEvaluatorBuilderIF chainBuilder =
        DefaultChainableRecordEvaluatorBuilder.create(valueEvaluator).linkTo(uniquenessEvaluator);

      // Taxon only evaluators
      if (DwcTerm.Taxon.qualifiedName().equals(archiveFile.getRowType())) {
        // Check if acceptedNameUsageID is actually used in this file
        if (archiveFile.getField(DwcTerm.acceptedNameUsageID) != null) {
          ReferentialIntegrityEvaluator referentialIntegrityEvaluator =
            ReferentialIntegrityEvaluator.create(EvaluationContext.CORE, DwcTerm.acceptedNameUsageID)
              .referTo(EvaluationContext.CORE, DwcTerm.taxonID, uniquenessEvaluator.getSortedIdFile())
              .supportMultipleValues("|").build();
          chainBuilder = chainBuilder.linkTo(referentialIntegrityEvaluator);
        }
      }

      return chainBuilder.build();
    } catch (IllegalStateException e) {
      LOGGER.error("Can't create core chain", e);
    } catch (IOException ioEx) {
      LOGGER.error("Can't create core chain", ioEx);
    }
    return null;
  }

}
