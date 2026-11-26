I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule, and `swift test` passes.

The old row "At the youngest age | 12 | 1 | admitted" was wrong under the new rule. A 12-week-old with one vaccination now gets `needsVaccination`.

The table now covers these cases:
- **Under 12 weeks:** 11 weeks with 1 or 2 vaccinations is `tooYoung`.
- **12 weeks:** one vaccination is `needsVaccination`, two is `admitted`.
- **15 weeks, the last puppy week:** one vaccination is `needsVaccination`, two is `admitted`.
- **16 weeks:** one vaccination is `admitted`, so the second dose is no longer required.
- **No vaccinations:** 12, 15, 16 and 52 weeks are all `needsVaccination`.
- **Adult:** 52 weeks with one vaccination is `admitted`.

I only changed the admission table. The play group and fee tests don't depend on the vaccination rule, so I left them alone.

I assumed "under 16 weeks" means a dog needs two vaccinations until it turns 16 weeks old. That matches `Admission.swift`, where `ageWeeks < 16` requires 2.