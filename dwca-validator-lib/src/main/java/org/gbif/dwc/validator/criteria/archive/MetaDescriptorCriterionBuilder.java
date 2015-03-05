package org.gbif.dwc.validator.criteria.archive;

import org.gbif.dwc.validator.config.ValidatorConfig;
import org.gbif.dwc.validator.exception.CriterionBuilderException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

/**
 * Builder of MetadataCriterion representing a MetaDescriptor (meta.xml).
 * 
 * @author cgendreau
 */
public class MetaDescriptorCriterionBuilder {

  private static final String META_XML_FILE = "meta.xml";
  public String metadataFilename;

  public static MetaDescriptorCriterionBuilder builder() {
    return new MetaDescriptorCriterionBuilder();
  }

  /**
   * Used to override the filename of the meta descriptor file.
   * The default value is meta.xml
   * 
   * @param metadataFilename
   * @return
   */
  public MetaDescriptorCriterionBuilder metaDescriptorFileName(String metadataFilename) {
    this.metadataFilename = metadataFilename;
    return this;
  }

  public MetadataCriterion build() throws CriterionBuilderException {
    try {
      SchemaFactory factory = SchemaFactory.newInstance(ValidatorConfig.XML_SCHEMA_LANG);
      // TODO this should be exposed in case we want to use another schema
      Schema schema = factory.newSchema(new URL(ValidatorConfig.META_XML_SCHEMA));
      Validator metaValidator = schema.newValidator();

      return new MetadataFileCriterion(metaValidator, StringUtils.defaultString(metadataFilename, META_XML_FILE));
    } catch (MalformedURLException e) {
      throw new CriterionBuilderException("Can't create Meta XML Schema", e);
    } catch (SAXException e) {
      throw new CriterionBuilderException("Can't create Meta XML Schema", e);
    }
  }

}
