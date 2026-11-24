I updated the admission table in `Tests/DaycareTests/AdmissionTests.swift`, and `swift test` passes.

**What was wrong.** The old row "At the youngest age | 12 | 1 vaccination | admitted" contradicted the new rule. A 12-week-old with one vaccination now needs a second.

**Table changes**
- The 12-week row with one vaccination now expects `needsVaccination`. A new row adds the second vaccination and expects `admitted`.
- Two new rows cover the last week under the limit, 15 weeks. One vaccination gives `needsVaccination` and two give `admitted`.
- A new row covers the first week past the limit, 16 weeks. One vaccination gives `admitted`.
- The "never vaccinated" row now uses the ages {12, 16, 52}, so it covers the puppy and non-puppy sides. It still expects `needsVaccination`.
- The doc comment now states the rule: two vaccinations under 16 weeks, one from 16 weeks.

I left the 11-week row as it was, and I didn't touch the play-group and fee tables. Admission's play-group and fee code hasn't changed.

I ran the table formatter, and it only realigned the columns. The admission test now runs 10 cases and all pass, along with the other two tests.

I assumed the new rule applies only to the admission decision, and that "under 16 weeks" excludes 16 itself. That matches `Admission.decision(for:)`.