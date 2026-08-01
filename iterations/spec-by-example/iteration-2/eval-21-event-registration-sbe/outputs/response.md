# Event Registration — Example Tables

Two separate concerns here: **validity of a registration** and **price calculation**. Keeping them in separate tables (per the skill's guidance) avoids mixing "is this registration acceptable" with "what does it cost."

## Table 1: Registration Validity

| Scenario | Name | Email | Dietary Requirements | Accessibility Needs | Valid? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Complete registration | Jane Doe | jane@example.com | Vegetarian | Wheelchair access | yes | |
| No special requirements given | John Smith | john@example.com | | | yes | |
| Missing name, regardless of other fields | | valid@example.com | {Vegetarian, (none)} | {Wheelchair access, (none)} | no | Name is required |
| Whitespace-only name | "   " | valid@example.com | | | no | Name is required |
| Missing email | Jane Doe | | | | no | Invalid email format |
| Email missing @ symbol | Jane Doe | jane.example.com | | | no | Invalid email format |
| Email missing domain | Jane Doe | jane@ | | | no | Invalid email format |
| Email without a top-level domain | Jane Doe | jane@example | | | no | Invalid email format |
| Both name and email invalid | | not-an-email | | | no | Name is required |

## Table 2: Registration Price Calculation

Assumption: flat base price of **EUR 100.00** per attendee (no tiers mentioned yet). Example cutoff date: **2026-06-01**.

| Scenario | Registration Date | Early-Bird Cutoff (Policy) | Group Size | Group Threshold (Policy) | Base Price | Early-Bird Applies? | Group Discount Applies? | Final Price? |
|---|---|---|---|---|---|---|---|---|
| Standard registration, no discount | 2026-07-01 | 2026-06-01 | 1 | 5 | 100.00 | no | no | 100.00 |
| Early-bird individual registration | 2026-05-15 | 2026-06-01 | 1 | 5 | 100.00 | yes | no | 80.00 |
| Registering on the cutoff date itself | 2026-06-01 | 2026-06-01 | 1 | 5 | 100.00 | no | no | 100.00 |
| One day before cutoff | 2026-05-31 | 2026-06-01 | 1 | 5 | 100.00 | yes | no | 80.00 |
| Group meets or exceeds threshold, registered late | 2026-07-01 | 2026-06-01 | {5, 20} | 5 | 100.00 | no | yes | 85.00 |
| Group just below threshold | 2026-07-01 | 2026-06-01 | 4 | 5 | 100.00 | no | no | 100.00 |
| Both early-bird and group discount conditions met | 2026-05-15 | 2026-06-01 | 8 | 5 | 100.00 | yes | yes | **?** (see below) |

### Open Question: Discount Stacking

Since this was flagged as unresolved, here's the same "both apply" scenario computed three ways:

| Option | Rule | Final Price? |
|---|---|---|
| A — Best discount only (no stacking) | Take the larger single discount (20% > 15%) | 80.00 |
| B — Additive stacking | 20% + 15% = 35% off | 65.00 |
| C — Multiplicative stacking | 100 × 0.80 × 0.85 | 68.00 |

**Assumption I'll proceed with: Option A (best-of, non-stacking).** It's the simplest to reason about and test, matches common ticketing-platform practice, and avoids compounding discounts to a point where margins collapse for large early groups. Flag this for confirmation before implementation — it changes real revenue, so worth a deliberate sign-off rather than a default.

## Other Assumptions Made (non-interactive, no follow-up possible)

- **Cutoff boundary is exclusive**: "before the cutoff" means the cutoff day itself gets standard pricing, not early-bird. This is a classic off-by-one source — worth confirming.
- **Group size** = attendees registered together in one group booking/order, not a running total across separate registrations.
- **Whitespace-only name** is treated as missing (trimmed before validation).
- **Email format** requires local-part @ domain . tld — an address without a TLD (`jane@example`) is rejected. Flag if you need to support intranet-style addresses.
- **Error precedence**: when both name and email are invalid, only "Name is required" is reported. If you want all validation errors surfaced at once, `Rejection Reason?` becomes a list instead of a single value.

## Still Open

1. **Stacking policy** — needs a real decision (see above).
2. **Cutoff inclusivity** — confirm exclusive interpretation.
3. **Multi-field errors** — first error only, or all at once?
4. **Email strictness** — is a TLD-less address ever valid in your domain?

Once stacking and the cutoff boundary are confirmed, these tables translate directly into `@TableTest`/parametrized tests via `/tabletest` or `/table-driven-testing`.