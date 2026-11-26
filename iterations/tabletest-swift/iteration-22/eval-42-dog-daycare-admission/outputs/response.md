I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule. `swift build --build-tests` and `swift test` both pass, with 10 cases in the admission test.

The old row "At the youngest age" (12 weeks, 1 vaccination, admitted) contradicted the new rule, so I replaced it. The table now covers each side of both boundaries:

| Scenario | Age (weeks) | Vaccinations | Decision |
|---|---|---|---|
| A week under the youngest age | 11 | 1 or 2 | tooYoung |
| At the youngest age, first jab | 12 | 1 | needsVaccination |
| At the youngest age, second jab | 12 | 2 | admitted |
| Last week of puppyhood, first jab | 15 | 1 | needsVaccination |
| Last week of puppyhood, second jab | 15 | 2 | admitted |
| First week past puppyhood | 16 | 1 | admitted |
| Old enough, never vaccinated | 12, 16 or 52 | 0 | needsVaccination |

- **Assumption:** I read "under 16 weeks" as 12–15 weeks inclusive, matching `Admission.swift`: `ageWeeks < 16 ? 2 : 1`. Under 12 weeks is still `tooYoung` whatever the vaccinations.
- **Formatter:** I also ran the table formatter, which realigned the columns.
- **Unchanged:** The play-group and fee tests are unaffected by this rule, so I left them as they were.