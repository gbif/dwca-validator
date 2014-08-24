package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.EvaluationResult;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.ValidationTypeIF;

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
  public static boolean containsResultMessage(List<EvaluationResult> results, String id, String message) {
    for (EvaluationResult currResult : results) {
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
  public static boolean containsValidationType(List<EvaluationResult> results, String id,
    ValidationTypeIF validationType) {
    for (EvaluationResult currResult : results) {
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
