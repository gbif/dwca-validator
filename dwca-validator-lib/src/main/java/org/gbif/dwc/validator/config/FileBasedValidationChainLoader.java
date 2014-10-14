package org.gbif.dwc.validator.config;

import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluatorBuilderIF;
import org.gbif.dwc.validator.evaluator.chain.DefaultChainableRecordEvaluatorBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Class allowing to build a validation chain from a configuration file in yaml.
 * 
 * @author cgendreau
 */
public class FileBasedValidationChainLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedValidationChainLoader.class);

  private static final String EVALUATORS_SECTION = "evaluators";

  /**
   * Only works for RecordEvaluator for now.
   * 
   * @param configFilePath
   * @return head of the validation chain or null if the chain can not be created
   */
  @SuppressWarnings("unchecked")
  public static ChainableRecordEvaluator buildValidationChainFromYamlFile(String configFilePath) {
    InputStream ios = null;
    ChainableRecordEvaluator chainHead = null;
    try {
      ios = new FileInputStream(new File(configFilePath));

      Yaml yaml = new Yaml();

      // configuration file is organized as sections
      Map<String, Object> conf = (Map<String, Object>) yaml.load(ios);

      List<RecordEvaluatorIF> recordEvaluatorList = (List<RecordEvaluatorIF>) conf.get(EVALUATORS_SECTION);

      // Build chain using DefaultChainableRecordEvaluatorBuilder
      ChainableRecordEvaluatorBuilderIF chainBuilder = null;
      for (RecordEvaluatorIF currRecordEvaluator : recordEvaluatorList) {
        if (chainBuilder == null) {
          chainBuilder = DefaultChainableRecordEvaluatorBuilder.create(currRecordEvaluator);
        } else {
          chainBuilder = chainBuilder.linkTo(currRecordEvaluator);
        }
      }
      ios.close();

      if (chainBuilder != null) {
        chainHead = chainBuilder.build();
      }
    } catch (FileNotFoundException e) {
      LOGGER.error("Cant load file " + configFilePath, e);
    } catch (IOException e) {
      LOGGER.error("Cant load file " + configFilePath, e);
    } finally {
      IOUtils.closeQuietly(ios);
    }

    return chainHead;
  }

}
