```swift
// Earns its place: the cap-versus-weight precedence appears in no other test.
@Test("Daily maximum caps the weight-based dose", arguments: [
    (weightKg: 40,  dailyMax: 400, dose: 200),
    (weightKg: 120, dailyMax: 400, dose: 400),
])
func dailyMaximumCaps(weightKg: Int, dailyMax: Int, dose: Int) {
    #expect(Dosing.daily(weightKg: weightKg, dailyMax: dailyMax) == dose)
}
```
