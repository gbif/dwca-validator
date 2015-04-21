package org.gbif.dwc.validator.chain;

import org.gbif.dwc.validator.criteria.metadata.MetadataCriterion;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.File;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * This class is responsible to manage the evaluation chain for metadata file(s).
 *
 * @author cgendreau
 */
public class MetadataEvaluatorChain {

  private final List<MetadataCriterion> metadataCriteriaList = Lists.newArrayList();

  public MetadataEvaluatorChain(List<MetadataCriterion> metadataCriteriaList) {
    this.metadataCriteriaList.addAll(ImmutableList.copyOf(metadataCriteriaList));
  }

  /**
   * Evaluate a metadata file.
   *
   * @param metadataFile
   * @param resultAccumulator
   * @throws ResultAccumulationException
   */
  public void evaluateMetadataFile(File metadataFile, ResultAccumulator resultAccumulator)
    throws ResultAccumulationException {
    Optional<ValidationResult> result;
    for (MetadataCriterion currMetadataCriterion : metadataCriteriaList) {
      result = currMetadataCriterion.validate(metadataFile);
      if (result.isPresent()) {
        result.get().accept(resultAccumulator);
      }
    }
  }


}
