package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.validator.evaluator.integrity.ReferenceUniqueEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.integrity.UniquenessEvaluatorBuilder;

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
   * Check uniqueness of the coreId and that all extension(s) records point to a valid core id.
   * 
   * @param tempFolder
   * @return
   */
  public static ReferenceUniqueEvaluatorBuilder archiveIdIntegrity(File tempFolder) {
    return ReferenceUniqueEvaluatorBuilder.builder().workingFolder(tempFolder);
  }
}
