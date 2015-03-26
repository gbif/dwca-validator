package org.gbif.dwc.validator;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.chain.EvaluatorChain;
import org.gbif.dwc.validator.criteria.DatasetCriteria;
import org.gbif.dwc.validator.criteria.RecordCriteria;
import org.gbif.dwc.validator.criteria.ValidationCriterion;
import org.gbif.dwc.validator.criteria.dataset.DatasetCriterion;
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
      builder().with(RecordCriteria.checkForInvalidCharacter(DwcTerm.scientificName))
        .with(RecordCriteria.withinRange(DwcTerm.decimalLatitude, MIN_LATITUDE, MAX_LATITUDE))
        .with(RecordCriteria.withinRange(DwcTerm.decimalLongitude, MIN_LONGITUDE, MAX_LONGITUDE))
        .with(DatasetCriteria.archiveIdIntegrity(tempFolder));
    return val;
  }

  /**
   * Build a FileEvaluator from an existing validation chain.
   *
   * @param tempFolder
   * @param head
   * @return
   */
  public static FileEvaluator buildFromValidationChain(File tempFolder, EvaluatorChain head) {
    return new DwcArchiveEvaluator(head);
  }

  /**
   * Build a validation chain from a list of RecordCriteria and DatasetCriteria.
   *
   * @param recordCriterionList
   * @param datasetCriterionList
   * @return new CriteriaChain
   */
  public static EvaluatorChain buildFromEvaluatorList(List<ValidationCriterion> recordCriterionList,
    List<DatasetCriterion> datasetCriterionList) {
    return new EvaluatorChain(recordCriterionList, datasetCriterionList);
  }


  /**
   * Private constructor, use Validator.builder()
   */
  private Evaluators() {
    this.buildersList = new ArrayList<RecordCriterionBuilder>();
    this.datasetCriteriaBuildersList = new ArrayList<DatasetCriterionBuilder>();
  }

  /**
   * Append a record level criterion to the validation chain.
   *
   * @param recordEvaluatorBuilder
   * @return
   */
  public Evaluators with(RecordCriterionBuilder recordEvaluatorBuilder) {
    buildersList.add(recordEvaluatorBuilder);
    return this;
  }

  /**
   * Append a dataset level criterion to the validation chain.
   *
   * @param datasetCriteriaBuilder
   * @return
   */
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
  public EvaluatorChain buildChain() throws IllegalStateException {
    List<ValidationCriterion> recordCriteriaList = new ArrayList<ValidationCriterion>();
    for (RecordCriterionBuilder currRecordCriteriaBuilder : buildersList) {
      recordCriteriaList.add(currRecordCriteriaBuilder.build());
    }

    List<DatasetCriterion> datasetCriteriaList = new ArrayList<DatasetCriterion>();
    for (DatasetCriterionBuilder currDDatasetCriteriaBuilder : datasetCriteriaBuildersList) {
      datasetCriteriaList.add(currDDatasetCriteriaBuilder.build());
    }

    return new EvaluatorChain(recordCriteriaList, datasetCriteriaList);
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
