package org.gbif.validator.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;

/**
 *
 */
@MultipartConfig(maxFileSize = 1)
public class FileUploadServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();

    Collection<Part> parts = request.getParts();

    out.write("<h2> Total parts : " + parts.size() + "</h2>");

    for (Part part : parts) {
      save("t1", part.getInputStream());
    }
  }

  public File save(String filename, InputStream inputStream) throws IOException {
    Preconditions.checkNotNull(filename, "filename can not be null");

    String randomFolderName = UUID.randomUUID().toString();
    File newFolder = new File("/tmp/toto/", randomFolderName);
    File uploadedFile = new File(newFolder, filename);
    if (newFolder.mkdir()) {
      try (FileOutputStream output = new FileOutputStream(uploadedFile)) {
        IOUtils.copy(inputStream, output);
      }
    } else {
      throw new IOException("can not create folder " + newFolder.getAbsolutePath());
    }
    return uploadedFile;
  }

}
