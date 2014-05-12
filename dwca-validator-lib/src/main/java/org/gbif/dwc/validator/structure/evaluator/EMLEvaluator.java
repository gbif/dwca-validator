package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.metadata.eml.ValidatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;


/**
 * @author melecoq
 */
public class EMLEvaluator {

  public class EMLInvalidException extends Exception {

    Exception exception;


    public EMLInvalidException(Exception e) {
      exception = e;
    }

    public Exception getException() {
      return exception;
    }


  }

  /**
   * @author melecoq
   * @throws EMLInvalidException
   */
  public void doEval(File eml) throws EMLInvalidException {
    handleEval(eml);
  }

  protected Source getEmlSource(File eml) throws FileNotFoundException {
    Source src = new StreamSource(new FileInputStream(eml));
    return src;
  }

  protected void handleEval(File eml) throws EMLInvalidException {

    try {
      ValidatorFactory.getGbifValidator().validate(getEmlSource(eml));
    } catch (SAXException e) {
      throw new EMLInvalidException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
