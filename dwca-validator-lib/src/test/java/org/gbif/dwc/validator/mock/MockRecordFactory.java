package org.gbif.dwc.validator.mock;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Factory class to build mock record for testing purpose.
 * 
 * @author cgendreau
 */
public class MockRecordFactory {

  /**
   * Build a mock Record instance for testing purpose.
   * 
   * @param idTerm
   * @param idValue
   * @param terms this should not include the term used in idTerm
   * @param values this should not include the the value idValue
   * @return
   */
  public static Record buildMockRecord(DwcTerm idTerm, String idValue, DwcTerm[] terms, String[] values) {

    if (terms == null || values == null || (terms.length != values.length)) {
      throw new IllegalArgumentException();
    }

    int idx = 0;
    ArchiveField idField = new ArchiveField(idx, idTerm, null, DataType.string);
    idx++;

    // create ArchiveField
    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();

    for (DwcTerm currTerm : terms) {
      fieldList.add(new ArchiveField(idx, currTerm, null, DataType.string));
      idx++;
    }
    RecordImpl testRecord = new RecordImpl(idField, fieldList, "Occurrence", false);

    List<String> row = new ArrayList<String>(Arrays.asList(values));
    row.add(0, idValue);
    testRecord.setRow(row.toArray(new String[0]));
    return testRecord;
  }

}
