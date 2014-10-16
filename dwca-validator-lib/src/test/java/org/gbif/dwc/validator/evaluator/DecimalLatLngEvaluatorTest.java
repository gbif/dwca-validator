package org.gbif.dwc.validator.evaluator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.config.ArchiveValidatorConfig;
import org.gbif.dwc.validator.evaluator.impl.DecimalLatLngEvaluator;
import org.gbif.dwc.validator.evaluator.impl.DecimalLatLngEvaluator.DecimalLatLngEvaluatorBuilder;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class DecimalLatLngEvaluatorTest {

  private Record buildMockRecord(String id, String lat, String lng) {
    // create a mock Record
    ArchiveField idField = new ArchiveField(0, DwcTerm.occurrenceID, null, DataType.string);
    ArchiveField latitudeField = new ArchiveField(1, DwcTerm.decimalLatitude, null, DataType.string);
    ArchiveField longitudeField = new ArchiveField(2, DwcTerm.decimalLongitude, null, DataType.string);
    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(latitudeField);
    fieldList.add(longitudeField);
    RecordImpl testRecord = new RecordImpl(idField, fieldList, "rowType", false);
    testRecord.setRow(new String[] {id, lat, lng});
    return testRecord;
  }

  @Test
  public void testInvalidCoordinates() {
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    DecimalLatLngEvaluator decimalLatLngEvaluator = DecimalLatLngEvaluatorBuilder.create().build();

    decimalLatLngEvaluator.handleEval(buildMockRecord("1", "a", "40"), resultAccumulator);
    decimalLatLngEvaluator.handleEval(buildMockRecord("2", "70", "b"), resultAccumulator);

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_VALUE));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "2",
      ContentValidationType.RECORD_CONTENT_VALUE));
  }

  @Test
  public void testOutOfBoundsCoordinates() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    DecimalLatLngEvaluator decimalLatLngEvaluator = DecimalLatLngEvaluatorBuilder.create().build();

    decimalLatLngEvaluator.handleEval(buildMockRecord("1", "91", "120"), resultAccumulator);
    decimalLatLngEvaluator.handleEval(buildMockRecord("2", "91", "40"), resultAccumulator);
    decimalLatLngEvaluator.handleEval(buildMockRecord("3", "70", "181"), resultAccumulator);

    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_BOUNDS));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "2",
      ContentValidationType.RECORD_CONTENT_BOUNDS));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "3",
      ContentValidationType.RECORD_CONTENT_BOUNDS));
  }

  @Test
  public void testPossiblyInvalidCoordinates() {
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    DecimalLatLngEvaluator decimalLatLngEvaluator = DecimalLatLngEvaluatorBuilder.create().build();

    decimalLatLngEvaluator.handleEval(buildMockRecord("1", "-120", "40"), resultAccumulator);
    decimalLatLngEvaluator.handleEval(buildMockRecord("2", "0", "0"), resultAccumulator);

    assertTrue(TestEvaluationResultHelper.containsResultMessage(resultAccumulator.getEvaluationResultList(), "1",
      ArchiveValidatorConfig.getLocalizedString("evaluator.decimal_lat_lng.inverted", "-120", "40")));
    assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getEvaluationResultList(), "1",
      ContentValidationType.RECORD_CONTENT_VALUE));

    assertTrue(TestEvaluationResultHelper.containsResultMessage(resultAccumulator.getEvaluationResultList(), "2",
      ArchiveValidatorConfig.getLocalizedString("evaluator.decimal_lat_lng.zero", "0", "0")));
  }

  @Test
  public void testValidCoordinates() {

    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    DecimalLatLngEvaluator decimalLatLngEvaluator = DecimalLatLngEvaluatorBuilder.create().build();

    decimalLatLngEvaluator.handleEval(buildMockRecord("1", "30.001", "40.001"), resultAccumulator);
    decimalLatLngEvaluator.handleEval(buildMockRecord("2", "30", "120"), resultAccumulator);

    assertTrue(resultAccumulator.getEvaluationResultList().isEmpty());
  }

}
