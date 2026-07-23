Multiple @TableTest methods with separate tables for: discount ladder (deriving tiers from the
'every fifth trip' rule, using value sets per tier), traveller eligibility (adults/seniors vs
children), and 30-day rolling window counting (with boundary testing).

## The dimensions

This eval scores how well a solution picks the domain apart and covers each dimension with its
own table — not how elegant any single table is.

| Dimension | Governs | Notes |
|---|---|---|
| Traveller category | which discount scheme applies | ADULT and SENIOR are **one** value of this dimension, not two — they follow identical rules. CHILD is a separate scheme. |
| Ticket type | whether a purchase participates at all | Only single tickets receive a Reis discount, and only single tickets count toward the ladder. Period tickets (weekly, monthly) do neither. |
| Zone | ticket **price** only | Never affects the discount percentage, and never affects whether a purchase counts. A Z1 single ticket counts exactly as a Z3 single ticket does. |
| Travel count | position on the ladder | Adult/senior single tickets purchased in the trailing 30 days, measured at the time of the new purchase. **The ticket being bought counts toward its own discount** — four prior single tickets plus this one is a count of five, and this purchase is charged at 5%. |
| Discount ladder | count → percentage | 5% per fifth ticket from ticket 5 (5→5%, 10→10%, … 40→40%), then flat 40% for every higher count. Nine rungs including 0%. |

**Child purchases never enter the count at all.** A child ticket is always flat 20%, so it neither
builds a count of its own nor contributes to the count of the adult buying it. The prompt does not
state this, so it is not assertable — see "Out of scope, but foreseeable".

## Independence claims that must be exercised, not narrated

Three claims are the substance of the feature, and each must be visible in rows rather than
stated in an `@Description`:

- **zone ⟂ discount percentage** — the same category and count yield the same percentage in
  every zone.
- **zone ⟂ counting** — a past purchase counts toward the window whatever its zone. A solution
  that hardcodes every history entry to one zone has claimed this without testing it.
- **adult ≡ senior** — one value set, not two rows with identical outcomes.

## The rolling window

- The boundary is **measured at the time of purchase**, so it is not a whole-day comparison: a
  purchase 30 days and one hour before the new purchase is outside the window, one 29 days and
  23 hours before it is inside.
- Exactly 30 days is included; just past 30 days is excluded.
- Times are expressed **relative to the purchase being counted** ("29 days ago", `SINGLE@29`, a
  `Days ago` column). No literal timestamp should be needed to read a table — including one
  parked in a converter class or a test field, which is the same defect one indirection away.

## Good decomposition

Three `@TableTest` methods: count derivation from raw history, the ladder from a count, and
eligibility/dispatch by traveller category. The tier→percentage mapping appears in exactly one
of them.

**Ticket price is out of scope.** This eval scores derivation of the discount percentage. A
price-application table (base price × discount → final price) is neither expected nor rewarded;
it is not a coverage gap when absent, and when present it must not re-enumerate the tier mapping.

## Out of scope, but foreseeable

Two rules a solution cannot derive from the prompt. Omitting either is not penalised; inventing a
rule that contradicts either is.

- When several adult single tickets are bought in one purchase (buying for a companion), only one
  counts toward the ladder.
- Child single-ticket purchases never count toward anyone's travel count.

## Judging

Score dimension separation and the three independence claims first. Choices *within* a table —
value sets versus enumerated boundary rows, column ordering, scenario wording — are legibility
preferences. They are real, and some assertions encode them, but a solution that separates the
dimensions correctly and enumerates a tier ladder is not a worse decomposition than one that
compresses it.
