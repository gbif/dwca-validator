package org.gbif.dwc.validator.evaluator.chain;

import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.evaluator.impl.UniquenessEvaluator;
import org.gbif.dwc.validator.evaluator.impl.ValueEvaluator;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.rule.EvaluationRuleIF;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRule;

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
    rulesPerTerm.put(DwcTerm.scientificName, ruleList);

    return rulesPerTerm;
  }

  @Override
  public ChainableRecordEvaluator getCoreChain() {
    try {
      return DefaultChainableRecordEvaluatorBuilder
        .create(new ValueEvaluator(buildDefaultValueEvaluatorRulesPerTermMap(), ValidationContext.CORE))
        .linkTo(new UniquenessEvaluator()).build();
    } catch (IOException ioEx) {
      LOGGER.error("Can't create core chain", ioEx);
    }
    return null;
  }

}
