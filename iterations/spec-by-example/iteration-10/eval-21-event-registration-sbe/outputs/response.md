Here's a set of example tables that pin down the registration rules. I split them into four separate concerns — validation, early-bird eligibility, group eligibility, and the price calculation where they interact (since that's where your open question about stacking actually lives).

## Biggest open question first: what defines a "group"?

Before the tables make sense, I need to assume an answer to this. Two very different models fit "groups of 5 or more get 15% off":

- **(A) Single group booking** — one registration transaction lists 5+ attendees at once.
- **(B) Linked individual registrations** — 5+ separate registrations share a group code/organization, discount applied retroactively or at checkout.

**Assumption used below: (A), single group booking.** This is simpler to model (one `Registration` with a list of attendees) and avoids needing to track/retract discounts across registrations submitted at different times. If you actually need (B), the validation and pricing tables both need an extra `Group ID`/linking concept — flag this back to me if that's the case.

---

### 1. Validates attendee registration details

| Scenario | Name | Email | Dietary Requirements | Accessibility Needs | Valid? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Complete registration, no optional fields | Ada Lovelace | ada@example.com | | | yes | |
| Complete registration, optional fields given | Ada Lovelace | ada@example.com | {blank, Vegetarian} | {blank, Wheelchair access} | yes | |
| Missing name | | ada@example.com | | | no | Name is required |
| Malformed email | Ada Lovelace | {ada.example.com, ada@, @example.com} | | | no | Email is malformed |
| Missing email | Ada Lovelace | | | | no | Email is required |
| Email with no top-level domain | Ada Lovelace | ada@example | | | ? | ? |

**Open question:** is `ada@example` (no `.com`/TLD) valid? Depends on the email regex/library you use — worth deciding explicitly rather than letting a library default decide it.

**Note on row 2:** the value sets show dietary requirements and accessibility needs don't affect validity either way — both are genuinely optional, not "optional but nudges something."

---

### 2. Determines early-bird eligibility from registration date

| Scenario | Registration Date | Cutoff Date | Early-Bird Eligible? |
|---|---|---|---|
| Well before cutoff | 2026-01-10 | 2026-03-01 | yes |
| Day before cutoff | 2026-02-28 | 2026-03-01 | yes |
| On the cutoff date | 2026-03-01 | 2026-03-01 | no |
| Day after cutoff | 2026-03-02 | 2026-03-01 | no |

**Assumption:** "before the cutoff date" is strictly before — registering *on* the cutoff date does **not** qualify. If you intended the cutoff day itself to be included (common in "ends March 1" promos), that's a one-word rule change (`<` vs `<=`) but changes row 3's answer to `yes`. Worth confirming — it's the kind of off-by-one that annoys customers on the boundary day.

*(Not modeled as a separate table, but worth deciding before coding: is "Cutoff Date" a date or a date+time, and in what timezone? A registration at 11:58pm cutoff-day-local-time vs. UTC can land on different sides of this table.)*

---

### 3. Determines group discount eligibility from group size

| Scenario | Group Size | Min Group Size (Policy) | Group Discount Eligible? |
|---|---|---|---|
| Individual registrant | 1 | 5 | no |
| Below threshold | 4 | 5 | no |
| At threshold | 5 | 5 | yes |
| Above threshold | 8 | 5 | yes |

Clean boundary, nothing ambiguous here given assumption (A) above.

---

### 4. Calculates final registration price from applicable discounts

*Base ticket price held at $100.00 throughout for readability — the percentages scale to any price.*

| Scenario | Early-Bird Eligible? | Group Eligible? | Early-Bird % | Group % | Discount Applied? | Final Price? |
|---|---|---|---|---|---|---|
| Neither discount applies | no | no | 20% | 15% | None | $100.00 |
| Early registration, individual | yes | no | 20% | 15% | Early-bird | $80.00 |
| Group registration, standard timing | no | yes | 20% | 15% | Group | $85.00 |
| Early registration by a group | yes | yes | 20% | 15% | **Best-of: Early-bird (ASSUMED)** | **$80.00 (ASSUMED)** |

**This is your open question, and I picked one of four plausible answers to fill the table — please confirm before I build against it:**

| Policy | Effective Discount | Final Price | Notes |
|---|---|---|---|
| **Best-of (no stacking)** — higher single discount wins | 20% | $80.00 | *Assumption used above.* Simplest to explain to attendees and cheapest for you. |
| Additive stacking | 35% | $65.00 | Most generous; biggest revenue impact. |
| Multiplicative stacking (sequential) | 32% (100 × 0.80 × 0.85) | $68.00 | Common in e-commerce carts; order of application doesn't change the result mathematically, only matters if you round between steps. |
| Group discount takes priority (early-bird forfeited for groups) | 15% | $85.00 | Rare, but some events treat group bookings as a separate pricing track entirely. |

I'd lean toward **best-of** as the default unless there's a business reason to be more generous — it's the least surprising to implement and to explain in a refund/pricing dispute. But this is a pricing/margin call, not a technical one, so it's really yours to make.

---

**Summary of things to confirm before implementation:**
1. Group model: single booking (assumed) vs. linked individual registrations
2. Cutoff date boundary: exclusive (assumed) vs. inclusive, plus timezone/time-of-day handling
3. Discount stacking policy: best-of (assumed) vs. additive vs. multiplicative vs. group-priority
4. Whether TLD-less emails like `ada@example` should validate

Once these are confirmed, table 4's assumed row becomes a real spec and this maps directly onto a `@TableTest` per table via `/tabletest`.