```
Scenario                  | Bin Before                | Action              | Bin After?                | Message?
Accept a labelled item    | [EMPTY]                   | deposit cardboard   | [CARDBOARD: 1]            | Accepted
Fill to the bulk limit    | [CARDBOARD: 1]            | deposit cardboard   | [CARDBOARD: 2]            | Accepted
Reject a mismatched item  | [CARDBOARD: 1]            | deposit solvent     | [CARDBOARD: 1]            | Wrong stream
```
