```swift
@Test("Daily dose by weight and renal function", arguments: [
    (weightKg: 70, renal: Renal.normal,   band: Band.standard, dose: 500),
    (weightKg: 70, renal: Renal.impaired, band: Band.reduced,  dose: 250),
    (weightKg: 40, renal: Renal.normal,   band: Band.low,      dose: 300),
])
func dailyDose(weightKg: Int, renal: Renal, band: Band, dose: Int) {
    let result = Dosing.resolve(weightKg: weightKg, renal: renal)
    #expect(result.band == band)   // the intermediate, so a failure says which step broke
    #expect(result.dailyDose == dose)
}
```
