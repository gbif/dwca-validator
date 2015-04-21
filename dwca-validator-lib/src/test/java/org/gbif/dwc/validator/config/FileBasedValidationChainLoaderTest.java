package org.gbif.dwc.validator.config;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.chain.EvaluatorChain;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.accumulator.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;
import org.gbif.dwca.io.ArchiveField;
import org.gbif.dwca.io.ArchiveField.DataType;
import org.gbif.dwca.record.Record;
import org.gbif.dwca.record.RecordImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test a validation chain built from a configuration file.
 *
 * @author cgendreau
 */
public class FileBasedValidationChainLoaderTest {

  private Record buildMockRecord(String id) {
    // create a mock Record
    ArchiveField idField = new ArchiveField(0, DwcTerm.occurrenceID, null, DataType.string);
    ArchiveField scientificNameField = new ArchiveField(1, DwcTerm.scientificName, null, DataType.string);
    ArchiveField eventDateField = new ArchiveField(2, DwcTerm.eventDate, null, DataType.string);
    ArchiveField decimalLatitudeField = new ArchiveField(3, DwcTerm.decimalLatitude, null, DataType.string);
    ArchiveField decimalLongitudeField = new ArchiveField(4, DwcTerm.decimalLongitude, null, DataType.string);
    ArchiveField genusField = new ArchiveField(5, DwcTerm.genus, null, DataType.string);

    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(scientificNameField);
    fieldList.add(eventDateField);
    fieldList.add(decimalLatitudeField);
    fieldList.add(decimalLongitudeField);
    fieldList.add(genusField);
    RecordImpl testRecord = new RecordImpl(idField, fieldList, DwcTerm.Occurrence, false, false);
    testRecord.setRow(new String[] {id, "gulo\tgulo", "10-07-2014", "2.3", "a", "monodon"});
    return testRecord;
  }

  @Test
  public void testValidationChainFromFile() {
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File testFile = new File(this.getClass().getResource("/evaluator/fileBasedValidationChain.yaml").toURI());
      FileBasedValidationChainLoader fbValidationChainLoader = new FileBasedValidationChainLoader();
      EvaluatorChain chainHead = null;
      try {
        chainHead = fbValidationChainLoader.buildValidationChainFromYamlFile(testFile);
      } catch (IOException e) {
        e.printStackTrace();
        fail();
      }

      Record testRecord = buildMockRecord("2");
      Record testRecordDuplicate = buildMockRecord("2");
      try {
        chainHead.evaluateRecord(testRecord, EvaluationContext.CORE, resultAccumulator);
        chainHead.evaluateRecord(testRecordDuplicate, EvaluationContext.CORE, resultAccumulator);

        chainHead.postIterate(resultAccumulator);

        chainHead.cleanup();
      } catch (ResultAccumulationException e) {
        e.printStackTrace();
        fail();
      } catch (IOException e) {
        e.printStackTrace();
        fail();
      }

// assertTrue(TestEvaluationResultHelper.containsResultMessage(
// resultAccumulator.getValidationResultList(),
// "2",
// ValidatorConfig.getLocalizedString("evaluator.value_evaluator", DwcTerm.eventDate,
// ValidatorConfig.getLocalizedString("rule.date.non_ISO", "10-07-2014"))));
//
// assertTrue(TestEvaluationResultHelper.containsResultMessage(
// resultAccumulator.getValidationResultList(),
// "2",
// ValidatorConfig.getLocalizedString("evaluator.record_completion", DwcTerm.country,
// ValidatorConfig.getLocalizedString("rule.blank_value"))));

      assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "2",
        ContentValidationType.FIELD_UNIQUENESS));

      System.out.println(resultAccumulator.getValidationResultList());

    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }

  }
}
