I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift` for the new rule, and `swift test` passes.

The old row "At the youngest age | 12 | 1 | admitted" contradicted the new rule. A 12-week puppy with one vaccination now gets `needsVaccination`.

The table now covers:
- **Too young:** 11 weeks with 1 or 2 vaccinations gives `tooYoung`.
- **Youngest age:** 12 weeks with 1 vaccination gives `needsVaccination`. With 2 vaccinations it gives `admitted`.
- **Just under 16:** 15 weeks with 1 vaccination gives `needsVaccination`. With 2 vaccinations it gives `admitted`.
- **At 16 weeks:** 1 vaccination is enough, so `admitted`.
- **Adult:** 52 weeks with 1 vaccination gives `admitted`.
- **Never vaccinated:** at 12, 16 and 52 weeks it gives `needsVaccination`. The old row only covered 12 and 52.

I left the play group and fee tests alone. The play group test uses a 52-week dog, so the new rule doesn't affect it.