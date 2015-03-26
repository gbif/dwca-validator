package org.gbif.dwc.validator.transformation;

import org.gbif.dwc.record.Record;

/**
 * A ValueTransformation transforms one or more values from a Record into an instance of T.
 * We use transformation in order to make the data usable in a Criterion.
 *
 * @author cgendreau
 * @param <T>
 */
public interface ValueTransformation<T> {

  ValueTransformationResult<T> transform(Record record);

}
