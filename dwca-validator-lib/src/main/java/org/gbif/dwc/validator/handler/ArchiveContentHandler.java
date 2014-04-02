package org.gbif.dwc.validator.handler;

import org.gbif.dwc.record.RecordIterator;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.validator.evaluator.ChainableRecordEvaluator;
import org.gbif.dwc.validator.evaluator.EvaluatorChainBuilder;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;

/**
 * Handler responsible for the validation of the content of the archive.
 * Content of the archive represents core file and extensions file(s).
 * 
 * @author cgendreau
 */
public class ArchiveContentHandler {

  /**
   * Inspect DarwinCore core component.
   * 
   * @param archiveFile
   * @param resultAccumulator
   */
  public void inspectCore(ArchiveFile archiveFile, ResultAccumulatorIF resultAccumulator) {
    ChainableRecordEvaluator coreChain = EvaluatorChainBuilder.buildCoreChain();
    inspectDwcComponent(archiveFile, coreChain, resultAccumulator);
  }

  /**
   * Internal DarwinCore component record loop function.
   * 
   * @param dwcaComponent
   * @param evaluatorChain head of the evaluators chain
   * @param resultAccumulator
   */
  private void inspectDwcComponent(ArchiveFile dwcaComponent, ChainableRecordEvaluator evaluatorChain,
    ResultAccumulatorIF resultAccumulator) {
    RecordIterator recordIt = RecordIterator.build(dwcaComponent, false);
    while (recordIt.hasNext()) {
      evaluatorChain.doEval(recordIt.next(), resultAccumulator);
    }
  }

  public void inspectExtension(ArchiveFile archiveFile, ResultAccumulatorIF resultAccumulator) {

  }


}
