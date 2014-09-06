package org.gbif.dwc.validator.result;

import java.util.List;

/**
 * Interface of an evaluation result.
 * 
 * @author cgendreau
 * @param <T>
 */
public interface EvaluationResultIF<T extends EvaluationResultElementIF> {

  EvaluationContext getContext();

  String getId();

  List<T> getResults();

}
