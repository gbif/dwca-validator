Idea for internal GBIF validation API

# POST /record
Post a single record with map of Terms as JSON.

Possible request:
```
{
  "occurrence": { "occurrenceId" : "ABCD-1",
  "countryCode":"XYZ"
  },
  "options": {"minimumLevel":"WARNING"}
}
```
In the following example "occurrence" represents the row type.

Possible response:
```
{
  "results":[{"evaluationId" : "GI-1",
    "evaluationType" : "IBE",
    "result" : "FAILED",
    "details" : {"issueFlags":"COUNTRY_CODE_INVALID", "relatedData" : {"countryCode":"XYZ"}}
    }
  ]
}
```
- GI-1: Geographic Interpretation
- IBE: Interpretation Based Evaluation
