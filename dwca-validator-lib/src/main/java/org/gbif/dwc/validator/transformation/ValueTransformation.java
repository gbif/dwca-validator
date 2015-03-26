package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;

import java.util.List;

/**
 * A ValueTransformation transforms one or more values from a Record into an instance of T.
 * We use transformation in order to make the data usable in a Criterion.
 *
 * @author cgendreau
 * @param <T>
 */
public interface ValueTransformation<T> {

  /**
   * Get the list of term used by this transformation.
   * 
   * @return
   */
  List<Term> getTerms();

  ValueTransformationResult<T> transform(Record record);

}
