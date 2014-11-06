package org.gbif.dwc.validator.config;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.Evaluators;
import org.gbif.dwc.validator.annotation.AnnotationLoader;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Class allowing to build a validation chain from a configuration file in yaml.
 * 
 * @author cgendreau
 */
public class FileBasedValidationChainLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedValidationChainLoader.class);

  private static final String EVALUATOR_BUILDERS_SECTION = "evaluatorBuilders";

  /**
   * Only works for RecordEvaluator for now.
   * 
   * @param configFilePath
   * @return head of the validation chain or null if the chain can not be created
   */
  @SuppressWarnings("unchecked")
  public ChainableRecordEvaluator buildValidationChainFromYamlFile(String configFilePath) {
    InputStream ios = null;
    ChainableRecordEvaluator chainHead = null;

    try {
      ios = new FileInputStream(new File(configFilePath));

      Yaml yaml = new Yaml(buildYamlContructor());

      // configuration file is organized as sections
      Map<String, Object> conf = (Map<String, Object>) yaml.load(ios);

      Evaluators validator = Evaluators.builder();

      List<RecordEvaluatorBuilder> recordEvaluatorBuilderList =
        (List<RecordEvaluatorBuilder>) conf.get(EVALUATOR_BUILDERS_SECTION);
      for (RecordEvaluatorBuilder currRecordEvaluatorBuilder : recordEvaluatorBuilderList) {
        validator.with(currRecordEvaluatorBuilder);
      }

      ios.close();

      chainHead = validator.buildChain();

    } catch (FileNotFoundException e) {
      LOGGER.error("Cant load file " + configFilePath, e);
    } catch (IOException e) {
      LOGGER.error("Cant load file " + configFilePath, e);
    } finally {
      IOUtils.closeQuietly(ios);
    }

    return chainHead;
  }


  /**
   * Build a yaml configured Constructor object.
   * 
   * @return
   */
  private Constructor buildYamlContructor() {
    Constructor yamlConstructor = new Constructor();
    yamlConstructor.addTypeDescription(new TypeDescription(DwcTerm.class, "!dwcTerm"));

    Map<Class<?>, RecordEvaluatorConfigurationKey> evaluatorConfigurationClasses =
      AnnotationLoader.getClassAnnotation("", RecordEvaluatorConfigurationKey.class);
    registerAliases(evaluatorConfigurationClasses.keySet(), yamlConstructor);

    Map<Class<?>, RecordEvaluatorBuilderKey> evaluatorBuilderClasses =
      AnnotationLoader.getClassAnnotation("", RecordEvaluatorBuilderKey.class);
    registerAliases(evaluatorBuilderClasses.keySet(), yamlConstructor);

    return yamlConstructor;
  }

  /**
   * Register aliases based on Class name.
   * 
   * @param class list
   * @param yamlConstructor
   */
  private void registerAliases(Collection<Class<?>> classList, Constructor yamlConstructor) {
    for (Class<?> currClass : classList) {
      yamlConstructor.addTypeDescription(new TypeDescription(currClass, "!"
        + StringUtils.uncapitalize(currClass.getSimpleName())));
    }
  }

}
