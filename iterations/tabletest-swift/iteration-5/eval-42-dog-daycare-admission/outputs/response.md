I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift`, and `swift test` passes.

The old row "At the youngest age | 12 | 1 | admitted" contradicted the new rule, so I replaced it. The table now has these rows:

| Scenario | Age (weeks) | Vaccinations | Decision |
|---|---|---|---|
| A week under the youngest age | 11 | {1, 2} | tooYoung |
| Puppy with one vaccination | {12, 15} | 1 | needsVaccination |
| Puppy with a second vaccination | {12, 15} | 2 | admitted |
| At the age a puppy becomes a dog | 16 | 1 | admitted |
| Old enough, never vaccinated | {12, 16, 52} | 0 | needsVaccination |

- **Boundaries:** both edges of the 12–15 week puppy range are covered (12 and 15). Week 16 is the first age where one vaccination is enough.
- **Value sets:** ages 12 and 15 sit in one `{12, 15}` set because they give the same result for a given vaccination count.
- **Title:** I changed it to "…old enough and vaccinated for its age" to match the new rule. I left the function name alone.

I assumed "needs a second vaccination" means two or more, which is what `Admission.swift` does with `>=`. I didn't add a row for three or more vaccinations because it wouldn't show anything new.

I only added rows to the existing table. I didn't add columns, so I didn't read the full table-design reference.