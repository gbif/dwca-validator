package org.gbif.dwc.validator.criteria;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.criteria.dataset.ReferenceUniqueCriterionBuilder;
import org.gbif.dwc.validator.criteria.dataset.UniquenessCriterionBuilder;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;


public class DatasetCriteria {


  /**
   * Check the uniqueness of the coreId.
   * 
   * @param workingFolder folder that should be used to saved temporary files. The folder must already exist.
   * @return
   */
  public static UniquenessCriterionBuilder coreIdUniqueness(File tempFolder) {
    return UniquenessCriterionBuilder.builder().workingFolder(tempFolder);
  }

  /**
   * Check uniqueness of the coreId and that all extension(s) records point to a valid coreId (aka star schema).
   * 
   * @param tempFolder
   * @return
   */
  public static ReferenceUniqueCriterionBuilder archiveIdIntegrity(File tempFolder) {
    return ReferenceUniqueCriterionBuilder.builder().workingFolder(tempFolder);
  }

  /**
   * Check that targetedTerm value is pointing to a valid value of referredTerm within the Core. Uniqueness of
   * referredTerm will also be checked.
   * 
   * @param tempFolder
   * @param targetedTerm term of which the value must match a valid value of referedTerm
   * @param referredTerm term referred by targetedTerm
   * @param rowType targeted core rowType (e.g. DwcTerm.Taxon.qualifiedName())
   * @return
   */
  public static ReferenceUniqueCriterionBuilder termReferentialIntegrityInCore(File tempFolder, Term targetedTerm,
    Term referredTerm, String rowType) {
    return ReferenceUniqueCriterionBuilder.builder()
      .termRefersToUnique(targetedTerm, EvaluationContext.CORE, rowType, referredTerm, EvaluationContext.CORE, rowType)
      .workingFolder(tempFolder);
  }
}
