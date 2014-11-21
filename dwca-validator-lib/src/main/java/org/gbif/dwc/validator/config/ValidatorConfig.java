package org.gbif.dwc.validator.config;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Container class to hold configurations related to the ArchiveValidator.
 * //TODO remove static method and allow language configuration
 * 
 * @author cgendreau
 */
public class ValidatorConfig {

  private static final ValidatorConfig _instance = new ValidatorConfig();

  // TODO load from config file
  public static final String META_XML_SCHEMA = "http://rs.tdwg.org/dwc/text/tdwg_dwc_text.xsd";

  public static final String CORE_ID = "coreId";

  // Used to display a message regarding the presence of an empty string
  public static final String EMPTY_STRING_FOR_DISPLAY = "<emtpy string>";

  public static final String ENDLINE = System.getProperty("line.separator");
  public static final String TEXT_FILE_EXT = ".txt";
  public static final String CSV_FILE_EXT = ".csv";

  public final File workingFolder;

  private static final String BUNDLE_NAME = "language_resources";

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);

  public static ValidatorConfig getInstance() {
    return _instance;
  }

  private ValidatorConfig() {
    File _workingFolder = new File(".");
    try {
      _workingFolder = new File(new File(".").getCanonicalFile(), "temp");
    } catch (IOException e) {
      e.printStackTrace();
    }
    workingFolder = _workingFolder;
  }

  /**
   * Get the folder to used to save temporary files.
   * 
   * @return
   */
  public File getWorkingFolder() {
    return workingFolder;
  }

  /**
   * Get a localized string represented by the provided key.
   * 
   * @param key
   * @return
   */
  public static String getLocalizedString(String key) {
    return RESOURCE_BUNDLE.getString(key);
  }

  /**
   * Get a localized string represented by the provided key.
   * The string contains parameter substitution in the form of {0} test {1}
   * 
   * @param key
   * @param params
   * @return
   */
  public static String getLocalizedString(String key, Object... params) {
    return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
  }

}
