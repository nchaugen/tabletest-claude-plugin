```swift
// Crew size was held at 2 in every case, so the test silently claimed a two-pilot rule.
@Test("Fitness to fly by duty hours and crew size", arguments: [
    (dutyHours: 12, crewSize: 2, maxDuty: 13, fit: true),
    (dutyHours: 14, crewSize: 2, maxDuty: 13, fit: false),
    (dutyHours: 14, crewSize: 3, maxDuty: 17, fit: true),
])
func fitnessToFly(dutyHours: Int, crewSize: Int, maxDuty: Int, fit: Bool) { ... }
```
