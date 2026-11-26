I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule. `swift test` passes.

The old row "At the youngest age, 12 weeks, 1 vaccination → admitted" was now wrong, because a 12-week-old puppy needs two. I replaced it with rows that cover both sides of each boundary:

| Age (weeks) | Vaccinations | Decision |
|---|---|---|
| 11 | 1 or 2 | `tooYoung` (unchanged) |
| 12 | 1 | `needsVaccination` |
| 12 | 2 | `admitted` |
| 15 | 1 | `needsVaccination` |
| 15 | 2 | `admitted` |
| 16 | 1 | `admitted` |
| 12 or 52 | 0 | `needsVaccination` (unchanged) |

The table now runs 9 cases, up from 5. The play-group and fee tests didn't need changes.

I assumed "under 16 weeks" means ages 12 to 15 need two vaccinations and 16 and up need one, as `Admission.swift` implements it.