package org.gbif.dwc.validator.result;

/**
 * Exception wrapper for ResultAccumulator.
 * 
 * @author cgendreau
 */
public class ResultAccumulationException extends Exception {

  private static final long serialVersionUID = 517038264642063106L;

  public ResultAccumulationException(Throwable ex) {
    super(ex);
  }

  public ResultAccumulationException(String message) {
    super(message);
  }
}
