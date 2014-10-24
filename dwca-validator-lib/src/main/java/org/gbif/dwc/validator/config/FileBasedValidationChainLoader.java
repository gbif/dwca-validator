package org.gbif.dwc.validator.config;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.annotation.AnnotationLoader;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilderIF;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorIF;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfiguration;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;
import org.gbif.dwc.validator.evaluator.chain.builder.ChainableRecordEvaluatorBuilderIF;
import org.gbif.dwc.validator.evaluator.chain.builder.DefaultChainableRecordEvaluatorBuilder;

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

  private static final String EVALUATORS_SECTION = "evaluators";
  private static final String EVALUATOR_BUILDERS_SECTION = "evaluatorBuilders";

  private static final String YAML_ALIAS_EVALUTOR_CONFIGURATION_SUFFIX = "_configuration";

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

      List<RecordEvaluatorBuilderIF> recordEvaluatorBuilderList =
        (List<RecordEvaluatorBuilderIF>) conf.get(EVALUATOR_BUILDERS_SECTION);
      for (RecordEvaluatorBuilderIF currRecordEvaluatorBuilder : recordEvaluatorBuilderList) {
        chainBuilder = chainBuilder.linkTo(currRecordEvaluatorBuilder.build());
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


  /**
   * Build a yaml configured Constructor object.
   * 
   * @return
   */
  private Constructor buildYamlContructor() {
    Constructor yamlConstructor = new Constructor();
    yamlConstructor.addTypeDescription(new TypeDescription(DwcTerm.class, "!dwcTerm"));

    Map<Class<?>, RecordEvaluator> evaluatorClasses = AnnotationLoader.getClassAnnotation("", RecordEvaluator.class);

    for (Class<?> currEvaluatorClass : evaluatorClasses.keySet()) {
      registerAliases(currEvaluatorClass, evaluatorClasses.get(currEvaluatorClass).key(), yamlConstructor);
    }
    return yamlConstructor;
  }

  /**
   * Register aliases based on RecordEvaluator class annotations.
   * 
   * @param evaluatorClass
   * @param evaluatorKey
   * @param yamlConstructor
   */
  private void registerAliases(Class<?> evaluatorClass, String evaluatorKey, Constructor yamlConstructor) {

    for (Class<?> currEvaluatorClassInnerClass : evaluatorClass.getDeclaredClasses()) {

      // Build aliases for Evaluator builder using the Evaluator key
      for (Class<?> currIf : currEvaluatorClassInnerClass.getInterfaces()) {
        if (RecordEvaluatorBuilderIF.class.equals(currIf)) {
          yamlConstructor.addTypeDescription(new TypeDescription(currEvaluatorClassInnerClass, "!" + evaluatorKey));
        }
      }

      // Build aliases for Evaluator configuration
      if (currEvaluatorClassInnerClass.getAnnotation(RecordEvaluatorConfiguration.class) != null) {
        yamlConstructor.addTypeDescription(new TypeDescription(currEvaluatorClassInnerClass, "!" + evaluatorKey
          + YAML_ALIAS_EVALUTOR_CONFIGURATION_SUFFIX));
      }
    }
  }

}
