```swift
@TableTest("""
    Body Weight (kg) | Renal Function | Dose Band? | Daily Dose (mg)?
    70               | normal         | standard   | 500
    70               | impaired       | reduced    | 250
    40               | normal         | low        | 300
    """)
func setsTheDailyDose(bodyWeight: Int, renalFunction: RenalFunction, doseBand: DoseBand, dailyDose: Int) { ... }
```

`Dose Band?` is not strictly necessary, but it lets a reader trace weight + renal function -> band ->
daily dose, and a failure shows which step broke.
