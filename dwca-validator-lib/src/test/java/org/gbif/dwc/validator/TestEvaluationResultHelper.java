package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.type.ValidationTypeIF;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.util.List;

import com.google.common.base.Optional;


public class TestEvaluationResultHelper {

  /**
   * This method is simply to increase code readability since Optional makes it too verbose.
   * 
   * @param result
   */
  public static boolean validationPassed(Optional<ValidationResult> result) {
    return result.isPresent() && result.get().passed();
  }

  /**
   * This method is simply to increase code readability since Optional makes it too verbose.
   * 
   * @param result
   */
  public static boolean validationFailed(Optional<ValidationResult> result) {
    return result.isPresent() && result.get().failed();
  }


  /**
   * Check that a message associated with an id is in the result list.
   * 
   * @param results
   * @param id
   * @param message
   * @return
   */
  public static boolean containsResultMessage(List<ValidationResult> results, String id, String message) {
    for (ValidationResult currResult : results) {
      if (currResult.getId().equals(id)) {
        for (ValidationResultElement currValidationElement : currResult.getResults()) {
          if (message.equals(currValidationElement.getExplanation())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Check that a validationType associated with an id is in the result list.
   * 
   * @param results
   * @param validationType
   * @return
   */
  public static boolean containsValidationType(List<ValidationResult> results, String id,
    ValidationTypeIF validationType) {
    for (ValidationResult currResult : results) {
      if (currResult.getId().equals(id)) {
        if (currResult.getResults() != null) {
          for (ValidationResultElement currValidationElement : currResult.getResults()) {
            if (validationType.equals(currValidationElement.getType())) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public static boolean containsValidationType(ValidationResult validationResult, ValidationTypeIF validationType) {
    for (ValidationResultElement currValidationElement : validationResult.getResults()) {
      if (validationType.equals(currValidationElement.getType())) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsResultMessage(ValidationResult validationResult, String message) {
    for (ValidationResultElement currValidationElement : validationResult.getResults()) {
      if (message.equals(currValidationElement.getExplanation())) {
        return true;
      }
    }
    return false;
  }

}
