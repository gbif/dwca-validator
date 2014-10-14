package org.gbif.dwc.validator.config;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.RecordImpl;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveField.DataType;
import org.gbif.dwc.validator.TestEvaluationResultHelper;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;
import org.gbif.dwc.validator.result.impl.InMemoryResultAccumulator;

import java.io.File;
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

    List<ArchiveField> fieldList = new ArrayList<ArchiveField>();
    fieldList.add(scientificNameField);
    fieldList.add(eventDateField);
    RecordImpl testRecord = new RecordImpl(idField, fieldList, "rowType", false);
    testRecord.setRow(new String[] {id, "gulo\tgulo", "10-07-2014"});
    return testRecord;
  }

  @Test
  public void testValidationChainFromFile() {
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();

    try {
      File testFile = new File(this.getClass().getResource("/evaluator/fileBasedValidationChain.yaml").toURI());
      ChainableRecordEvaluator chainHead =
        FileBasedValidationChainLoader.buildValidationChainFromYamlFile(testFile.getAbsolutePath());

      Record testRecord = buildMockRecord("2");
      chainHead.doEval(testRecord, resultAccumulator);

      assertTrue(TestEvaluationResultHelper.containsResultMessage(resultAccumulator.getEvaluationResultList(), "2",
        ArchiveValidatorConfig.getLocalizedString("rule.date.non_ISO", "10-07-2014")));

    } catch (URISyntaxException e) {
      e.printStackTrace();
      fail();
    }

  }

}
