```
Scenario                  | Items                     | Streams?
Mixed recyclables         | [paper, card]             | [recycling: [paper, card]]
Recyclable and residual   | [paper, foil]             | [recycling: [paper], landfill: [foil]]
```

`Streams?` stays a native map. Flattening it to `"recycling:[paper]"` would test the formatter.

The `?` marks outputs only — never an input, however yes/no it looks:

| Good (input)         | Bad (input)           | Why bad                       |
|----------------------|-----------------------|-------------------------------|
| `Repeat Donor`       | `Repeat Donor?`       | `?` implies this is an output |
| `Within Rest Period` | `Within Rest Period?` | This is a given condition     |
| `Vent Open`          | `Vent?`               | This is an input state        |
