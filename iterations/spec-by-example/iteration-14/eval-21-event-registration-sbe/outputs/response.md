Here are four example tables that pin down the registration rules. Each states one rule; the last one resolves your open stacking question by picking a default and showing the alternatives.

## Assumptions (flag any of these that are wrong)

- **Email is required**, same as name — the spec says format must be valid, which implies presence.
- **Cutoff date is exclusive** — a registration made *on* the cutoff date does **not** get early-bird pricing (only strictly before).
- **Group Size** is an attribute of the whole booking (all attendees in one group registration), not something derived elsewhere.
- **Stacking**: I've defaulted to **best-of / non-stacking** — when both early-bird and group discount apply, the attendee gets the *higher* of the two, not both combined. This is the part you flagged as unresolved, so treat it as a proposal, not a decision — see the last table.

---

### Validates a Registration

| Scenario | Name | Email | Dietary Requirements | Accessibility Needs | Valid? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Complete registration | Ada Lovelace | ada@example.com | | | yes | |
| Optional needs provided | Grace Hopper | grace@example.com | Vegetarian | Wheelchair access | yes | |
| Missing name | | ada@example.com | | | no | Name is required |
| Missing email | Ada Lovelace | | | | no | Email is required |
| Malformed email | Ada Lovelace | {ada.example.com, ada@, @example.com} | | | no | Email is malformed |

Dietary requirements and accessibility needs never affect validity — shown by the first two rows (absent vs. present, both valid).

### Applies Early-Bird Pricing by Registration Date

| Scenario | Registration Date | Cutoff Date (Policy) | Early-Bird Discount? |
|---|---|---|---|
| Registered before the cutoff | {2026-01-10, 2026-02-28} | 2026-03-01 | 20% |
| Registered on the cutoff date | 2026-03-01 | 2026-03-01 | 0% |
| Registered after the cutoff | {2026-03-02, 2026-05-01} | 2026-03-01 | 0% |

The value sets include the boundary date itself (Feb 28, the day before; Mar 2, the day after), so both sides of the cutoff are exercised.

### Applies Group Pricing by Group Size

| Scenario | Group Size | Threshold (Policy) | Group Discount? |
|---|---|---|---|
| Below the group discount threshold | 4 | 5 | 0% |
| At or above the group discount threshold | {5, 6, 50} | 5 | 15% |

### Combines Early-Bird and Group Discounts When Both Apply

| Scenario | Registration Timing | Group Size | Early-Bird Discount? | Group Discount? | Applied Discount? |
|---|---|---|---|---|---|
| Early-bird only | Before cutoff | 4 | 20% | 0% | 20% |
| Group only | On/after cutoff | 5 | 0% | 15% | 15% |
| Neither applies | On/after cutoff | 4 | 0% | 0% | 0% |
| **Both apply** | Before cutoff | 5 | 20% | 15% | **20%** (assumed: best-of) |

**Only the last row is genuinely open** — the other three follow directly from the two rules above. For that row, here's what each stacking policy would produce, so you can pick:

| Policy | Applied Discount? | Notes |
|---|---|---|
| Best-of (assumed default) | 20% | Simplest; no double-dipping |
| Additive stacking | 35% | 20% + 15%, straightforward but can erode margin fast |
| Multiplicative stacking | 32% | (1 − 0.20) × (1 − 0.15) = 0.68 → 32% off |
| Group takes priority | 15% | Group discount overrides early-bird entirely |

My recommendation is best-of, since it's the least surprising to implement and reason about, but this is a pricing/business call, not a technical one — worth confirming with whoever owns pricing before coding it in.

**Still open, lower priority:**
- Exact email format check (simple `local@domain.tld` regex vs. RFC 5322 strictness) — the malformed-email row assumes any missing/misplaced `@` or missing TLD is rejected, but doesn't pin the exact validation library/pattern.
- Whether "before the cutoff" should be inclusive of the cutoff date itself — I assumed exclusive; confirm this matches intent.