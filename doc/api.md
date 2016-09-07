Idea for internal GBIF validation API

# POST /record
Post a single record with Terms as post parameters.

Possible response:
```
{
  "results":[{"evaluation_id" : "GI-1",
    "evaluation_type" : "IBE",
    "result" : "FAILED",
    "details" : {"issue_flags":"COUNTRY_CODE_INVALID"}
    }
  ]
}
```
- GI-1: Geographic Interpretation
- IBE: Interpretation Based Evaluation
