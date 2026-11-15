I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule. `swift test` passes: the admission test now has 14 cases (it was 7), and the play-group and fee tests are unchanged. The formatter also realigned the table columns, which is why the table's columns shifted.

The rows now straddle both boundaries, 11/12 and 15/16:

| Scenario | Age (weeks) | Vaccinations | Decision |
|---|---|---|---|
| Under the youngest age | 11 | 0, 1 or 2 | `tooYoung` |
| Puppy with a single vaccination | 12 or 15 | 1 | `needsVaccination` |
| Puppy with a second vaccination | 12 or 15 | 2 | `admitted` |
| Sixteen weeks or older, vaccinated | 16 or 52 | 1 or 2 | `admitted` |
| Old enough, never vaccinated | 12, 16 or 52 | 0 | `needsVaccination` |

- **Changed outcome:** a 12-week-old with one vaccination used to be admitted and now needs a second one.
- **Added coverage:** a 16-week-old with one vaccination is still admitted, and a second vaccination admits a puppy at 12 and at 15 weeks.
- **Under-12 row:** I added 0 vaccinations, so the row now says age alone decides it.
- **Rename:** I renamed the test to "Admits a dog once it has the vaccinations its age requires", since the old title no longer described the rule.

The 12, 16 and 16+ week thresholds aren't columns in the table. That matches the existing style, but say if you'd rather show them.