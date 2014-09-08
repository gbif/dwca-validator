package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.impl.validation.ValidationResult;
import org.gbif.dwc.validator.result.impl.validation.ValidationResultElement;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.metadata.MetadataException;
import org.gbif.metadata.MetadataFactory;

import java.io.File;

/**
 * @author melecoq
 */
public class MetaDescriptorEvaluator {

  // TODO replace with new annotation like @StructureEvaluator
  private static final String key = "MetaDescriptorEvaluator";

  /**
   * @author melecoq
   * @throws MetadataException
   */
  public void doEval(File metaXML, ResultAccumulatorIF result) {
    handleEval(metaXML, result);
  }

  protected void handleEval(File metaXML, ResultAccumulatorIF result) {
    String identifier = metaXML.getName();
    MetadataFactory metadataValidatator = new MetadataFactory();
    try {
      metadataValidatator.read(metaXML);
    } catch (MetadataException e) {
      result.accumulate(new ValidationResult(identifier, key, EvaluationContext.STRUCTURE, new ValidationResultElement(
        StructureValidationType.METADATA_SCHEMA, Result.ERROR, ArchiveValidatorConfig.getLocalizedString(
          "evaluator.internal_error", e.getMessage()))));
    }
  }
}
