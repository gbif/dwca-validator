package org.gbif.dwc.validator.evaluator;

import java.io.Closeable;

/**
 * Interface defining a RecordEvaluator where it's required to keep a state to perform an evaluation.
 * 
 * @author cgendreau
 */
public interface StatefulRecordEvaluatorIF extends RecordEvaluator, Closeable {

}
