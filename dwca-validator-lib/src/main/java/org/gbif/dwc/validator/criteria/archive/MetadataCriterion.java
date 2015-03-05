package org.gbif.dwc.validator.criteria.archive;

import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.File;

import com.google.common.base.Optional;

/**
 * Criterion interface for metadata level validation.
 * 
 * @author cgendreau
 */
public interface MetadataCriterion {

  Optional<ValidationResult> validate(File metadataFile);

}
