# Assertion triage — mechanical, bounded, or irreducible

Instability is not one problem. Sorting the unstable assertions by *what kind of judgement they
ask for* points each at a different fix, and one of those fixes removes variance rather than
averaging it.

Source: three independent gradings of byte-identical iteration-40 outputs, sonnet grader,
2026-07-25. **18 of 365 slots unstable = 4.9%**, level ranging 320–327 (a 7-slot spread on the same
bytes). Single-run MDE is therefore ~7–8 slots, which is why `--grade-runs 3` is now required for any
comparison rather than reserved for adjudication.

**44% of the instability is two assertions:** `rule-statable-from-table` (4 flips: evals 15, 25, 27,
28) and `minimal-rows-per-concern` (4: evals 14, 18, 22, 30). Then
`scenario-names-describe-conditions` (2), and one flip each from `1.7-readability-scenario-names`,
`depth-premium-boundaries`, `concerns-decomposed`, `description-no-redundant-field-values`,
`rule-falsifiable-by-a-row`, `titles-form-a-family`, `business-language-columns`,
`quantifier-covered-by-rows`. Fixing the top two roughly halves the rate on its own.

## Split on decidability boundaries, not on size

Length does not predict instability. The flippers average 521 chars against 292 for stable ones, but
**the five longest assertions in the suite are all stable** — `assertion-criteria-declared` (1290),
`held-constants-declared` (1275), `concern-not-over-split` (1211), `consistent-quantity-naming` (871),
`native-collection-output` (652) — while three of the worst flippers are among the shortest:
`scenario-names-describe-conditions` (127), `business-language-columns` (173),
`minimal-rows-per-concern` (192).

What tracks stability is whether the assertion states a **decision procedure**. The long stable ones
are long *because* they say "FAILS when X. PASSES when Y. Z is explicitly not a failure." The short
unstable ones only illustrate — "like this, not like that" — leaving the grader to invent the cut, and
it invents a different one each time.

So:
- **Split when clauses differ in decidability.** `rule-statable-from-table` is the case: clause (1) is
  mechanical, clause (2) irreducible, and compounding them permanently *masks* the mechanical verdict.
  Proof it works: `assertion-criteria-declared`, split out of it on 2026-07-25, graded identically on
  all three hosts across all three passes (9/9) while its parent flipped four times.
- **Do not split equally-decidable clauses.** That inflates slots for no gain, and 55% of slots already
  sit in families that never fail — ballast divides any real effect.
- **Grow, don't split, the short ones.** `scenario-names-describe-conditions` needs a decision rule
  added, not division.

## The three buckets

| Bucket | Fix | Residual variance |
|---|---|---|
| **Mechanical** — countable from the source | Move to a checker in `scripts/assertions.js` | **None.** 36 checkers over 162 slots have never flipped |
| **Bounded judgement** — a real judgement with a locatable cut | Narrow the criterion to its decidable core | Reduced |
| **Irreducible** — "could a reader state the rule?" | Accept; `--grade-runs 3` | Unchanged |

**Inline examples are not the lever they look like.** `scenario-names-describe-conditions` already
carries four of them (two good, two bad) and is the most unstable assertion in the set. Examples pin
the ends of a spectrum; they do not tell the grader where to cut it. Prefer narrowing the criterion
to something decidable over adding more demonstrations.

**Determinism converts variance into bias.** A checker encodes a syntactic proxy for a semantic
property; when the proxy is wrong it is wrong every time, with a confident 0% instability around the
wrong answer — harder to spot than noise. **Every new checker needs answer-key entries covering at
least one PASS and one FAIL case before it is trusted** (`docs/grader-answer-key.json`).

## Mechanical — convert to checkers

- **`rule-statable-from-table`, clause (1)** — "a value needed to predict the expectation appears
  only in the method body". Extract numeric and string literals from the method body and test
  membership in the table text, `@DisplayName`, and `@Description`. This is the highest-value
  conversion in the list: the assertion has surfaced as unstable in four separate probes, and this
  clause would have caught the one genuine answer-key miss (eval-28's fragile multiplier `1.15`,
  present nowhere on the published surface). Clause (2) stays LLM — see Irreducible.
- **`titles-form-a-family`, clause (1)** — "three or more titles share a leading word that carries no
  information". A regex over method names. Clause (2) ("unrelated grammatical shapes") should be
  **deleted**, not anchored: it is what flips on evals 26 and 27, its bar could not be stated even by
  its author, and the suite contains no exemplary title family to anchor against.
- **`business-language-columns`** — a code-ism list over column headers (`Id`, `Str`, `Int`, `param`,
  `result`, camelCase, snake_case). Its eval-29 flip was on `Product Id`, which a list matches.
- **`consistent-quantity-naming`** — comparing output-column headers across the `@TableTest` methods
  of one class is string work with no judgement in it. It did not flip, so this conversion buys cost
  and permanence rather than stability.

## Bounded — narrow the criterion

- **`scenario-names-describe-conditions`** (3 flips, the worst) — replace "describes conditions, not
  outcomes" with the decidable core: **FAILS when a scenario name states or paraphrases a value in an
  expectation column of the same row.** Narrower than the current property — it will not catch "No
  discount applies", where the name describes the outcome without echoing the cell — but a narrower
  stable rule beats a broader random one. Consider a mechanical token-overlap checker for the clear
  half and no LLM assertion at all.
- **`quantifier-covered-by-rows`** — claim detection is semi-mechanical (`regardless of`, `any`,
  `whatever the`, `with or without`); mapping the quantified domain and checking coverage is
  judgement. Its eval-29 flip sits on a genuine edge (a floor claim covering PERCENT and FIXED but
  omitting PRODUCT), so the assertion is working — it is the reporting that is intermittent.

## Irreducible — accept and vote

- **`rule-statable-from-table`, clause (2)** — "the operation applied to the inputs cannot be named
  from the column headers and cell values alone". This is the plan's central question (D(e)) and there
  is no syntactic proxy for it.
- **`minimal-rows-per-concern`** — row count is only judgeable against the concern's obligations, which
  live in prose in `expected_output.md`. Not derivable from source.

## Sequencing

1. Convert the four mechanical items, each with answer-key entries. Free per run, permanently stable,
   and it shrinks the batched LLM calls (cost and latency).
2. Narrow the two bounded items.
3. Re-measure: three passes for instability, plus answer-key accuracy. Both must improve, or accuracy
   must at least hold — a change that stabilises the grader onto a *wrong* boundary is worse than noise.
4. Whatever remains unstable is the irreducible floor, and `--grade-runs 3` is the only lever left.

This is a regime change: re-baseline afterwards, and no comparison may span it.
