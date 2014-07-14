package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.validator.evaluator.impl.UniquenessEvaluator;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRule;
import org.gbif.dwc.validator.rule.value.NumericalValueEvaluationRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   * Build a default Map<ConceptTerm, List<EvaluationRuleIF<String>>> for ValueEvaluator.
   * TODO Evaluate the possibility to move the creation of Evaluators in their own class.
   * 
   * @return
   */
  private Map<ConceptTerm, List<EvaluationRuleIF<String>>> buildDefaultValueEvaluatorRulesPerTermMap() {
    Map<ConceptTerm, List<EvaluationRuleIF<String>>> rulesPerTerm =
      new HashMap<ConceptTerm, List<EvaluationRuleIF<String>>>();

    // register an InvalidCharacterEvaluationRule for scientificName
    List<EvaluationRuleIF<String>> ruleList = new ArrayList<EvaluationRuleIF<String>>();
    ruleList.add(InvalidCharacterEvaluationRule.createRule().build());

    // lat,lng rule list
    List<EvaluationRuleIF<String>> latLngRuleList = new ArrayList<EvaluationRuleIF<String>>();
    latLngRuleList.add(NumericalValueEvaluationRule.createRule().build());

    rulesPerTerm.put(DwcTerm.scientificName, ruleList);
    rulesPerTerm.put(DwcTerm.decimalLatitude, latLngRuleList);
    rulesPerTerm.put(DwcTerm.decimalLongitude, latLngRuleList);

    return rulesPerTerm;
  }

  /**
   * TODO, set workingFolder, receive ArchiveFile to avoid creating unnecessary Evaluators.
   */
  @Override
  public ChainableRecordEvaluator getCoreChain(ArchiveFile archiveFile) {

    try {
      // Check uniqueness on 'coreId'
      UniquenessEvaluator uniquenessEvaluator = UniquenessEvaluator.create().build();

      ValueEvaluator valueEvaluator =
        new ValueEvaluator(buildDefaultValueEvaluatorRulesPerTermMap(), ValidationContext.CORE);

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
