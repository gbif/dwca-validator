package org.gbif.dwc.validator.config;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.validator.Evaluators;
import org.gbif.dwc.validator.annotation.AnnotationLoader;
import org.gbif.dwc.validator.evaluator.RecordEvaluator;
import org.gbif.dwc.validator.evaluator.RecordEvaluatorBuilder;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorBuilderKey;
import org.gbif.dwc.validator.evaluator.annotation.RecordEvaluatorConfigurationKey;
import org.gbif.dwc.validator.evaluator.chain.ChainableRecordEvaluator;
import org.gbif.dwc.validator.rule.EvaluationRuleBuilder;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleBuilderKey;
import org.gbif.dwc.validator.rule.annotation.EvaluationRuleConfigurationKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  private static final String EVALUATOR_BUILDERS_SECTION = "evaluators";

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

      List<RecordEvaluator> recordEvaluatorList = (List<RecordEvaluator>) conf.get(EVALUATOR_BUILDERS_SECTION);

      ios.close();

      chainHead = Evaluators.buildFromEvaluatorList(recordEvaluatorList);

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
    // Get all annotated EvaluationRuleBuilder implementations
    Set<Class<EvaluationRuleBuilder>> evaluationRuleBuilderClasses =
      AnnotationLoader.getAnnotatedClasses("", EvaluationRuleBuilderKey.class, EvaluationRuleBuilder.class);

    // Get all annotated EvaluationRuleBuilder implementations
    Set<Class<RecordEvaluatorBuilder>> evaluatorBuilderClasses =
      AnnotationLoader.getAnnotatedClasses("", RecordEvaluatorBuilderKey.class, RecordEvaluatorBuilder.class);

    Constructor yamlConstructor = new ValidatorYamlContructor(evaluationRuleBuilderClasses, evaluatorBuilderClasses);

    // Register an alias for DwcTerm class
    yamlConstructor.addTypeDescription(new TypeDescription(DwcTerm.class, "!dwcTerm"));

    // Register aliases on class name for @EvaluationRuleConfigurationKey
    Set<Class<?>> evaluationRuleConfigurationClasses =
      AnnotationLoader.getAnnotatedClasses("", EvaluationRuleConfigurationKey.class);
    registerAliases(evaluationRuleConfigurationClasses, yamlConstructor);

    // Register aliases on class name for RecordEvaluatorConfigurationKey
    Set<Class<?>> evaluatorConfigurationClasses =
      AnnotationLoader.getAnnotatedClasses("", RecordEvaluatorConfigurationKey.class);
    registerAliases(evaluatorConfigurationClasses, yamlConstructor);

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
