package org.gbif.dwc.validator.cli;

import org.gbif.dwc.validator.result.EvaluationResultElementIF;
import org.gbif.dwc.validator.result.EvaluationResultIF;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

/**
 * Validation results report printer for command line usage.
 * 
 * @author cgendreau
 */
public class CliReportPrinter {


  /**
   * Print report to the standard output.
   * 
   * @param resultAccumulator
   */
  public static void printReport(InMemoryResultAccumulator resultAccumulator) {
    // Print results
    if (resultAccumulator.getCount() > 0) {
      System.out.println("The Dwc-A file looks invalid according to current default validation chain:");
      System.out.println("Validation chain output(s):");
      ValidationResultElement validationElement;
      for (EvaluationResultIF<? extends EvaluationResultElementIF> vr : resultAccumulator.getEvaluationResultList()) {
        System.out.println(vr.getContext() + " : " + vr.getId());
        for (EvaluationResultElementIF el : vr.getResults()) {
          validationElement = (ValidationResultElement) el;
          System.out.println("->" + validationElement.getResult() + "," + validationElement.getType() + ":"
            + validationElement.getExplanation());
        }
      }
    } else {
      System.out.println("The Dwc-A file looks valid according to current default validation chain.");
    }
  }

}
