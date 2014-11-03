package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.type.ValidationTypeIF;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

import java.util.List;


public class TestEvaluationResultHelper {


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
        for (ValidationResultElement currValidationElement : currResult.getResults()) {
          if (validationType.equals(currValidationElement.getType())) {
            return true;
          }
        }
      }
    }
    return false;
  }

}
