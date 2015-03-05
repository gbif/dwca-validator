package org.gbif.dwc.validator.exception;

/**
 * Exception wrapper for criterion builders.
 * 
 * @author cgendreau
 */
public class CriterionBuilderException extends Exception {

  private static final long serialVersionUID = 5506125742033783202L;

  public CriterionBuilderException(Throwable cause) {
    super(cause);
  }

  public CriterionBuilderException(String message) {
    super(message);
  }

  public CriterionBuilderException(String message, Throwable cause) {
    super(message, cause);
  }

}
