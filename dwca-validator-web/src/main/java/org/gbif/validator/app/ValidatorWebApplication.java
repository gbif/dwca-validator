package org.gbif.validator.app;

import org.gbif.validator.conf.ValidatorWebConfiguration;
import org.gbif.validator.file.FileUploadManager;
import org.gbif.validator.health.ValidatorHealthCheck;
import org.gbif.validator.resource.ValidateResource;

import java.io.File;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 */
public class ValidatorWebApplication extends Application<ValidatorWebConfiguration> {

  private static final String APP_NAME = "validator";

  public static void main(String[] args) throws Exception {
    new ValidatorWebApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<ValidatorWebConfiguration> bootstrap){
    bootstrap.addBundle(new AssetsBundle("/assets/", "/"));

  }

  @Override
  public void run(ValidatorWebConfiguration validatorWebConfiguration, Environment environment) throws Exception {
    final ValidateResource resource =
            new ValidateResource(new FileUploadManager(new File(validatorWebConfiguration.getUploadLocation())));
    environment.jersey().register(resource);


    //custom Servlet
    //DropwizardResourceConfig jerseyConfig = new DropwizardResourceConfig(environment.metrics());
    //JerseyContainerHolder servletContainerHolder = new JerseyContainerHolder(new FileUploadServlet());

   // environment.admin().addServlet("a", FileUploadServlet.class).addMapping("/up");
   // MultipartConfigElement mce = new MultipartConfigElement("", 10, 1, 10);
   // environment.getApplicationContext().addServlet(FileUploadServlet.class, "/upload").getRegistration().setMultipartConfig(mce);

    // Health check
    final ValidatorHealthCheck healthCheck = new ValidatorHealthCheck();
    environment.healthChecks().register("main", healthCheck);
    environment.jersey().register(resource);

    // file upload
    environment.jersey().register(MultiPartFeature.class);


  }

  @Override
  public String getName() {
    return APP_NAME;
  }
}
