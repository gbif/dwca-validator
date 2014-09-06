package org.gbif.dwc.validator.structure.evaluator;

import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.result.Result;
import org.gbif.dwc.validator.result.ResultAccumulatorIF;
import org.gbif.dwc.validator.result.ValidationContext;
import org.gbif.dwc.validator.result.ValidationResult;
import org.gbif.dwc.validator.result.ValidationResultElement;
import org.gbif.dwc.validator.result.type.StructureValidationType;
import org.gbif.metadata.MetadataException;
import org.gbif.metadata.MetadataFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author melecoq
 */
public class MetaDescriptorEvaluator {

  /**
   * @author melecoq
   * @throws MetadataException
   */
  public void doEval(File metaXML, ResultAccumulatorIF result) {
    handleEval(metaXML, result);
  }

  protected void handleEval(File metaXML, ResultAccumulatorIF result) {
    MetadataFactory metadataValidatator = new MetadataFactory();
    try {
      metadataValidatator.read(metaXML);
    } catch (MetadataException e) {
      List<ValidationResultElement> list = new ArrayList<ValidationResultElement>();
      list.add(new ValidationResultElement(StructureValidationType.METADATA_SCHEMA, Result.ERROR,
        ArchiveValidatorConfig.getLocalizedString("evaluator.internal_error", e.getMessage())));
      result.accumulate(new ValidationResult("MetaDescriptorValidation", ValidationContext.STRUCTURE, list));
    }
  }
}
