Black-box `@TableTest`s over `evaluateApplication(applicantType, age, claimCount)` returning
`EvaluationResult(decision, premium)`. Two concerns, two tables: a **decision** table whose axis
is the claim count (with the renewal special case), and a **premium** table showing how the
approved premium varies with age and claims. Columns are the *public inputs and observable
outputs only* — never `internalRiskScore`, `hasActivePolicy`, or the private formulas.

## What the method actually does

| Condition (checked in order) | Decision | Premium |
|---|---|---|
| `RENEWAL` and 0 claims | `AUTO_APPROVED` | 0 |
| risk score > 75 | `REJECTED` | 0 |
| otherwise, age ≥ 65 | `APPROVED` | senior formula |
| otherwise | `APPROVED` | standard formula |

The risk score is internal (`age/10 + claims*15`, rejected above 75). It must **not** appear as a
column or in `@Description` — the reader sees its effect through the input/output rows, not the
arithmetic.

## Claim count is the decision, and age is a red herring for it

The decision hinges almost entirely on claims. Because `claims*15` dominates and `age/10` is at
most 10 for any realistic age, the rejection cliff sits cleanly between **4 claims (approved)** and
**5 claims (rejected)**, independent of age — a claim count of 4 cannot be pushed over 75 by age,
and 5 is over the line for anyone. A row that holds 4 claims while pushing age to its realistic
maximum is what *proves* the boundary is about claims, not age.

Reference **decision** table (assert `Decision?` only; hold age roughly constant, vary claims and
type):

| Scenario | Applicant type | Age | Claims | Decision? |
|---|---|---|---|---|
| New applicant, no claims | NEW | 40 | 0 | APPROVED |
| New applicant at the claim limit | NEW | 40 | 4 | APPROVED |
| Oldest applicant still under the limit | NEW | 100 | 4 | APPROVED |
| New applicant over the claim limit | NEW | 40 | 5 | REJECTED |
| Renewal with no claims | RENEWAL | 40 | 0 | AUTO_APPROVED |
| Renewal with a claim | RENEWAL | 40 | 1 | APPROVED |
| Renewal over the claim limit | RENEWAL | 40 | 5 | REJECTED |

The last two rows carry the renewal rule the prompt cares about: **a renewal is auto-approved
only at zero claims; with any claims it is treated exactly like a new application** and runs the
same risk/premium path (approved at 1 claim, rejected at 5).

## Premium: a few illustrative rows, not an enumeration

For approved applications the premium concern shows five effects, and only needs enough rows to
make each visible (the user's steer: "just a few illustrative examples", claims being the
important factor overall). Hold one thing constant and move the other:

| Scenario | Applicant type | Age | Claims | Premium? |
|---|---|---|---|---|
| Applicant in their thirties | NEW | `{30, 39}` | 0 | 106.0 |
| Applicant in their forties | NEW | `{40, 49}` | 0 | 108.0 |
| Standard applicant, one claim | NEW | 30 | 1 | 136.0 |
| Standard applicant, a second claim | NEW | 30 | 2 | 166.0 |
| Just below senior age | NEW | 64 | 0 | 112.0 |
| Senior applicant, clean history | NEW | 65 | 0 | 221.0 |
| Senior applicant, one claim | NEW | 65 | 1 | 273.5 |
| Senior applicant, a second claim | NEW | 65 | 2 | 326.0 |

**Five obligations.** Each row must earn its place by showing a *different kind of impact*, and each
obligation below names the wrong reading its rows exist to kill:

- **A claim raises the premium** (`106 → 136` at age 30). Kills "claims do not price".
- **The charge is per claim, not per claim history** (`106 → 136 → 166`): three consecutive counts,
  because 0 against 1 alone is equally consistent with a flat penalty for having any claim history —
  a real pricing pattern, and the reading a two-row pair cannot exclude. The third row is what
  decides between them. **This is not sampling a known-linear effect**; it is what establishes that
  the effect is linear rather than a step.
- **A claim costs a senior more** (`221 → 273.5` at age 65, against `106 → 136` at age 30): the
  increment is 30.00 for a standard applicant and 52.50 for a senior. Kills "a claim adds 30",
  which is wrong for a senior by 22.50. This is the *rate* half of the senior jump, which the
  threshold pair alone cannot separate from the base half. The claim run therefore appears at
  **both** ages.
- **Age is banded, not counted** (`{30, 39} → 106.0`): two applicants in the same decade pay the
  same. Kills **"premium rises with age"**, which is the reading a table of 30 → 106 beside
  64 → 112 invites and which is false — every age from 30 to 39 prices identically. A value set is
  the compact discharge; two rows sharing a premium do it just as well.
- **The bands are decades** (`{40, 49} → 108.0`, adjacent to the row above): the second flat band
  places the edge between 39 and 40 and shows the pattern repeats. Kills "the thirties are a special
  case" and every other banding a single plateau leaves open — under-40 against 40–64, say, which
  one flat band plus a distant `64 → 112` fits equally well. This is the same argument as the third
  claim, one level up: one plateau states that *some* ages price alike, two state the shape. It also
  separates the two kinds of step the domain has, a 2.00 decade edge against a 109.00 senior
  threshold, which a table showing only the senior one gives a reader no reason to expect.

The premium properties would really be a graph; the table is only a handful of samples of it. Do not
add rows that are the same pattern with different arithmetic — a third and fourth flat band at
`{50, 59}` and `{60, 64}`, or claims 3 and 4 once the per-claim charge is established. They exercise
the same multiplication and teach nothing the earlier rows did not. **Note what separates those from
the two bands above:** the first band states that ages inside it price alike, the second states that
the banding repeats and where its edge falls, and a third states neither — it re-shows a pattern two
rows have already fixed.

**The test for a further row is whether you can name the wrong reading it kills.** If you cannot, it
is excess. This is also the line between a black-box table and a white-box one: the table states a
rule a reader can act on and rules out the readings a reader would otherwise take, and it stops well
short of letting anyone reconstruct the formula.

The senior threshold is a **premium** effect, not a decision one: 64 and 65 both decide
`APPROVED`, so the distinction only shows up in the premium column and belongs here, not in the
decision table.

## Black-box discipline

- **No internal columns** (`black-box-columns`, `observable-io-only`): no `riskScore`,
  `hasActivePolicy`, `internalRiskScore`.
- **No re-implemented formulas** (`no-reimplemented-internals`): the method body calls
  `evaluateApplication` and asserts a field of the result — it never recomputes `age/10 +
  claims*15` to derive an expected value.
- **`@Description` carries business context, not internals** (`description-no-internals`): "how
  new applications and renewals are assessed", not "riskScore = age/10 + claimCount*15" or
  "rejected when riskScore > 75".
- Business-language columns (`Applicant type`, `Age`, `Claims`, `Decision?`, `Premium?`), scenario
  names describe conditions ("Renewal with no claims"), not outcomes ("Auto approved").

## The unused policy lookup

`checkPolicyDatabase` sets `hasActivePolicy`, but nothing reads it — it is a dead dependency that
does not affect either output today. It is therefore **not testable through this method** and must
not become a column or a scenario. A solution that ignores it is correct; noting it as a latent
dependency (an open question for when it starts influencing the decision) is fine, inventing
behaviour for it is not. This is the "four inputs" framing: the third param plus a policy lookup
that is currently inert.

## Judging

Score the two-concern split and black-box discipline first, then the claim-count cliff (4 vs 5,
demonstrated across ages) and the renewal-at-zero-claims rule. The premium table's stated rule is
necessarily **qualitative** — each claim adds a fixed amount, that amount is larger for a senior, and
age is banded by decade rather than counted — because the exact multipliers are internal and a
black-box test rightly does not expose them. Correct-but-opaque premium figures are the honest limit
of converting a formula to a black-box table, not a legibility failure.

**Qualitative does not mean unfalsifiable, and "premium rises with age" is not one of the rules.**
Premium is flat across each decade and steps at the decade edge: every age from 30 to 39 prices
identically, and 40 to 49 all price 2.00 higher. The last four statements above are the ones a
four-row premium table leaves out, and each is a reading a reader would otherwise take and be wrong
about. A premium table that shows the claim pair at one age only, or that never shows two ages
pricing the same, is incomplete rather than merely sparse — but **no assertion scores any of the four
today**, so do not invent one while grading.
