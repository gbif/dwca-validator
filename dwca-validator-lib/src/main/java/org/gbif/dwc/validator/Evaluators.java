package org.gbif.dwc.validator;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.chain.CriteriaChain;
import org.gbif.dwc.validator.criteria.DatasetCriterion;
import org.gbif.dwc.validator.criteria.RecordCriterion;
import org.gbif.dwc.validator.criteria.RecordCriteriaBuilder;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriteria;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriterionBuilder;
import org.gbif.dwc.validator.criteria.record.RecordCriterionBuilder;

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

  private final List<RecordCriterionBuilder> buildersList;
  private final List<DatasetCriterionBuilder> datasetCriteriaBuildersList;

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
      builder().with(RecordCriteriaBuilder.checkForInvalidCharacter(DwcTerm.scientificName))
        .with(RecordCriteriaBuilder.withinRange(DwcTerm.decimalLatitude, MIN_LATITUDE, MAX_LATITUDE))
        .with(RecordCriteriaBuilder.withinRange(DwcTerm.decimalLongitude, MIN_LONGITUDE, MAX_LONGITUDE))
        .with(DatasetCriterion.archiveIdIntegrity(tempFolder));
    return val;
  }

  /**
   * Build a FileEvaluator from an existing validation chain.
   * 
   * @param tempFolder
   * @param head
   * @return
   */
  public static FileEvaluator buildFromValidationChain(File tempFolder, CriteriaChain head) {
    return new DwcArchiveEvaluator(head);
  }

  /**
   * Build a validation chain from a list of RecordCriteria and DatasetCriteria.
   * 
   * @param recordCriteriaList
   * @param datasetCriteriaList
   * @return new CriteriaChain
   */
  public static CriteriaChain buildFromEvaluatorList(List<RecordCriterion> recordCriteriaList,
    List<DatasetCriteria> datasetCriteriaList) {
    return new CriteriaChain(recordCriteriaList, datasetCriteriaList);
  }


  /**
   * Private constructor, use Validator.builder()
   */
  private Evaluators() {
    this.buildersList = new ArrayList<RecordCriterionBuilder>();
    this.datasetCriteriaBuildersList = new ArrayList<DatasetCriterionBuilder>();
  }

  /**
   * Append a validation to the validation chain.
   * 
   * @param recordEvaluatorBuilder
   * @return
   */
  public Evaluators with(RecordCriterionBuilder recordEvaluatorBuilder) {
    buildersList.add(recordEvaluatorBuilder);
    return this;
  }

  public Evaluators with(DatasetCriterionBuilder datasetCriteriaBuilder) {
    datasetCriteriaBuildersList.add(datasetCriteriaBuilder);
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
  public CriteriaChain buildChain() throws IllegalStateException {
    List<RecordCriterion> recordCriteriaList = new ArrayList<RecordCriterion>();
    for (RecordCriterionBuilder currRecordCriteriaBuilder : buildersList) {
      recordCriteriaList.add(currRecordCriteriaBuilder.build());
    }

    List<DatasetCriteria> datasetCriteriaList = new ArrayList<DatasetCriteria>();
    for (DatasetCriterionBuilder currDDatasetCriteriaBuilder : datasetCriteriaBuildersList) {
      datasetCriteriaList.add(currDDatasetCriteriaBuilder.build());
    }

    return new CriteriaChain(recordCriteriaList, datasetCriteriaList);
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
