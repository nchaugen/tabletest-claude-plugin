Let me build the example table with you. Based on what you've described, I'll lay out the cases I can pin down confidently, surface the boundary conditions, and call out the income question explicitly as open.

---

## Loan Approval Decision

| Scenario | Applicant Age | Min Credit Score (Policy) | Credit Score | Income Status | Approved? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Standard adult, strong credit, stable income | 40 | 650 | 720 | Stable | yes | |
| Standard adult, credit below threshold | 40 | 650 | 620 | Stable | no | Credit score below 650 |
| Standard adult, unstable income | 40 | 650 | 720 | Unstable | no | Income not stable |
| Standard adult, both conditions fail | 40 | 650 | 580 | Unstable | no | Credit score below 650, income not stable |
| Standard adult, exactly at threshold | 40 | 650 | 650 | Stable | ? | Is 650 itself approved, or must score be strictly above 650? |
| Senior applicant, credit in senior band | 65 | 600 | 630 | Stable | yes | |
| Senior applicant, credit below even senior threshold | 65 | 600 | 580 | Stable | no | Credit score below 600 |
| Senior applicant, credit above standard threshold | 65 | 600 | 700 | Stable | yes | |
| Senior, exactly at senior threshold | 65 | 600 | 600 | Stable | ? | Same boundary question — is 600 itself approved? |
| Age boundary — just below senior | 64 | 650 | 630 | Stable | no | Credit score below 650 |
| Age boundary — just at senior | 65 | 600 | 630 | Stable | yes | |
| Standard adult, income info missing | 40 | 650 | 720 | _missing_ | ? | **Open: reject, or allow if credit is strong?** |
| Senior applicant, income info missing | 67 | 600 | 630 | _missing_ | ? | **Open: same question, does age change the answer?** |
| Senior, unstable income | 67 | 600 | 630 | Unstable | no | Income not stable |

---

## Open Questions

**1. Is the credit score threshold inclusive or exclusive?**
The rule says "above 650" — does a score of exactly 650 qualify? Same question applies to the 600 senior threshold. This is a boundary that commonly causes bugs. You need a definitive answer for both.

**2. What happens when income info is missing?**
This is the biggest unknown. Three plausible interpretations:

- **Reject** — income stability is a required condition; absence means it's not met
- **Refer** — flag for manual review rather than auto-deciding
- **Defer to credit score** — if credit score is strong enough, approve anyway

Do these three behave the same for seniors vs. standard applicants? That gives you two more rows once you decide.

**3. Does "stable income" need its own table?**
"Stable income" may itself have sub-rules (employment type, duration, self-employed thresholds). If so, that's a separate concern and warrants its own table rather than a single column here.

---

Once you resolve questions 1 and 2, the `?` cells can be filled in and this table is ready to hand off to `/tabletest` for implementation.