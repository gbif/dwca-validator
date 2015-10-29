# dwca-validator

*This project is still in developement and has not been released yet.*

The dwca-validator library provides:
 * Base for validating a DarwinCore archive file from the file system or a URL
 * Command-line interface (CLI) producing validation results in CSV or JSON file
 * Predefined and configurable validation criterion.

## To build the project
```
mvn clean install
```

## Usage

### From command-line
See the [wiki](https://github.com/gbif/dwca-validator/wiki/CommandLine) for all the information.

### As library

Example of a validation chain that ensures `scientificName` is provided and does not contain invalid characters (invisible characters except space)
```java
try {
  FileEvaluator fileEvaluator = fileEvaluator = Evaluators.builder()
    .with(RecordCriteria.required(DwcTerm.scientificName).onRowType(DwcTerm.Taxon))
    .with(RecordCriteria.checkForInvalidCharacter(DwcTerm.scientificName))
    .build();

    //example only, InMemoryResultAccumulator should not be used in production
    InMemoryResultAccumulator resultAccumulator = new InMemoryResultAccumulator();
    File dwcaFile = new File("/tmp/dwca.zip");
    fileEvaluator.evaluateFile(dwcaFile, resultAccumulator);
    for( ValidationResult vr : resultAccumulator.getValidationResultList()){
      System.out.println(vr);
    }
} catch (CriterionBuilderException e) {
  e.printStackTrace();
}
```