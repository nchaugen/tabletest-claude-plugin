```
Scenario                  | Items                     | Streams?
Mixed recyclables         | [paper, card]             | [recycling: [paper, card]]
Recyclable and residual   | [paper, foil]             | [recycling: [paper], landfill: [foil]]
```

`Streams?` stays a native map. Flattening it to `"recycling:[paper]"` would test the formatter.
