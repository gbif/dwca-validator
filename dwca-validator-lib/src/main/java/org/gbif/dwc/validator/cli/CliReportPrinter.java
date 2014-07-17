package org.gbif.dwc.validator.cli;

import org.gbif.dwc.validator.result.ValidationResult;
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
      for (ValidationResult vr : resultAccumulator.getValidationResultsList()) {
        System.out.println(vr.getContext() + " : " + vr.getId());
        for (ValidationResultElement el : vr.getResults()) {
          System.out.println("->" + el.getResult() + "," + el.getType() + ":" + el.getExplanation());
        }
      }
    } else {
      System.out.println("The Dwc-A file looks valid according to current default validation chain.");
    }
  }

}
