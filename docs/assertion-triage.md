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
- ~~**`minimal-rows-per-concern`**~~ — **reclassified as Bounded, 2026-07-25.** The premise was that the
  concern's obligations "live in prose in `expected_output.md`" and are therefore unavailable. They are
  available: `run-evals.js` puts `expected_output.md` into the grader's prompt. What is missing is any
  instruction to use it — the assertion is 192 characters and never names the obligation list. Evidence:
  authoring the answer key for eval-30 produced a PASS from the assertion text alone, and the artefact
  plus the obligation list ("one interaction case … is valuable but optional"; "Iteration-40 spends 6
  rows here where 4 obligations exist") makes it a FAIL — the grader had it right and cited the
  obligation list to get there. **Fix: name the surface** ("judge the row set against the coverage
  obligations stated for this concern in the expected output"), the same edit that worked for items 5,
  6 and 8 of the `84e6916` tranche. Do not vote it before trying that.

## Grading effort swept — 2026-07-25 (`benchmark-m1.json`)

Grading sends no `output_config`, so every measurement to date was taken at the API default,
**effort `high`**. Sweeping to `medium` on identical stored outputs:

| | high (t4) | medium (m1) |
|---|---|---|
| Accuracy | 60/63 (95%) | **58/63 (92%)** |
| Level | 314/365 | 322/365 |
| Wall clock | 22–83 min | **9m32s** |
| Grading cost | not recorded | $1.65 |

**Medium is faster by 3–8× and more lenient by 8 slots** — it misses real failures rather than
inventing them, which is the expected shape for less deliberation.

**The losses are eval-shaped, not assertion-shaped — which is the result that matters.** All five
slots medium gets wrong and high gets right sit in **two evals**, and each is a *different*
assertion:

| | Losses |
|---|---|
| **By eval** | 18 → 3, 29 → 2 |
| **By assertion** | five assertions, one each — no pattern |

Had the losses clustered by assertion (every `rule-statable-from-table` host, say), per-eval effort
would buy nothing, because the hard assertions are spread across the suite. They cluster by eval
instead, and on the two hardest evals — 18 is the suite's weakest at 18/24, 29 its largest at 29
slots. **So mixed effort is viable in principle: cheap evals at `medium`, hard evals at `high`.**
`--grading-effort` (run-wide) and `grading_effort` in `eval.json` (per eval, feeds the fingerprint)
both exist for this as of `07a898e`.

**Do not act on this yet — n is 5 and the run is single-pass.** Medium also got **3 slots right that
high got wrong** (`2.16`/15, `concerns-decomposed`/23, `rule-statable-from-table`/25), which is
direct evidence that ordinary slot instability is mixed into these numbers. With one pass per level
there is no way to separate "medium is worse on 18 and 29" from "those slots flip anyway". Confirming
it needs repeat passes at each level — and **that is a measurement to run when the cost of a variance
probe is the thing being optimised, not before the skill has moved.**

**If it is confirmed, pick the split by a stated property, not by this error list.** "Evals whose
failing set contains irreducible-judgement assertions grade at `high`" is a rule that generalises to
a new eval; "18 and 29 grade at `high`" is a lookup table fitted to 64 answer-key entries.

## Haiku retested on the rebuilt instrument — 2026-07-25 (`benchmark-h1.json`)

The hypothesis was that three batches of assertion rewrites had lowered the capability bar: the
stable assertions are long *because* they state a decision procedure, and applying a stated procedure
should be easier than inventing one. Dropping the `text` echo also freed haiku's much smaller 4096
budget. **Predicted 80–90%. Measured 46/63 (73%)** against sonnet's 60/63 (95%) on identical stored
outputs and identical assertion text — essentially unchanged from haiku's original 76%.

**The hypothesis is wrong, and the error set says why.** Of the 16 slots haiku gets wrong that sonnet
gets right, the failures are *not* confined to the irreducible judgement calls. They include the
assertions deliberately made decidable:

- `titles-form-a-family`/28 — eight sibling titles all begin `should`, and clause (1) says "FAILS when
  three or more share a leading word that carries no information ('should…')". That is nearly a regex,
  and haiku missed it. The single most damning case.
- `2.19-depth-all-tiers`/15 — the ladder shows five of nine tiers; the assertion says "not just a
  subset". Countable.
- `black-box-columns`/18 and `assertion-criteria-declared`/18 — both surface-scoped in the `84e6916`
  tranche precisely to remove judgement; `assertion-criteria-declared` graded 9/9 on sonnet.
- `held-constants-declared`/15 — explicit FAILS/PASSES/not-a-failure structure.

**So the decision-procedure rewrites buy stability on a capable grader; they do not substitute for
grader capability.** Those are separate axes and this measurement separates them. It is also a
caution on the triage's own core claim: converting an assertion to a decision procedure is not a route
to grading it with a cheaper model.

Direction of haiku's errors: **11 missed real FAILs (said PASS), 6 wrongly failed real PASSes** — a
PASS lean, but wrong in both directions, so not a bias a threshold could correct. Its level (294/365)
sits *below* sonnet's 314 despite missing more real failures, i.e. it is noisier both ways.

**Economics, now measured rather than estimated:** haiku graded the full suite in **3m17s for $0.27**
(169k input, 20k output, 29 calls) against sonnet's tens of minutes. Roughly 10× faster and ~5× cheaper
— and unusable at 73%. **Grader thrift stays false economy**; the speed would have made a three-pass
variance probe cheap, but voting a 73% grader three times just makes 73% stable.

Recorded as a closed question: do not re-test haiku on a wording change alone. It would take a
capability change, not an assertion change.

## Batch-3 re-baseline — measured 2026-07-25 (`benchmark-t3.json`)

**Accuracy 59/63 (94%)**, from 89% (t2) and 77–88% pre-tranche. **New level 318/365** — the current
baseline for any skill comparison. t2's 323/365 and the original 324/365 are both dead.

**The level fell and that is the instrument working.** Batch 3 made two assertions catch defects they
were previously blind to, so the same unchanged skill output scores lower. A level drop that follows an
accuracy rise is not a regression — it is the instrument becoming less wrong. Never read these two
numbers in the same direction.

**Every prediction held, including the falsifier.** `scenario-names-describe-conditions` is correct on
all five key-covered hosts (7, 9, 22, 23 FAIL; 27 the deliberate PASS release), and — the check that
mattered — **none of the six uncovered hosts started failing**. 18, 25, 26, 28, 29 and 30 all still
PASS, so the narrowing did not over-fire; the two names flagged in advance as likeliest over-fires
(eval-30's "Delivery and pickup items always split", eval-18's "would otherwise be standard") both held.

**The judge-every-table clause worked and found one the key had missed.** eval-14 flipped to FAIL as
predicted; **eval-15 also flipped**, with no key entry to predict it. Adjudicated from the artefact
before reading the grader's evidence, and the two agree: `calculatesDiscountEndToEnd` spends six rows
re-proving rules tables 1 and 3 already established. Correct failure; entry added, key now 64 entries
with **one borderline left** (`business-language-columns`/29).

**Four slots remain wrong and three are one assertion** — `rule-statable-from-table` on 25, 27 and 28,
which this batch did not touch and which is next in the queue, plus
`type-converters-for-complex-objects`/29.

## Tranche-2 re-baseline — measured 2026-07-25 (`benchmark-t2.json`)

**Accuracy 54/61 (89%)**, against pre-tranche passes of 88% / 87% / 77% / 87%. New level is
**323/365**, which is *not* comparable to the old 324/365 — different assertion texts.

**Both title predictions confirmed exactly.** `titles-form-a-family` went 4/4 correct: eval-25, the
suite's **only stably-wrong slot** (0/4 before), is now right, and 26 (3/4) and 27 (2/4) stabilised
while 28 correctly stays FAIL on its eight `should` prefixes. `description-no-redundant-field-values`/23
went 1/4 → correct. `rule-falsifiable-by-a-row`/27 is correct now that the invariance exemption exists.
**The bias slot is gone.**

**The `minimal-rows-per-concern` prediction was wrong, and the reason matters more than the miss.**
It first read as a regression — 18 and 22 flipped from mostly-agreeing to disagreeing. Reading the
grader's evidence against the ground truth showed **the key was wrong, not the assertion**:

- eval-22 — the grader cited "two representative invalids (no `@`, no domain) are enough. Enumerating
  an RFC is neither expected nor rewarded." The output carries four malformed-email rows. Grounded.
- eval-18 — the reference decision table discharges rejection at 5 claims and has no "well above
  threshold" row; the output spends one on `50 | 10 | REJECTED` while omitting the 4-claim row.
  Grounded.

Both entries were corrected to FAIL. **I had made the exact error this file's own method note warns
about — adjudicating from the assertion text instead of the obligation list — twice more, on the very
assertion the note is about.** Against the corrected key the assertion went from **11/20 to 4/5
correct**. Naming the surface worked.

**The one remaining miss is a different defect.** eval-14 now PASSES with the evidence
"calculatesWeekdayPay rows progress from 0, below-threshold, at-threshold … no obvious duplicate rows"
— the grader judged **one table and never looked at the other four**, including the fifth, whose own
`@Description` says it combines the rules from the tables above. That is a *coverage* failure, not a
criterion failure, and its fix is the same surface-naming pattern: say "judge every `@TableTest` in the
class, not the first one". Add it to the next tranche.

**Six unedited slots also moved wrong** (`1.7`/14, `scenario-names`/23 and /27, `rule-statable`/27,
`quantifier-covered-by-rows`/29, `type-converters-for-complex-objects`/29). This is one single run
against a 32%-variance-affected slot set, so treat it as noise until a probe says otherwise — except
`scenario-names-describe-conditions`, which was already majority-wrong and stays wrong. It remains the
top of the queue.

## Accuracy over the whole failing set — measured 2026-07-25 (pre-tranche-2 texts)

The answer key was extended from 30 entries to 63, covering **every slot in the union of the three
variance passes' failing sets** (51 slots: 33 stable failures, 18 flippers). Scoring the four gradings
of identical stored outputs against it:

| Run | Accuracy |
|---|---|
| baseline `grading.json` | 53/60 (88%) |
| pass v1 | 52/60 (87%) |
| pass v2 | 46/60 (77%) |
| pass v3 | 52/60 (87%) |

**The 20/21 (95%) that justified the haiku→sonnet switch was measured on 21 entries and overstates the
grader.** On the slots that actually decide a skill delta, sonnet runs at 77–88% — and accuracy itself
swings 10 points across byte-identical inputs.

**Bias versus variance, over the four runs:**

- **40 of 60 slots are correct in every pass.**
- **1 slot is wrong in every pass** — `titles-form-a-family`/25, which fails an otherwise coherent
  family for one subject-first outlier. Genuine bias, and the tranche-2 exemption already drafted in
  the slice-1 plan is its fix.
- **19 slots are wrong in at least one pass**, and five of those are wrong in *three* of four:
  `scenario-names-describe-conditions` on 7, 23 and 27, `description-no-redundant-field-values`/23, and
  `rule-statable-from-table`/27.

**The consequence that changes the sequencing.** Majority-of-three voting resolves a slot to whatever
the grader says most often — so on those five slots **voting converts a 75%-wrong verdict into a
100%-wrong one**. `scenario-names-describe-conditions` is the clearest case: it would become reliably
wrong on three separate evals. Voting buys detection power and pays for it in direction, which is the
worse trade for a plan trying to establish that a skill edit helped. Estimated post-voting accuracy is
~53/60 — the same headline as a single run, with roughly six slots now *stably* wrong.

**So: narrow and re-score before voting.** `--grade-runs 3` stays necessary for the MDE, but it must be
applied after the wording fixes, not instead of them, and every fix must be re-scored for accuracy — an
edit that raises stability while lowering accuracy has made the instrument worse.

## Sequencing

**Revised 2026-07-25 by the accuracy measurement, and by tranche 2 landing (`2dcad47`).** Steps marked
✅ are done; the order of what remains changed because bias now outranks variance.

- ✅ **`minimal-rows-per-concern`** — reclassified bounded and fixed by naming the surface, across all
  13 hosts (it had drifted into three divergent texts). Not voted.
- ✅ **`rule-falsifiable-by-a-row`** — invariance exemption added on all 10 hosts; a table whose claim
  *is* an invariance correctly holds one expectation value, and conditions (1) and (3) no longer fire
  on it where the title or description states the invariance.
- ✅ **`titles-form-a-family`** — clause (2) replaced with a majority-shape test plus a single-outlier
  exemption, which is the fix for the suite's only stably-wrong slot. **It is therefore no longer a
  delete-clause-(2) candidate**; measure the new wording before reaching for the regex.
- **Next: `scenario-names-describe-conditions`**, promoted ahead of `rule-statable-from-table`. It is
  *majority*-wrong on evals 7, 23 and 27 — three of four passes each — so voting would lock in three
  wrong verdicts. Narrow it to the decidable core; do not add examples.
- **Then `rule-statable-from-table` clause (1) → checker**, as below. Note the trap the answer key
  found: eval-15 holds `PURCHASE_TIME` in a private field, which a literal "reference date in a field"
  proxy fails, but the cells encode days-ago relative to it and the scenario names decode that, so the
  correct verdict is PASS. Evals 25 (PASSes, publishes the 1.15 multiplier) and 27/28 (FAIL, it appears
  nowhere) are the discriminating fixtures.
- **Then the two remaining mechanical conversions**: `business-language-columns`,
  `consistent-quantity-naming`.

Original numbering, still current for the mechanical work:

1. Convert the mechanical items, each with answer-key entries. Free per run, permanently stable,
   and it shrinks the batched LLM calls (cost and latency).
2. Narrow the two bounded items.
3. Re-measure: three passes for instability, plus answer-key accuracy. Both must improve, or accuracy
   must at least hold — a change that stabilises the grader onto a *wrong* boundary is worse than noise.
4. Whatever remains unstable is the irreducible floor, and `--grade-runs 3` is the only lever left.

This is a regime change: re-baseline afterwards, and no comparison may span it.
