```
Scenario                  | Bin Before     | Action            | Bin After?     | Message?
Accept a labelled item    | [:]            | deposit cardboard | [cardboard: 1] | Accepted
Fill to the bulk limit    | [cardboard: 1] | deposit cardboard | [cardboard: 2] | Accepted
Reject a mismatched item  | [cardboard: 1] | deposit solvent   | [cardboard: 1] | Wrong stream
```
