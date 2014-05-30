package org.gbif.dwc.validator.config;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Container class to hold configurations related to the ArchiveValidator.
 * //TODO remove static method and allow language configuration
 * 
 * @author cgendreau
 */
public class ArchiveValidatorConfig {

  private static final String BUNDLE_NAME = "language_resources";

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);

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
