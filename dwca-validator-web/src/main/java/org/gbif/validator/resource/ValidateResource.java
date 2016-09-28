package org.gbif.validator.resource;

import org.gbif.validator.file.FileUploadManager;
import org.gbif.validator.model.ValidationResult;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 */
@Path("/validate")
@Produces(MediaType.APPLICATION_JSON)
public class ValidateResource {

  private FileUploadManager fileUploadManager;

  public ValidateResource(FileUploadManager fum){
    this.fileUploadManager = fum;
  }


  @GET
  @Timed
  public ValidationResult sayHello(@QueryParam("name") Optional<String> name) {
    //final String value = String.format(template, name.or(defaultName));
    return new ValidationResult();
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public void uploadArchive(@FormDataParam("file") final InputStream inputStream,
                            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
  throws IOException{

    //try {
      fileUploadManager.save("toto.zip", inputStream);
    //} catch (IOException e) {
      // TODO proper LOG and return appropriate HTTP code
    //  e.printStackTrace();
    //}

  }

}

