I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule. `swift build --build-tests` and `swift test` both pass.

The old row "At the youngest age" (12 weeks, 1 vaccination, `admitted`) contradicted the new rule, so I replaced it. The table now has these rows:

| Scenario | Age (weeks) | Vaccinations | Decision |
|---|---|---|---|
| A week under the youngest age | 11 | {1, 2} | tooYoung |
| Puppy with the first vaccination | {12, 15} | 1 | needsVaccination |
| Puppy with the second one | {12, 15} | 2 | admitted |
| Old enough for a single one | {16, 52} | 1 | admitted |
| Old enough, never vaccinated | {12, 52} | 0 | needsVaccination |

- **Boundaries:** the rows with {12, 15} cover both ends of the puppy range, and the {16, 52} row covers the first age that needs only one vaccination, so 15 and 16 weeks are each tested.
- **Cases:** the decision table now runs 10 cases; the weight and fee tables are unchanged.
- **Assumption:** 16 weeks counts as an adult, because `Admission.swift` checks `ageWeeks < 16`.