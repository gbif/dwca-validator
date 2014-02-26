package org.gbif.dwc.validator.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class to generate and build mock data for testing purpose.
 * 
 * @author cgendreau
 */
public class MockDataGenerator {

  /**
   * Generate a list of random alphabetic strings.
   * 
   * @param size
   * @param strLength
   * @return
   */
  public static List<String> newRandomDataList(int size, int strLength) {
    List<String> dataList = new ArrayList<String>(size);
    for (int i = 0; i < size; i++) {
      dataList.add(RandomStringUtils.randomAlphabetic(strLength));
    }
    return dataList;
  }

}
