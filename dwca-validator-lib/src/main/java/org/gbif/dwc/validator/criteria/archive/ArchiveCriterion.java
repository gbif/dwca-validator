package org.gbif.dwc.validator.criteria.archive;

import org.gbif.dwc.text.Archive;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import com.google.common.base.Optional;

/**
 * Criterion interface for Archive level validation.
 * 
 * @author cgendreau
 */
public interface ArchiveCriterion {

  Optional<ValidationResult> validate(Archive dwc);

}
