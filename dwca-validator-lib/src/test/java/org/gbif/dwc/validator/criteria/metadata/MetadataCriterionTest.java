package org.gbif.dwc.validator.criteria.metadata;

import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.criteria.metadata.EMLCriterionBuilder;
import org.gbif.dwc.validator.criteria.metadata.MetaDescriptorCriterionBuilder;
import org.gbif.dwc.validator.criteria.metadata.MetadataCriterion;
import org.gbif.dwc.validator.exception.CriterionBuilderException;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.File;
import java.net.URISyntaxException;

import com.google.common.base.Optional;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test MetaDescriptorCriterion implementation.
 * 
 * @author cgendreau
 */
public class MetadataCriterionTest {

  @Test
  public void testMetaXMLSchemaValidation() {
    try {
      File testDwca = new File(getClass().getResource("/metadata/broken_meta.xml").toURI());

      MetadataCriterion metadataCriterion =
        MetaDescriptorCriterionBuilder.builder().metaDescriptorFileName("broken_meta.xml").build();
      Optional<ValidationResult> result = metadataCriterion.validate(testDwca);

      assertTrue(TestEvaluationResultHelper.validationFailed(result));
    } catch (CriterionBuilderException e) {
      e.printStackTrace();
      fail();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testMetaEMLSchemaValidationWithGBIFProfile() {
    try {
      File testDwca = new File(getClass().getResource("/eml/broken_eml.xml").toURI());

      MetadataCriterion metadataCriterion =
        EMLCriterionBuilder.builder().emlFileName("broken_eml.xml").gbifProfile().build();
      Optional<ValidationResult> result = metadataCriterion.validate(testDwca);

      assertTrue(TestEvaluationResultHelper.validationFailed(result));
    } catch (CriterionBuilderException e) {
      e.printStackTrace();
      fail();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }
  }

}
