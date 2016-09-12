    
# Glossary
    
## Parser
A parser is a software component that takes input data (frequently text) and builds a data structure [1].

```
Text : "4" -> Integer : 4
```
    
## Interpreter
An interpreter is a software component that takes structured data as input and put it into a specific context with bouderies and specific meaning.

```
Integer : 4 -> minimumElevationInMeters : 4 meters
```

## Criterion
Criterion represents a rule to be evaluated on one or more data field.

```
minimumElevationInMeters > -5000 && minimumElevationInMeters < 5000
```

## Evaluation
Evaluation represents the evaluation of the criteria to produce a result.
Input data + Criterion produces the evaluation result.

```
boolean result = (4 > -5000 && 4 < 5000);
```
