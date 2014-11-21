package org.gbif.dwc.validator.exception;

/**
 * Exception wrapper for ResultAccumulator.
 * 
 * @author cgendreau
 */
public class ResultAccumulationException extends Exception {

  private static final long serialVersionUID = 517038264642063106L;

  public ResultAccumulationException(Throwable cause) {
    super(cause);
  }

  public ResultAccumulationException(String message) {
    super(message);
  }

  public ResultAccumulationException(String message, Throwable cause) {
    super(message, cause);
  }
}
