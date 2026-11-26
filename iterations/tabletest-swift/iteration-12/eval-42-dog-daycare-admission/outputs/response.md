I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule, and `swift test` passes (12 cases for that test).

- **Stale row:** The old row "At the youngest age | 12 weeks | 1 vaccination | admitted" contradicts the new rule. A 12-week-old with one vaccination now gets `needsVaccination`, so I replaced it.
- **New rows:**
  - 12 weeks: one vaccination gives `needsVaccination`, two give `admitted`.
  - 15 weeks, the last puppy week: same two outcomes.
  - 16 weeks, the first week past puppyhood: one vaccination gives `admitted`.
  - 52 weeks, an adult: one vaccination gives `admitted`.
- **Kept rows:** The 11-week `tooYoung` row and the never-vaccinated row are still there. I widened the never-vaccinated ages to `{12, 15, 16, 52}` so it covers both sides of the 16-week boundary.

I left the play-group and fee tests alone because the rule doesn't affect them.