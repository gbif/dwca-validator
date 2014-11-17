package org.gbif.dwc.validator.config;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;
import org.gbif.dwc.validator.result.EvaluationContext;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;
import org.gbif.dwc.validator.result.type.ContentValidationType;

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

    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(scientificNameField);
    fieldList.add(eventDateField);
    fieldList.add(decimalLatitudeField);
    fieldList.add(decimalLongitudeField);
    RecordImpl testRecord = new RecordImpl(idField, fieldList, DwcTerm.Occurrence.qualifiedName(), false);
    testRecord.setRow(new String[] {id, "gulo\tgulo", "10-07-2014", "2.3", "a"});
    return testRecord;
  }

  @Test
  public void testValidationChainFromFile() {
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File testFile = new File(this.getClass().getResource("/evaluator/fileBasedValidationChain.yaml").toURI());
      FileBasedValidationChainLoader fbValidationChainLoader = new FileBasedValidationChainLoader();
      ChainableRecordEvaluator chainHead = null;
      try {
        chainHead = fbValidationChainLoader.buildValidationChainFromYamlFile(testFile);
      } catch (IOException e) {
        e.printStackTrace();
        fail();
      }

      Record testRecord = buildMockRecord("2");
      Record testRecordDeplicate = buildMockRecord("2");
      chainHead.doEval(testRecord, EvaluationContext.CORE, resultAccumulator);
      chainHead.doEval(testRecordDeplicate, EvaluationContext.CORE, resultAccumulator);

      chainHead.postIterate(resultAccumulator);
      chainHead.cleanup();

      assertTrue(TestEvaluationResultHelper.containsResultMessage(
        resultAccumulator.getValidationResultList(),
        "2",
        ValidatorConfig.getLocalizedString("evaluator.value_evaluator", DwcTerm.eventDate,
          ValidatorConfig.getLocalizedString("rule.date.non_ISO", "10-07-2014"))));

      assertTrue(TestEvaluationResultHelper.containsResultMessage(
        resultAccumulator.getValidationResultList(),
        "2",
        ValidatorConfig.getLocalizedString("evaluator.record_completion", DwcTerm.country,
          ValidatorConfig.getLocalizedString("rule.blank_value"))));

      assertTrue(TestEvaluationResultHelper.containsValidationType(resultAccumulator.getValidationResultList(), "2",
        ContentValidationType.FIELD_UNIQUENESS));

    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }

  }
}
