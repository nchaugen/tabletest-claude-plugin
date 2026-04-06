There are two distinct concerns here — **registration validation** and **discount calculation**. I'll build a table for each, with the stacking question surfaced as an open cell for you to decide.

---

## Table 1: Registration Validation

| Scenario | Name | Email | Valid? | Error Message? |
|---|---|---|---|---|
| Typical valid registration | Alice Smith | alice@example.com | yes | |
| Missing name | | alice@example.com | no | Name is required |
| Missing email | Alice Smith | | no | Email is required |
| Invalid email format | Alice Smith | not-an-email | no | Invalid email format |
| Both name and email missing | | | no | ? |
| Valid with optional fields provided | Alice Smith | alice@example.com | yes | |
| Valid with no optional fields | Alice Smith | alice@example.com | yes | |

**Open question on the last input row:** optional fields (dietary requirements, accessibility needs) don't affect validity — I've expressed that by not including them as columns, since they pass through regardless. Does that match your intent?

**Open question — row 5 (both missing):** Does the system return one error or both? That determines whether `Error Message?` is a single string or a list.

---

## Table 2: Discount Calculation

| Scenario | Registration Date | Cutoff Date | Group Size | Base Price | Discount? | Final Price? |
|---|---|---|---|---|---|---|
| No discounts apply | 2026-05-15 | 2026-05-01 | 1 | 100.00 | none | 100.00 |
| Early-bird only | 2026-04-15 | 2026-05-01 | 1 | 100.00 | 20% | 80.00 |
| Group only | 2026-05-15 | 2026-05-01 | 5 | 100.00 | 15% | 85.00 |
| Group just under threshold | 2026-05-15 | 2026-05-01 | 4 | 100.00 | none | 100.00 |
| Group exactly at threshold | 2026-05-15 | 2026-05-01 | 5 | 100.00 | 15% | 85.00 |
| Registered on cutoff date exactly | 2026-05-01 | 2026-05-01 | 1 | 100.00 | ? | ? |
| Early-bird + group (stacking?) | 2026-04-15 | 2026-05-01 | 5 | 100.00 | ? | ? |

---

## Open Questions to Resolve

**1. Do the discounts stack? (Row 7)**
This is your main unresolved rule. Three common options:

| Option | Discount applied | Final Price (on 100.00) |
|---|---|---|
| Additive | 20% + 15% = 35% | 65.00 |
| Sequential (early-bird first) | 100 → 80 → 68 | 68.00 |
| Best-of (take the higher) | 20% only | 80.00 |

Which of these matches the business intent?

**2. Does "before the cutoff" include the cutoff date itself? (Row 6)**
Is registration on 2026-05-01 early-bird or not? "Before the cutoff" is strict (no), but "by the cutoff" would include it (yes). This is a common boundary misunderstanding worth pinning down now.

**3. Multiple validation errors (Row 5 of Table 1)**
Single error message (first failure wins) or all errors returned together?

---

Once you answer the stacking question, I can fill in row 7 and we'll have a complete spec ready to hand off to `/tabletest`.