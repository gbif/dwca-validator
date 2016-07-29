package org.gbif.validator.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;

/**
 * Simple file manager to create folders with random names (UUID) from a base folder and save some file in it.
 */
public class FileUploadManager {

  private final File baseFolder;

  /**
   * @param baseFolder folder from which this manager can create and write files
   */
  public FileUploadManager(File baseFolder) {
    this.baseFolder = baseFolder;
  }

  /**
   * Save an {@link InputStream} to a file inside the folder structure this manager is working on.
   * A new folder with a random UUID will be created for the file.
   *
   * @param filename    file name including extension
   * @param inputStream stream from which the data is coming, will NOT be closed after the copy
   *
   * @return the File object of the newly created file
   */
  public File save(String filename, InputStream inputStream) throws IOException {
    Preconditions.checkNotNull(filename, "filename can not be null");

    String randomFolderName = UUID.randomUUID().toString();
    File newFolder = new File(baseFolder, randomFolderName);
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
