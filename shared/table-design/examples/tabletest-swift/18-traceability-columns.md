```swift
@TableTest("""
    Scenario                    | Body Weight (kg) | Renal Function | Dose Band? | Daily Dose (mg)?
    Adult, normal function      | 70               | normal         | standard   | 500
    Adult, impaired function    | 70               | impaired       | reduced    | 250
    Low weight, normal function | 40               | normal         | low        | 300
    """)
func setsTheDailyDose(bodyWeight: Int, renalFunction: RenalFunction, doseBand: DoseBand, dailyDose: Int) { ... }
```

`Dose Band?` is not strictly necessary, but it lets a reader trace weight + renal function -> band ->
daily dose, and a failure shows which step broke.
