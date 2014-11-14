package org.gbif.dwc.validator;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.evaluator.IntegrityEvaluators;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.TermsValidators;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;
import org.gbif.dwc.validator.rule.value.InvalidCharacterEvaluationRuleBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main builder to create evaluation chain.
 * 
 * @author cgendreau
 */
public class Evaluators {

  // decimalLatitude : Legal values lie between -90 and 90, inclusive.
  public static final double MIN_LATITUDE = -90d;
  public static final double MAX_LATITUDE = 90d;

  // decimalLongitude : Legal values lie between -180 and 180, inclusive.
  public static final double MIN_LONGITUDE = -180d;
  public static final double MAX_LONGITUDE = 180d;

  private final List<RecordEvaluatorBuilder> buildersList;

  /**
   * Get a new Evaluators instance
   * 
   * @return
   */
  public static Evaluators builder() {
    return new Evaluators();
  }

  /**
   * Current chain
   * -scientificName does NOT contains whitespace other than space
   * -decimalLatitude and decimalLongitude are number within valid bounds
   * -coreId is unique
   * -extension records points to a valid coreID
   * 
   * @param tempFolder
   * @return
   */
  public static Evaluators defaultChain(File tempFolder) {
    Evaluators val =
      builder()
        .with(TermsValidators.rule(InvalidCharacterEvaluationRuleBuilder.builder().build(), DwcTerm.scientificName))
        .with(TermsValidators.withinRange(DwcTerm.decimalLatitude, MIN_LATITUDE, MAX_LATITUDE))
        .with(TermsValidators.withinRange(DwcTerm.decimalLongitude, MIN_LONGITUDE, MAX_LONGITUDE))
        .with(IntegrityEvaluators.archiveIdIntegrity(tempFolder));
    return val;
  }

  /**
   * Build a validation chain from a list of RecordEvaluator.
   * 
   * @param evaluatorList
   * @return chain head
   */
  public static ChainableRecordEvaluator buildFromEvaluatorList(List<RecordEvaluator> evaluatorList) {
    ChainableRecordEvaluator current = null;
    // iterate from last element to the first to be able to provide the next ChainableRecordEvaluator
    for (int i = evaluatorList.size() - 1; i >= 0; i--) {
      current = new ChainableRecordEvaluator(evaluatorList.get(i), current);
    }
    return current;
  }

  /**
   * Private constructor, use Validator.builder()
   */
  private Evaluators() {
    this.buildersList = new ArrayList<RecordEvaluatorBuilder>();
  }

  /**
   * Append a validation to the validation chain.
   * 
   * @param recordEvaluatorBuilder
   * @return
   */
  public Evaluators with(RecordEvaluatorBuilder recordEvaluatorBuilder) {
    buildersList.add(recordEvaluatorBuilder);
    return this;
  }

  /**
   * Build the ArchiveValidator instance.
   * 
   * @return
   */
  public FileEvaluator build() throws IllegalStateException {
    return new DwcArchiveEvaluator(buildChain());
  }

  /**
   * Build the validation chain.
   * 
   * @return head of the chain
   */
  public ChainableRecordEvaluator buildChain() throws IllegalStateException {

    ChainableRecordEvaluator current = null;
    // iterate from last element to the first to be able to provide the next ChainableRecordEvaluator
    for (int i = buildersList.size() - 1; i >= 0; i--) {
      current = new ChainableRecordEvaluator(buildersList.get(i).build(), current);
    }
    return current;
  }

// Taxon only evaluators
// if (DwcTerm.Taxon.qualifiedName().equals(archiveFile.getRowType())) {
// // Check if acceptedNameUsageID is actually used in this file
// if (archiveFile.getField(DwcTerm.acceptedNameUsageID) != null) {
// ReferentialIntegrityEvaluator referentialIntegrityEvaluator =
// ReferentialIntegrityEvaluator.create(EvaluationContext.CORE, DwcTerm.acceptedNameUsageID)
// .referTo(EvaluationContext.CORE, DwcTerm.taxonID, uniquenessEvaluator.getSortedIdFile())
// .supportMultipleValues("|").build();
// chainBuilder = chainBuilder.linkTo(referentialIntegrityEvaluator);
// }
// }

}
