Multiple @TableTest methods, each covering one dimension of the domain: which discount scheme a
purchase gets, whether a past purchase counts toward the travel count, the 30-day rolling window,
aggregation of a history into a count, and the count → percentage ladder.

## The dimensions

This eval scores how well a solution picks the domain apart and covers each dimension with its own
table — not how elegant any single table is.

**Traveller category and ticket type answer two different questions.** The same pair of attributes
is read twice: once about the purchase being made, once about each past purchase.

| Question | Inputs | Outcome |
|---|---|---|
| **Which scheme applies to the purchase being made?** | traveller category, ticket type | no discount (period ticket) · flat 20% (child single) · Reis (adult/senior single) |
| **Does this past purchase count toward the travel count?** | traveller category, ticket type, age at purchase time | counts / does not count |

The second is the first plus time: **a past purchase counts if and only if its own scheme would
have been Reis and it falls within the trailing 30 days.**

A solution does not have to name a "scheme" concept to score well — see § Degrees of decomposition.
What it has to do is cover both questions, and not re-derive the category/ticket-type mapping in two
places.

The remaining dimensions are internal to Reis.

| Dimension | Governs | Notes |
|---|---|---|
| Rolling window | whether a past purchase is recent enough | Measured at the time of purchase, so not a whole-day comparison. Exactly 30 days is inside; 30 days and one hour is outside. |
| Travel count | position on the ladder | The number of counting past purchases in the window. |
| Ladder | count → percentage | Nine rungs. **The ticket being bought counts toward its own discount, and the ladder is where that is applied** — see § Where the new purchase enters. |
| Traveller category (ADULT vs SENIOR) | nothing | They follow identical rules and are **one** value of the category dimension, not two. |
| Zone | ticket **price** only | Never affects which scheme applies, never affects whether a past purchase counts, never affects the percentage. A Z1 single counts exactly as a Z3 single does. |

## Where the new purchase enters

The ticket being bought counts toward its own discount: four prior single tickets plus this one is
a count of five, and this purchase is charged at 5%.

**The count derived from history is prior purchases only. The ladder applies the +1.** A count
table over an empty history yields 0, and the ladder maps 4 prior purchases to 5%.

Either placement is defensible, but **the placement must be visible on the published surface** — a
column name, a title, or a `@Description` — because it is the join between two tables and a reader
cannot locate it otherwise. A solution whose count table returns 0 for an empty history and whose
ladder maps 5 → 5% has put the +1 nowhere at all, and that is a defect however each table reads
alone.

The nine rungs, indexed by prior count:

| Prior single tickets | This purchase is | Discount |
|---|---|---|
| 0–3 | 1st–4th | 0% |
| 4–8 | 5th–9th | 5% |
| 9–13 | 10th–14th | 10% |
| 14–18 | 15th–19th | 15% |
| 19–23 | 20th–24th | 20% |
| 24–28 | 25th–29th | 25% |
| 29–33 | 30th–34th | 30% |
| 34–38 | 35th–39th | 35% |
| 39+ | 40th or later | 40% |

## Independence claims that must be exercised, not narrated

Three claims are the substance of the feature, and each must be visible in rows rather than stated
in a `@Description`. **Judge them wherever the relevant rule is stated, not in a table with a
particular name.**

- **zone ⟂ scheme and percentage** — wherever the solution decides which discount applies, the same
  category and count yield the same outcome in every zone.
- **zone ⟂ counting** — wherever the solution decides whether a past purchase counts, it counts
  whatever its zone. A solution that hardcodes every history entry to one zone — in a column, a
  converter, or a field — has claimed this without testing it.
- **adult ≡ senior** — one value set, not two rows with identical outcomes.

A `@Description` asserting an independence that no row exercises is worse than silence: it makes a
claim the tests do not support.

## The rolling window

- The boundary is **measured at the time of purchase**, so it is not a whole-day comparison: a
  purchase 30 days and one hour before the new purchase is outside the window, one 29 days and
  23 hours before it is inside.
- Exactly 30 days is included; just past 30 days is excluded.
- Times are expressed **relative to the purchase being counted** — `29 days ago`, `SINGLE@29`, a
  `Days ago` column. No literal timestamp should be needed to read a table, including one parked in
  a converter class or a test field, which is the same defect one indirection away. A
  `@TypeConverter` turning `30 days 1 hour ago` into a `LocalDateTime` relative to a fixed purchase
  time is the intended shape, and it is what makes the sub-day boundary legible.

## Degrees of decomposition

Score the dimensions, not the table count. These are cumulative — a solution at any level has the
levels below it.

1. **Separated the ladder.** The count → percentage mapping is in a table of its own and appears
   nowhere else.
2. **Separated counting from the ladder.** A table derives a count from a raw purchase history;
   the ladder consumes a count it does not derive.
3. **Separated countability from aggregation.** One table decides whether a *single* past purchase
   counts (ticket type, traveller category, age); another aggregates over a history. The
   aggregation table then shows only counting.
4. **Resolved category and ticket type once.** The mapping from traveller category and ticket type
   to a discount scheme is derived in one place, and no sibling table re-derives it. A solution that
   names a scheme concept reaches this naturally; one that does not can still reach it by keeping
   the category rows in a single table.

**Level 4 does not require the abstraction, only the single derivation.** Do not reward a `Scheme?`
column as such, and do not penalise its absence.

A four-table solution at level 3 is not worse than a three-table solution at level 3. Adding a
table that covers a dimension is never a penalty; adding one that re-proves an established rule is,
and `no-table-reproves-another` judges that.

**Ticket price is out of scope.** This eval scores derivation of the discount percentage. A
price-application table is neither expected nor rewarded; when present it must not re-enumerate the
tier mapping.

## Out of scope, but foreseeable

- **Child single-ticket purchases never count toward anyone's travel count.** This follows from the
  countability rule — a child purchase's own scheme is the flat discount, not Reis — which is why a
  good solution often finds it. But the prompt says "single tickets you have purchased" without
  qualifying by category, so it is not derivable and **not assertable**. Omitting it is not
  penalised.
- When several adult single tickets are bought in one purchase, only one counts toward the ladder.

Inventing a rule that contradicts either is a defect, but **no assertion scores it** — see
§ Judging.

## Judging

**Score each dimension independently.** A solution that misses one dimension must still be able to
score every other. The dimensions are: scheme selection, past-purchase countability, the rolling
window, aggregation, the ladder, and the three independence claims. Failing to see that category
and ticket type are one function should cost the assertions about that function and nothing else.

Choices *within* a table — value sets versus enumerated boundary rows, column ordering, scenario
wording — are legibility preferences. They are real, and some assertions encode them, but a
solution that separates the dimensions correctly and enumerates a tier ladder is not a worse
decomposition than one that compresses it.

**Two things this file states and no assertion scores**, so do not invent one while grading:
child purchases not counting, and multiple tickets in one purchase.
