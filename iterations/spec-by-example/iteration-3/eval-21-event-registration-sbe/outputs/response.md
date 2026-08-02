Here's the breakdown as example tables, working from the concerns you listed: registration validation, early-bird eligibility, group eligibility, discount stacking, and price calculation. Each is a separate concern (the "and" test — you can't name any of these without splitting them).

## Assumptions (stated since this is non-interactive)

1. **Email is required**, not just format-checked — a blank email is rejected (spec didn't say this explicitly, but "must be valid format" implies presence).
2. **Cutoff date is exclusive** — "before the cutoff date" means the cutoff day itself does *not* get early-bird pricing.
3. **Group** means attendees registered together in one booking; the 15% applies per attendee in that booking.
4. **Stacking**: I've assumed early-bird and group discounts **stack additively** (20% + 15% = 35%) when both apply, since nothing in the spec suggests otherwise. This is the open question you flagged — I've marked it explicitly below so you can confirm or override it before implementation.

---

### Table 1 — Validates a Registration

| Scenario                              | Name    | Email             | Dietary Requirements | Accessibility Needs | Valid? | Rejection Reason?  |
|----------------------------------------|---------|-------------------|-----------------------|----------------------|--------|---------------------|
| Complete registration, no extras       | Ada     | ada@example.com   |                       |                      | yes    |                     |
| Complete registration, with extras     | Ada     | ada@example.com   | vegetarian            | wheelchair access    | yes    |                     |
| Missing name                           |         | ada@example.com   |                       |                      | no     | Name is required    |
| Whitespace-only name                   | "   "   | ada@example.com   |                       |                      | ?      | Open question: does whitespace-only count as missing? |
| Missing email                          | Ada     |                   |                       |                      | no     | Email is required   |
| Malformed email                        | Ada     | ada.example.com   |                       |                      | no     | Email is malformed  |

### Table 2 — Determines Early-Bird Eligibility from Registration Date

| Scenario                | Registration Date | Cutoff Date | Early-Bird Rate Applies? |
|--------------------------|--------------------|-------------|----------------------------|
| Well before cutoff      | 2026-01-15         | 2026-03-01  | yes                        |
| Day before cutoff       | 2026-02-28         | 2026-03-01  | yes                        |
| On the cutoff date      | 2026-03-01         | 2026-03-01  | no                         |
| Day after cutoff        | 2026-03-02         | 2026-03-01  | no                         |

### Table 3 — Determines Group Discount Eligibility from Group Size

| Scenario           | Group Size | Group Discount Threshold (Policy) | Group Discount Applies? |
|---------------------|------------|-------------------------------------|----------------------------|
| Below threshold    | 4          | 5                                    | no                          |
| At threshold        | 5          | 5                                    | yes                         |
| Above threshold     | 8          | 5                                    | yes                         |

### Table 4 — Determines the Discount Rate from Early-Bird and Group Eligibility

This is the table that answers your stacking question — it's a genuine combining table because neither Table 2 nor Table 3 alone tells you what happens when both are true.

| Scenario                          | Early-Bird Rate Applies? | Group Discount Applies? | Discount Rate? | Open Questions?                          |
|-------------------------------------|-----------------------------|----------------------------|------------------|--------------------------------------------|
| Neither applies                    | no                           | no                          | 0%               |                                              |
| Early-bird only                    | yes                          | no                          | 20%              |                                              |
| Group discount only                | no                           | yes                         | 15%              |                                              |
| Both apply (assumed: stack)        | yes                          | yes                         | 35%              | Confirm: additive stacking, or should it be capped/max-of-the-two/mutually exclusive instead? |

### Table 5 — Calculates the Final Price from Base Price and Discount Rate

| Scenario                    | Base Price | Discount Rate | Final Price? |
|-------------------------------|------------|----------------|----------------|
| No discount                  | 100.00     | 0%             | 100.00         |
| Early-bird only              | 100.00     | 20%            | 80.00          |
| Group discount only          | 100.00     | 15%            | 85.00          |
| Both discounts (stacked)     | 100.00     | 35%            | 65.00          |

---

## Open questions to confirm with a stakeholder before coding

1. **Stacking rule** (Table 4, last row) — I assumed additive (35%). Alternatives worth ruling out: capping at some max discount, taking the better of the two rather than both, or making them mutually exclusive.
2. **Whitespace-only name** (Table 1) — treat as missing, or as a valid (if odd) name?
3. **Cutoff boundary** (Table 2) — confirmed exclusive here; flag if your business actually wants inclusive.

Once you settle the stacking question, this table set is ready to hand to `/tabletest` (Java/Kotlin) or `/table-driven-testing` (other languages) — the columns map straight across, and the `?`-marked cells are exactly the decisions to resolve first.