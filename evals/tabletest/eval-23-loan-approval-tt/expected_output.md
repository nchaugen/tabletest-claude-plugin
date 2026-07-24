One or more @TableTest methods covering three interlocking concerns — the age-dependent credit-score
threshold, the effect of income status, and their precedence — with policy thresholds made visible
by boundary rows (a passing score just above, a failing score at/below, per age band), scenario
names describing conditions, business-language columns, and concrete domain values. Because each
concern needs only one or two scenarios and they share the same input columns, a single well-ordered
table is sufficient; a two-table split is equally good. The @Description states no rule or policy
value the rows already carry.

## The decision function

```
threshold = age >= 65 ? 600 : 650
if creditScore <= threshold  → REJECTED        // below/at threshold wins over income status
else if income == null       → PENDING_REVIEW
else if income == false       → REJECTED       // stable income is a prerequisite for approval
else                          → APPROVED
```

Two facts here are **not stated in the prompt** and are the interpretive core of the eval:

- **Precedence.** The prompt gives "missing income → PENDING_REVIEW" and "below-threshold scores are
  rejected regardless of income" as independent rules; they collide when a below-threshold score has
  null income. The rejection wins ("regardless of income" includes null). A solution must pick a side
  and show it in a row — the reading here is REJECTED, so credit score is *not* irrelevant to the
  pending outcome.
- **Strict threshold.** "above 650" / "lower threshold of 600" is read as strictly greater — 651 and
  601 qualify, 650 and 600 do not.

## The three concerns

| Concern | Rule | Made visible by |
|---|---|---|
| Age → threshold | `<65` uses 650, `65+` uses 600 | the two boundary pairs sitting at different scores |
| Credit boundary | strictly above the applicable threshold | a passing row just above and a failing row at the threshold, per age band |
| Income status | `true` → approve, `false` → reject, `null` → pending — **only once the score qualifies** | three rows holding age and score fixed, varying only income |

The threshold is a *policy value*, not test data. It is made checkable by **bracketing** — 651
approved beside 650 rejected — so the reader infers "the cut is between 650 and 651" without the
number being restated in prose. A dedicated `Credit threshold` column is an equally valid way to
show it; boundary rows are the leaner one when scenarios are already minimal.

## Reference decomposition

A single table. `Stable income` is `Boolean`; a blank cell is null (income unknown).

| Scenario | Age | Credit score | Stable income | Decision? |
|---|---|---|---|---|
| Below non-senior threshold | 64 | 650 | true | REJECTED |
| Meets non-senior threshold | 64 | 651 | true | APPROVED |
| Below senior threshold | 65 | 600 | true | REJECTED |
| Senior meets lower threshold | 65 | 601 | true | APPROVED |
| Qualifies but income unknown | 64 | 651 | | PENDING_REVIEW |
| Qualifies but income not stable | 64 | 651 | false | REJECTED |
| Below threshold outweighs unknown income | 64 | 650 | | REJECTED |

What each row is load-bearing for:

- Rows 1–2 and 3–4 are the two boundary pairs: they fix the non-senior cut between 650/651 and the
  senior cut between 600/601, and *together* show the age effect (a senior clears at 601, a score a
  non-senior would need 651 for).
- Rows 2, 5, 6 hold age and score fixed at 64/651 and vary only income: `true` → APPROVED,
  `null` → PENDING_REVIEW, `false` → REJECTED. This makes the income rule falsifiable and shows the
  stable-income effect as a held-constant pair (rows 2 vs 6).
- Row 7 encodes the precedence decision: the same null income that gives PENDING at a qualifying
  score (row 5) gives REJECTED once the score is below threshold. Without it the precedence is
  untested.

**Equivalent two-table split** (also good): a table of age × credit score with stable income held
`true` (rows 1–4), and a table of income variations at a fixed qualifying score (rows 5–7). Only the
`Stable income` column is dropped from the first, so the saving is small; the split is a
presentation preference, not a requirement.

## Assumed error conditions → their own table

The prompt says nothing about invalid inputs, so these are foreseeable-but-invented and belong in a
separate table (not mixed into the decision rows), each row committing to a chosen behaviour a
reviewer can challenge — e.g. negative or zero age, negative credit score, a score outside a
plausible range. Whether the chosen behaviour is an exception or a REJECTED/error result is a
reading; the point is that the assumption is explicit and isolated, not that a particular answer is
correct.

## Underspecified → make it an explicit row

- **Below-threshold + null income** → REJECTED (row 7), stating the precedence.
- **Exact boundary** (650 / 600) → REJECTED (rows 1, 3), stating that the threshold is strict.

## @Description discipline

The rows carry the rules and the policy numbers, so the `@Description` must not restate the 650/600
thresholds, the 65 age cut, or any field value shown in a column — doing so duplicates a rule the
rows already prove and rots silently if the policy changes. Reserve it for context the rows cannot
show (that "regardless of income" was resolved in favour of rejection, say).

## Judging

Score coverage of the three concerns with minimal, non-redundant scenarios — **a single compact
table is not worse decomposition than three separated ones** when the concerns share columns and each
is covered in one or two rows. Then score legibility: thresholds verifiable from the table (boundary
rows or a policy column), the stable-income effect visible as a held-constant pair, condition-named
scenarios, concrete values (`64`, `651`, `APPROVED`, not codes), and a description that adds context
rather than echoing rows. Redundant permutations (the same rule re-proven across many rows) count
against a solution; whether it split into one table or two does not.
