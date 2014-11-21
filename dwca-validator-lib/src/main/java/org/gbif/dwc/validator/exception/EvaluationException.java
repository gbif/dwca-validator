package org.gbif.dwc.validator.exception;


public class EvaluationException extends Exception {

  private static final long serialVersionUID = 517038264642063106L;

  public EvaluationException(Throwable cause) {
    super(cause);
  }

  public EvaluationException(String message) {
    super(message);
  }

  public EvaluationException(String message, Throwable cause) {
    super(message, cause);
  }

}
