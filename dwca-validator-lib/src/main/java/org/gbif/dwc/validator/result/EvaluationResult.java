package org.gbif.dwc.validator.result;

import java.util.List;

/**
 * Interface of an evaluation result.
 * 
 * @author cgendreau
 * @param <T>
 */
public interface EvaluationResult<T> {

  EvaluationContext getEvaluationContext();

  String getId();

  List<T> getResults();

}
