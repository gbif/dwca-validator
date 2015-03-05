package org.gbif.dwc.validator.criteria.metadata;

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
 * Builder of MetadataCriterion representing an EML file.
 * 
 * @author cgendreau
 */
public class EMLCriterionBuilder {

  private static final String EML_SCHEMA_URL = "http://rs.gbif.org/schema/eml-2.1.1/eml.xsd";
  private static final String EML_GBIF_PROFILE_SCHEMA_URL = "http://rs.gbif.org/schema/eml-gbif-profile/dev/eml.xsd";

  private static final String EML_FILE = "eml.xml";
  public String emlFileName;
  public String emlSchemaURL;

  public static EMLCriterionBuilder builder() {
    return new EMLCriterionBuilder();
  }

  /**
   * Used to override the filename of the meta descriptor file.
   * The default value is meta.xml
   * 
   * @param metadataFilename
   * @return
   */
  public EMLCriterionBuilder emlFileName(String emlFileName) {
    this.emlFileName = emlFileName;
    return this;
  }

  /**
   * Used the GBIF profile eml schema.
   * 
   * @return
   */
  public EMLCriterionBuilder gbifProfile() {
    this.emlSchemaURL = EML_GBIF_PROFILE_SCHEMA_URL;
    return this;
  }

  /**
   * Used to specify a schemaURL to use.
   * 
   * @param schemaURL
   * @return
   */
  public EMLCriterionBuilder schemaURL(String emlSchemaURL) {
    this.emlSchemaURL = emlSchemaURL;
    return this;
  }

  public MetadataCriterion build() throws CriterionBuilderException {
    try {
      SchemaFactory factory = SchemaFactory.newInstance(ValidatorConfig.XML_SCHEMA_LANG);
      // TODO this should be exposed in case we want to use another schema
      Schema schema = factory.newSchema(new URL(StringUtils.defaultString(emlSchemaURL, EML_SCHEMA_URL)));
      Validator metaValidator = schema.newValidator();
      return new XMLMetadataCriterion(metaValidator, StringUtils.defaultString(emlFileName, EML_FILE));
    } catch (MalformedURLException e) {
      throw new CriterionBuilderException("Can't create Meta XML Schema", e);
    } catch (SAXException e) {
      throw new CriterionBuilderException("Can't create Meta XML Schema", e);
    }
  }
}
