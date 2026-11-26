```
Scenario                  | Bin Before     | Action            | Bin After?     | Message?
Labelled item, empty bin  | [:]            | deposit cardboard | [cardboard: 1] | Accepted
Fill to the bulk limit    | [cardboard: 1] | deposit cardboard | [cardboard: 2] | Accepted
Mismatched item           | [cardboard: 1] | deposit solvent   | [cardboard: 1] | Wrong stream
```

`deposit cardboard` is a text form that you invent: a `TableTextDecodable` conformance (*Your Own
Text Form* in `SKILL.md`), or a test-local enum when the action builds the fixture.
