package org.gbif.dwc.validator.cli;

import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.validation.ValidationResult;
import org.gbif.dwc.validator.result.validation.ValidationResultElement;

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
      for (ValidationResult vr : resultAccumulator.getValidationResultList()) {
        System.out.println(vr.getEvaluationContext() + " : " + vr.getId());
        for (ValidationResultElement vre : vr.getResults()) {
          System.out.println("->" + vre.getResult() + "," + vre.getType() + ":" + vre.getExplanation());
        }
      }
      if (resultAccumulator.getValidationResultList().size() == InMemoryResultAccumulator.MAX_RESULT) {
        System.out.println("Maximum of " + InMemoryResultAccumulator.MAX_RESULT + " results recorded.");
      }
    } else {
      System.out.println("The Dwc-A file looks valid according to current default validation chain.");
    }
  }

}
