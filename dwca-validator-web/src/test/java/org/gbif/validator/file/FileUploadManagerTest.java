package org.gbif.validator.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link FileUploadManager}
 */
public class FileUploadManagerTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testSave(){
    InputStream in = null;
    try {
      in = getClass().getResourceAsStream("/file/upload.txt");
      assertNotNull("resource file used for testing is found", in);

      // we need to read the file twice so we load it once and use a ByteArrayInputStream
      String uploadTestContent = IOUtils.toString(in, StandardCharsets.UTF_8);
      ByteArrayInputStream baIn = new ByteArrayInputStream(uploadTestContent.getBytes());

      FileUploadManager fum = new FileUploadManager(tempFolder.newFolder("fileUpload_manager_test"));
      File uploadedFile = fum.save("uploaded.txt", baIn);

      // read the content of the "uploaded" file
      String uploadedContent = FileUtils.readFileToString(uploadedFile, StandardCharsets.UTF_8);
      assertEquals(uploadTestContent, uploadedContent);

    } catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      IOUtils.closeQuietly(in);
    }

  }

}
