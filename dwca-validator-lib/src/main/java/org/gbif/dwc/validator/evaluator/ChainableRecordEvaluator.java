package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;

/**
 * Abstract class for chainable evaluators.
 * @author christiangendreau
 *
 */
public abstract class ChainableRecordEvaluator {
	
	private ChainableRecordEvaluator nextElement;
	
	/**
	 * Do validation and call nest element in the chain (if there is one).
	 * @param record
	 */
	public void doEval(Record record){
		handleEval(record);
		if(nextElement == null){
			nextElement.doEval(record);
		}
	}
	
	protected abstract void handleEval(Record record);
	protected abstract void postIterate();

}
