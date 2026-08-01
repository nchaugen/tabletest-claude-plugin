**Good** — every expectation traceable to the row's own inputs:
```
Scenario                    | Body Weight (kg) | Dose Per Kg (mg) | Daily Dose (mg)?
Standard adult              | 70               | 5                | 350
Paediatric                  | 20               | 5                | 100
```

**Bad** — `standard` and `reduced` appear nowhere in the row:
```
Scenario                    | Heavy | Impaired | Dose?
Normal function             | true  | false    | standard
Impaired function           | true  | true     | reduced
```
