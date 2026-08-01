```csharp
public static TheoryData<string, Adjustments, int> DoseCases => new() {
    { "no adjustment",              new(),                                    500 },
    { "renal impairment",           new(Renal: Severe),                       250 },
    { "low body weight",            new(WeightKg: 20),                        200 },
    { "interacting drug",           new(Interaction: true),                   400 },
    { "renal and interacting drug", new(Renal: Severe, Interaction: true),    200 },
};
```

One family, one theory. Three separate theories fixing the same setup would be the over-split.
