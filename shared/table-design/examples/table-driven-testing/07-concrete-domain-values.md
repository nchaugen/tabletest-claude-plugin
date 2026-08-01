```csharp
[Theory]
[InlineData("standard adult", 70, 5, 350)]
[InlineData("paediatric",     20, 5, 100)]
public void DailyDoseByWeight(string scenario, int bodyWeightKg, int dosePerKgMg, int dailyDoseMg)
    => Assert.Equal(dailyDoseMg, Dosing.DailyDose(bodyWeightKg, dosePerKgMg));
```

`350` is traceable to `70 x 5` in its own row; a `"standard"` expectation would not be.
