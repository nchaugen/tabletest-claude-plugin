I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule, and `swift test` passes. The admission test now runs 15 cases, up from 5.

The new rows are:

| Scenario | Age (weeks) | Vaccinations | Decision |
|---|---|---|---|
| Under the youngest age | 11 | 0, 1 or 2 | `tooYoung` |
| Puppy with one vaccination | 12 or 15 | 1 | `needsVaccination` |
| Puppy with two vaccinations | 12 or 15 | 2 | `admitted` |
| Never vaccinated, any age | 12, 15, 16 or 52 | 0 | `needsVaccination` |
| Age where one is enough | 16 or 52 | 1 or 2 | `admitted` |

- **Boundaries:** Ages 12 and 15 are the first and last puppy ages. Age 16 is the first age where one vaccination is enough.
- **Under 12 weeks:** The vaccination count shouldn't change the result, so the 11-week row now covers 0, 1 and 2 vaccinations. The old row covered only 1 and 2.
- **Assumption:** I read "under 16 weeks" as still bound by the existing 12-week minimum, matching `Admission.decision`.

I left the play-group and fee tables unchanged, since the rule change doesn't touch them. I also ran the repo's table formatter, which realigned the columns.