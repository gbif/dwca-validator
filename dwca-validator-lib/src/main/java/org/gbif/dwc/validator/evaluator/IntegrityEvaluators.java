package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.validator.evaluator.integrity.ReferenceUniqueEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.integrity.UniquenessEvaluatorBuilder;
import org.gbif.dwc.validator.result.EvaluationContext;

import java.io.File;

/**
 * Collection of builders related to integrity validations.
 * 
 * @author cgendreau
 */
public class IntegrityEvaluators {

  /**
   * Check the uniqueness of the coreId.
   * 
   * @param workingFolder folder that should be used to saved temporary files. The folder must already exist.
   * @return
   */
  public static UniquenessEvaluatorBuilder coreIdUniqueness(File tempFolder) {
    return UniquenessEvaluatorBuilder.builder().workingFolder(tempFolder);
  }

  /**
   * Check uniqueness of the coreId and that all extension(s) records point to a valid coreId (aka star schema).
   * 
   * @param tempFolder
   * @return
   */
  public static ReferenceUniqueEvaluatorBuilder archiveIdIntegrity(File tempFolder) {
    return ReferenceUniqueEvaluatorBuilder.builder().workingFolder(tempFolder);
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
  public static ReferenceUniqueEvaluatorBuilder termReferentialIntegrityInCore(File tempFolder,
    Term targetedTerm, Term referredTerm, String rowType) {
    return ReferenceUniqueEvaluatorBuilder.builder()
      .termRefersToUnique(targetedTerm, EvaluationContext.CORE, rowType, referredTerm, EvaluationContext.CORE, rowType)
      .workingFolder(tempFolder);
  }
}
