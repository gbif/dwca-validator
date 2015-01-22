package org.gbif.dwc.validator.refactor;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class GenericCriteriaTest {

  private Record buildMockRecord(String id, String lat, String lng, String minimumElevationInMeters,
    String maximumElevationInMeters) {
    // create a mock Record
    ArchiveField idField = new ArchiveField(0, DwcTerm.occurrenceID, null, DataType.string);
    ArchiveField latField = new ArchiveField(1, DwcTerm.decimalLatitude, null, DataType.string);
    ArchiveField longField = new ArchiveField(2, DwcTerm.decimalLongitude, null, DataType.string);
    ArchiveField minimumElevationField = new ArchiveField(3, DwcTerm.minimumElevationInMeters, null, DataType.string);
    ArchiveField maximumElevationField = new ArchiveField(4, DwcTerm.maximumElevationInMeters, null, DataType.string);
    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(latField);
    fieldList.add(longField);
    fieldList.add(minimumElevationField);
    fieldList.add(maximumElevationField);
    RecordImpl testRecord = new RecordImpl(idField, fieldList, "rowType", false);
    testRecord.setRow(new String[] {id, lat, lng, minimumElevationInMeters, maximumElevationInMeters});
    return testRecord;
  }

  @Test
  public void testValueEvaluator() {

    // register an InvalidCharacterEvaluationRule for scientificName
// List<ValueTransformation<?>> parsers = new ArrayList<ValueTransformation<?>>();
// parsers.add(ValueTransformations.toNumeric(DwcTerm.decimalLatitude));
// parsers.add(ValueTransformations.toNumeric(DwcTerm.decimalLongitude));
//
// RecordCriteria areNumber = new GenericCriteria(parsers);
//
// RecordCriteria minMaxElevationCriteria =
// new MinMaxCriteria(ValueTransformations.toNumeric(DwcTerm.minimumElevationInMeters),
// ValueTransformations.toNumeric(DwcTerm.maximumElevationInMeters));
//
// BoundCriteria boundMinElevationCriteria =
// new BoundCriteria(-2, 2, ValueTransformations.toNumeric(DwcTerm.minimumElevationInMeters));
//
// // Optional<ValidationResult> result = areNumber.validate(buildMockRecord("1", "a", "b","1","2"),
// // EvaluationContext.CORE);
// // System.out.println(areNumber.validate(buildMockRecord("1", "a", "b","1","2"), EvaluationContext.CORE));
//
// System.out.println(boundMinElevationCriteria.validate(buildMockRecord("1", "a", "b", "10", "20"),
// EvaluationContext.CORE));
  }

}
