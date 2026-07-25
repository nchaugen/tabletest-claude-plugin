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

### Per-eval agreement — the measurement that decided it

Comparing **every** slot (not only the answer-key-covered ones) between `m1` and `t4`:

**351 of 365 slots identical — 96.2% — and 10 of 17 evals agree perfectly.**

| Disagreement | Evals |
|---|---|
| 4 slots | 18, 29 |
| 2 slots | 25 |
| 1 slot | 15, 22, 23, 28 |
| **0 slots** | **1, 2, 7, 8, 9, 14, 20, 26, 27, 30** |

**Read the 1-slot cases against the noise floor.** Instability at `high` is 4.9%, so on a ~27-slot
eval **~1.3 slots of disagreement is what noise alone produces**. Evals 15, 22, 23 and 28 are
therefore indistinguishable from identical; only 18, 29 and 25 differ above the floor — and 18 is
the suite's weakest eval, 29 its largest.

### LANDED: `medium` on the ten fully-agreeing evals

`grading_effort: "medium"` is pinned in `eval.json` for evals **1, 2, 7, 8, 9, 14, 20, 26, 27, 30** —
**182 of 365 slots, exactly half the suite**. Everything else stays at the `high` default.

Deliberately the *conservative* split: it claims `medium` only where the two levels produced
**identical verdicts on every slot**, and declines to adjudicate the 1-slot cases even though they
are almost certainly noise. Adopting `medium` where verdicts were identical is provably free for
this run; the residual risk is only that a future run diverges, which accuracy scoring would catch.

Being a per-eval property it feeds each eval's fingerprint, so the guard excludes those evals from
comparisons spanning the change — **re-baseline before comparing anything against t4 on them.**

**Still owed, and now cheaper to get:** repeat passes per level to separate signal from noise on 18,
29 and 25, and a principled rule for the split. "Evals whose failing set contains
irreducible-judgement assertions grade at `high`" generalises to a new eval; the current list is
fitted to one measurement and should be replaced by a property once the probe exists.

### The split was decided on agreement — re-decided on accuracy, 2026-07-25

The ten-eval list above was chosen by **agreement with `high`**, which is the precision-not-accuracy
error this document argues against everywhere else: `high` is itself only 93% accurate, so an eval
where `medium` disagrees may be `medium` being right. Re-deciding it on the answer key needed the
divergent slots keyed, so the **five divergences with no key entry were authored from the artefacts**
(`rule-statable-from-table`/18, `description-no-irrelevant-information`/22, `options-as-map`/25,
`minimal-rows-per-concern`/28, `scenario-names-describe-conditions`/29). Key is now **69 entries**;
`t4` scores **63/68 (93%)** and `m1` **60/68 (88%)**.

**The two levels have almost disjoint error sets**, and per eval that is decisive:

| Eval | `high` wrong | `medium` wrong | Better |
|---|---|---|---|
| 15 | `2.16-no-duplicate-tier-mapping` | — | **medium** |
| 18 | `rule-statable-from-table` | `depth-premium-boundaries`, `minimal-rows-per-concern`, `separates-decision-and-premium` | high |
| 22 | — | `description-no-irrelevant-information` | high |
| 23 | `concerns-decomposed` | — | **medium** |
| 25 | `rule-statable-from-table` | `options-as-map` | tie |
| 28 | `minimal-rows-per-concern` | — | **medium** |
| 29 | — | `quantifier-covered-by-rows`, `scenario-names-describe-conditions`, `type-converters-for-complex-objects` | high |

**The two levels fail in opposite directions.** Seven of `medium`'s eight errors are false PASSes —
it misses real defects. Four of `high`'s five are false FAILs — it over-fires, and in three of them
by *inventing a stricter obligation list than the eval's own `expected_output.md` states*, which the
assertions explicitly forbid. `medium` is not simply a worse `high`; it trades over-firing for
under-firing.

**The mechanism behind `medium`'s misses, from the evidence text:** it stops at the first confirming
instance instead of scanning. On `scenario-names`/29 it quoted a row from a different table and never
reached `Empty cart cannot check out`; on `description-no-irrelevant-information`/22 it cited the
description's legitimate additions and never weighed the redundant sentence. Both assertions carry an
explicit "judge every `@TableTest`" clause.

**Candidate split — 14 of 17 at `medium`,** adding **15, 23, 25, 28** (100 slots) to the ten, leaving
only **18, 22, 29** (83 slots) at `high`. Note this *moves 22 up* and *moves 25 down* relative to the
noise-floor reasoning above: 25's 2-slot divergence is one error each way, and 22's single divergence
is `medium` being wrong.

**The property is still not stated, and this measurement does not supply it.** The obvious candidates
are falsified by the data: it is not eval size (15 and 25 are as large as 18), not failing-set size
(15 and 28 have *six* genuine failures each and `medium` is perfect on both, while 29 has four and
`medium` misses three), and not assertion identity (`medium` reads `minimal-rows-per-concern`
correctly on 15 and 28 and wrongly on 18). **n = 1 per level**, so a real share of this table could be
instability rather than an effort effect — that is exactly what the repeat passes must settle before
any of it is pinned.

> **⚠️ SUPERSEDED by the probe below.** Every per-eval verdict in the table above is an n = 1
> artefact. At n = 3 per level **all five "stable divergences" dissolve into grader instability** and
> the two levels are indistinguishable. The table is kept only as the record of what a single pass
> per level wrongly appeared to show — do not cite it.

### The probe: n = 3 per level — 2026-07-25 (`m1/m2/m3`, `t4/h2/h3`)

Three grade-only passes at each effort over the five evals where the decision was close (15, 22, 23,
25, 28 — 128 slots, 29 of them answer-keyed). Identical stored outputs, identical assertion text,
nothing else changed.

| | `medium` | `high` |
|---|---|---|
| Accuracy per pass (of 29) | 27, 26, 27 | 25, 27, 28 |
| **Mean accuracy** | **26.67 (92.0%)** | **26.67 (92.0%)** |
| Accuracy spread | **1** | 3 |
| Level per pass (of 128) | 109, 112, 109 | 107, 110, — |
| Unstable slots | **4/128 (3.1%)** | 7/128 (5.5%) |
| **Stable divergences** | **zero** | |
| Grading cost per pass | **$0.67** | $1.03 |

**The two levels are indistinguishable in quality.** Mean accuracy is identical to three significant
figures; `medium` is the *more consistent* of the two (spread 1 against 3) and shows fewer unstable
slots — while costing about two-thirds as much.

**Every apparent per-eval difference was `high` sampling its own noise.** Each of the five slots that
looked like a level effect turned out to be a slot `high` itself flips on:

| Eval | Slot | What n = 3 shows |
|---|---|---|
| 15 | `2.16-no-duplicate-tier-mapping` | `high` wrong once, right twice |
| 22 | `description-no-irrelevant-information` | `high` right once, wrong twice |
| 23 | `concerns-decomposed` | `high` wrong once, right twice |
| 25 | `rule-statable-from-table` | unstable at **both** levels |
| 28 | `minimal-rows-per-concern` | `high` unstable |

**So there is no property to state, because there is no eval-level effect to explain.** The owed
"principled rule for the split" was owed on a false premise: the fitted list was fitted to noise. The
honest formulation is the negative one — *no eval-level property distinguishes `medium` from `high`
on this suite; effort is not the variable the divergences were measuring.*

**What the probe does confirm is which slots are genuinely hard.** The unstable set concentrates on
`rule-statable-from-table` (both levels, evals 25 and 28) and `minimal-rows-per-concern` (both levels)
— already this document's #1 and #2 noise sources. They are hard *irrespective of effort*, so raising
effort is not a lever on them; only wording or a checker is.

**A methodological note worth keeping.** The original sweep compared one `medium` pass against one
`high` pass and read every difference as an effort effect. With `high`'s own instability at ~5%, a
single-pass comparison over 128 slots is expected to show ~6 spurious divergences — which is
approximately what it showed. **Never compare two grading configurations at n = 1**; the instrument's
own noise exceeds any effect being looked for, exactly as it does for skill deltas.

### Evals 18 and 29 probed the same way — and they are genuinely different

The five evals above showed no effect, so 18 and 29 were probed at n = 3 per level too. They do not
behave like the other five:

| Eval set | keyed slots | `medium` (n = 3) | `high` (n = 3) | Gap |
|---|---|---|---|---|
| 15, 22, 23, 25, 28 | 29 | 27, 26, 27 → **26.67** | 25, 27, 28 → **26.67** | **0.00** |
| 18 | 8 | 5, 7, 7 → **6.33** | 7, 8, 8 → **7.67** | 1.33 |
| 29 | 5 | 2, 3, 2 → **2.33** | 5, 4, 4 → **4.33** | 2.00 |

On eval-29 the two levels' ranges **do not overlap at all** — `medium`'s best pass is worse than
`high`'s worst. On eval-18 `medium` is both less accurate and three times noisier (3 unstable slots
against 1).

**This corrects the selection rule proposed above.** "Pin `medium` where n = 3 shows no *stable
divergence*" is too strict a test: at n = 3 even eval-29's last stable divergence dissolved, because
when both levels are noisy on *different* slots no single slot diverges consistently while aggregate
accuracy still differs sharply. **The criterion is mean accuracy over repeated passes**, and
per-slot stability is a diagnostic, not the decision.

**The mechanism behind `medium`'s losses, evidenced twice on eval-29.** `medium` reasons from a
confirming instance and stops. On `scenario-names-describe-conditions` it wrote *"'Expired coupon does
not replace the active coupon' names the rule, not a literal expectation value"* — true of that row,
while never scanning back to `Empty cart cannot check out | … | Success? false` in the first table. On
`type-converters-for-complex-objects` it listed the four converters that exist without checking what
was missing. Both assertions carry an explicit "judge every `@TableTest`" clause; `high` honours it.

**The eval-level predictor is still not established.** `medium` finds the buried offender on eval-22's
`scenario-names` and misses it on eval-29's; slot count and genuine-failure count both fail to
separate the two groups (eval-22 has 28 slots and behaves safely, eval-18 has 26 and does not;
eval-15 has six genuine failures with `medium` perfect, eval-18 has five with `medium` struggling).
Plausibly it is the number of tables to scan — eval-29 is the suite's largest — but seven evals cannot
establish that. **Treat the split as measured, not derived**, and re-measure when the suite changes.

### LANDED: `medium` on fifteen of seventeen evals

`grading_effort: "medium"` now covers the original ten plus **15, 22, 23, 25, 28** — **310 of 365
slots (85%)**. **Evals 18 and 29 stay at `high`**, on the accuracy gaps above.

`rule-statable-from-table`, `minimal-rows-per-concern` and `scenario-names-describe-conditions` are
unstable at **both** levels. They are intrinsically hard, so effort is not a lever on them; wording or
a checker is.

**Re-baselined: `benchmark-t5.json` — 318/365, accuracy 62/68 (91%), grading $1.83, ~13 min.**
This is the current baseline for any skill comparison; **`t4` (314/365) is dead**, and the level
difference is not readable as a change — five evals carry new fingerprints, and both benchmarks grade
the same stored pre-tranche outputs. Against the all-`high` regime's 22–83 minutes a full regrade is
now ~13 minutes, which is the point of the exercise. The 1-slot accuracy difference from `t4`'s 63/68
is inside the measured per-pass spread, and the error set *moved* rather than grew.

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
