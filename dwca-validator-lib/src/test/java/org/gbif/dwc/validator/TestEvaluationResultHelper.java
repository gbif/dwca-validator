package org.gbif.dwc.validator;

import org.gbif.dwc.validator.result.EvaluationResultElementIF;
import org.gbif.dwc.validator.result.EvaluationResultIF;
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
  public static boolean containsResultMessage(List<EvaluationResultIF<? extends EvaluationResultElementIF>> results,
    String id, String message) {
    for (EvaluationResultIF<? extends EvaluationResultElementIF> currResult : results) {
      if (currResult.getId().equals(id)) {
        for (EvaluationResultElementIF currValidationElement : currResult.getResults()) {
          if (message.equals(((ValidationResultElement) currValidationElement).getExplanation())) {
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
  public static boolean containsValidationType(List<EvaluationResultIF<? extends EvaluationResultElementIF>> results,
    String id, ValidationTypeIF validationType) {
    for (EvaluationResultIF<? extends EvaluationResultElementIF> currResult : results) {
      if (currResult.getId().equals(id)) {
        for (EvaluationResultElementIF currValidationElement : currResult.getResults()) {
          if (validationType.equals(((ValidationResultElement) currValidationElement).getType())) {
            return true;
          }
        }
      }
    }
    return false;
  }

}
