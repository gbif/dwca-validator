package org.gbif.validator.conf;

import io.dropwizard.Configuration;

/**
 * Main configuration class of the Dwc-A validator web component
 *
 */
public class ValidatorWebConfiguration extends Configuration {

  // we to store the uploaded files
  private String uploadLocation;

  public String getUploadLocation() {
    return uploadLocation;
  }

  public void setUploadLocation(String uploadLocation) {
    this.uploadLocation = uploadLocation;
  }


}
