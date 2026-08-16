# Assertion triage — mechanical, bounded, or irreducible

Instability is not one problem. Sorting the unstable assertions by *what kind of judgement they
ask for* points each at a different fix, and one of those fixes removes variance rather than
averaging it.

## Reference probe, 2026-08-17 — **0 of 331 slots flip**

Three independent gradings of the fourteen hand-authored reference answers
(`iterations/tabletest/reference/`, suffixes `v1`/`v2`/`v3`, ~$5.00, regrade only). **Every one of
331 slots returned the same verdict in all three passes, and every slot passed.** The gradings are
genuinely independent — different checksums, visibly different evidence prose for the same slot —
so this is reproducibility, not a cached read.

**Read against the 12-of-378 population flip rate, this locates the variance.** The same assertion
texts that flip on agent answers are perfectly stable here. So a flip is **a property of the answer,
not of the assertion text**: slots flip when the answer sits near the grader's cut, and a reference
is deliberately far from the cut on every slot.

**The consequence is a lever question.** Rewording an assertion cannot stabilise a judgement about a
genuinely borderline table — the wording is already stable wherever the answer is clear. That is
consistent with the one measured stabilisation attempt in this file: the enumeration clause added to
`rule-statable-from-table` kept its 3 flips *and* lost accuracy. **Prefer conversion to a checker,
which removes the judgement, over narrowing prose, which only moves the cut.**

**It also unblocks conversion.** § Mechanical requires "answer-key entries covering at least one PASS
and one FAIL case before [a checker] is trusted", and `docs/grader-answer-key.json` cannot supply
them — it is bound to `iteration-40` and pre-repair texts. The references supply the **PASS** half on
current texts, now shown reproducible, for every host of three of the four conversion candidates:
`rule-statable-from-table` 7/7, `titles-form-a-family` 4/4, `consistent-quantity-naming` 2/2, and
`business-language-columns` 8/11 (the three uncovered hosts are `spec-by-example`, which has no
reference answers — slice 7's work).

**What this does NOT measure.** Only the PASS side, and only on unambiguous answers. It says nothing
about a weak answer wrongly passing, nor about the flip rate on borderline answers, which is what a
population probe still buys. It does say where to point one.

**Correction while here:** `scenario-names-describe-conditions` is listed below under *Bounded —
narrow the criterion* with the narrowing still to do. **That narrowing has landed** — the current
text carries the decidable test verbatim, and on 2026-08-16 it failed a reference draft whose names
paraphrased an `Allowed?` cell and passed the fix one edit later. Treat that entry as done.

---

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
`held-constants-declared` (1275), `concern-not-over-split` (1211) — **not stable; corrected 2026-08-09, it moved on eval-25 and eval-18 in one session (§ J60)** —, `consistent-quantity-naming` (871),
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
  of one class is string work with no judgement in it. **Correction 2026-07-31: it does flip.** It had
  not, at the time this was written; `iteration-50 [v1]` flipped it on eval-26 (FAIL → PASS) while the
  `Weight (kg)` / `Actual Weight (kg)` divergence stayed in the output, and held it stable on eval-28
  in the same pass. That strengthens the case for the conversion rather than weakening it — the
  judgement-free version cannot flip. This conversion buys cost
  and permanence rather than stability.

## Bounded — narrow the criterion

**Every time you narrow one, name what the narrowing cut and check something else still owns it** —
see § Check the residue when you narrow an assertion for the procedure and the instance that cost
four runs.

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

## The two-assertion fix: one worked, one backfired — measured 2026-07-26 (`s1`/`s2`/`s3`)

Three passes after splitting `minimal-rows-per-concern` and adding a per-method enumeration clause to
`rule-statable-from-table`. Scores 331 / 334 / 328 of 378; **12 of 378 slots flip** against 17 of 365
before. Level held (87.6% vs 87.9%), so no calibration break.

**The split worked.** `minimal-rows-per-concern` contributed 4 flips across 13 slots; its two
successors contribute **2 flips across 26 slots** — `no-duplicate-rows-within-a-table` ×2 and
`no-table-reproves-another` ×0. The class-wide half is completely stable, which is what separating the
two judgements was for. No accuracy read yet: the answer-key rows are `pending-reread`.

**The enumeration clause backfired, and the wording was mine.** `rule-statable-from-table` kept 3
flips *and* lost accuracy badly:

| | s1/s2/s3 (post) | plain/p1/p2 (pre) |
|---|---|---|
| `rule-statable-from-table` | 0/5 · 2/5 · 1/5 | 3/5 · 4/5 · 3/5 |
| all 61 comparable slots | 53 · 54 · 57 | 56 · 55 · 55 |

Evals 15 and 18 are now wrong in **all three** passes, and the key had already written down why. Its
eval-18 basis records an earlier failure that "imports a per-method scoping the text does not carry",
and its eval-15 basis warns it "is the entry a mechanical clause-(1) checker is most likely to get
wrong". The added clause listed the published surface as *that method's own* `@DisplayName`/
`@Description`, its table's columns, or the class-level `@Description` — **omitting another method's
`@Description` on the same class**, which is precisely what eval-18 relies on. The clause hard-coded
the error the key warns against.

**A second, separate effect worth keeping.** Where the grader did enumerate (eval-27, all three
passes list every method with a verdict), the residual disagreement became *substantive and visible*:
s1/s2 pass `appliesDimensionalWeightWhenGreaterThanActual`, s3 fails it because the `L*W*H/5000`
volumetric divisor is stated nowhere. On eval-25 the grader still sampled (no enumeration) and split
on the same divisor. So enumeration does what it was meant to; the criterion underneath is what
disagrees.

**That exposes an open question about the key, not the grader.** Clause (1) lists value *kinds* —
threshold, rate, cutoff, multiplier, reference date — and a derivation formula like `/5000` is not
obviously among them. The key discriminates evals 25/27/28 on the **1.15 fragile multiplier** (25
publishes it and passes; 27 and 28 do not and fail) and never addresses the volumetric divisor at
all, though the same method exists in all three. If `/5000` is genuinely needed to predict `12.50`
from `[70,50,10]` and appears nowhere, then by the assertion's own words eval-25 should fail and the
key is incomplete. **Settle this by reading eval-25's artefact before touching either again** — a
disagreement is not automatically the grader's fault.

### The corrected clause — measured 2026-07-26 (`c1`, now the plain baseline)

The surface clause was rewritten to name the whole class, and the answer key's eval-25 entry was
corrected to `false` after reading the artefact. One regrade of the 11 changed evals, then a rebuild.

| | before any change | broken clause | corrected clause (`c1`) |
|---|---|---|---|
| `rule-statable-from-table` | 3/5, 4/5, 3/5 | 1/5, 1/5, 2/5 | **4/5** |
| all 61 comparable slots | 56, 55, 55 | 54, 53, 58 | **58 (95%)** |

Evals 15 and 18 are correct again. They were wrong in all three passes under the broken clause, which
was the specific prediction, so the diagnosis was right and the first wording was wrong.

Three limits on this result:

- **One pass.** `rule-statable` flipped three times in the earlier probe, so 4/5 may be a good draw.
  Consistent with improvement, not proof.
- **58/61 is not a new high overall.** Pass `s3` also reached 58/61 with the broken clause. The clear
  gain is on the targeted assertion, not the total.
- **Eval 25 is unresolved.** The grader now says PASS while the corrected key says FAIL — the
  reverse of before. The artefact shows no `/5000` and no fee band above 5 kg, so FAIL still looks
  right, but this is one observation on a slot that has flipped. Do not change the key or the
  assertion again without a second pass.

Level held: 333/378, inside the earlier 331/334/328.

**Do not add further clauses.** Two rounds of rewording failed before this, and the third round made
its target worse before the correction. If `rule-statable` is still unstable at the next probe, the
answer is to revert the enumeration clause, not to extend it.

### Note for the next re-baseline

Every run during a deliberate re-baseline exits **2**, because the old baseline is stale by
definition and the void-comparison guard is doing its job. Chain re-baseline commands with `;`, not
`&&`, or the first one will stop the rest. `--rebuild` also needs `--evals` when the iteration
directory holds fewer evals than the suite.

## The noise floor on the CURRENT instrument — measured 2026-07-26 (`p1`/`p2`)

Three gradings of the same stored outputs under the live regime: `benchmark.json` (318),
`benchmark-p1` (323), `benchmark-p2` (322), instrument `8425750f`. Reproduce with
`node scripts/flip-report.js --skill tabletest --iteration 40 --suffixes ,p1,p2`.

**17 of 365 slots flip; 37 fail in all three.** Against the old instrument's 18 of 365,
**tranche 2 did not reduce instability** — pairwise it is 15 flips against the old series' mean of
12. Tranche 2 bought *accuracy* (its stated aim) and never bought stability, which is the documented
lesson restated as a measurement: sharper wording does not fix a sampling problem.

| Eval | Assertion | plain p1 p2 |
|---|---|---|
| 8 | `minimal-rows-per-concern` | p F F |
| 15 | `2.16-no-duplicate-tier-mapping` | F p p |
| 18 | `concerns-decomposed` | p F p |
| 18 | `depth-premium-boundaries` | F p p |
| 18 | `minimal-rows-per-concern` | p p F |
| 18 | `rule-statable-from-table` | F p p |
| 22 | `description-no-irrelevant-information` | F p p |
| 25 | `consistent-quantity-naming` | p p F |
| 25 | `minimal-rows-per-concern` | F p p |
| 25 | `options-as-map` | F p p |
| 27 | `minimal-rows-per-concern` | F p p |
| 27 | `rule-statable-from-table` | p F p |
| 28 | `rule-statable-from-table` | F p p |
| 29 | `quantifier-covered-by-rows` | p F p |
| 29 | `scenario-names-describe-conditions` | F p F |
| 29 | `type-converters-for-complex-objects` | F p F |
| 30 | `held-constants-declared` | p F F |

`minimal-rows-per-concern` ×4 and `rule-statable-from-table` ×3 are **7 of the 17** — the same two
assertions that topped the old instrument's list. Everything else appears once.

**The baseline is the low draw.** 318 against a three-sample mean of 321. Every cluster measured
against `iteration-40/benchmark.json` is anchored to a favourable sample, so measured wins are
flattered by roughly 3 slots. Prefer per-slot evidence to net score.

### Voting does not help — measured, not assumed

Scoring all three passes against the answer key:

| | accuracy |
|---|---|
| plain | 62/68 = 91.2% |
| p1 | 61/68 = 89.7% |
| p2 | 62/68 = 91.2% |
| **majority-of-3** | **61/68 = 89.7%** |

Zero slots are wrong in all three; twelve are split. But majority-of-3 leaves **seven** wrong —
no better than a single pass at 3× the cost. The split slots are not wobbling symmetrically around
the right answer: the grader is uncertain *and* leans wrong on most of them, so voting converts
"wrong two times in three" into "wrong three times in three". This is the entrenchment warning in a
subtler form than the original measurement found — not *reproducibly* wrong, but *predominantly*
wrong. **`--grade-runs 3` stays reserved.**

At 68 keyed slots a one-slot difference is inside noise, so the claim is "no measurable gain", not
"voting is worse". Note also that single-sample accuracy carries roughly ±2 slots here, so the
recorded progression 89% → 94% → 95% is partly inside its own noise.

### The current outputs — iteration-41, evals 20/26/29/30

Same three-pass treatment on the post-promotion outputs (88 / 90 / 89, instrument `9eb3645a`):
**6 of 96 flip; 4 fail in all three.** Flips: `minimal-rows-per-concern`/26,
`consistent-quantity-naming`/29, `scenario-names-describe-conditions`/29 and /30,
`type-converters-for-complex-objects`/29, `held-constants-declared`/30. Stable failures:
`concern-not-over-split`/26, `minimal-rows-per-concern`/29 and /30, `quantifier-covered-by-rows`/29.

**The answer key cannot score iteration-41.** Its `scored_against.iteration` is iteration-40 and its
first caveat is that entries hold only while the assertion text *and the stored output* are
unchanged. `score-grader.js --iteration 41` would run anyway and return a confident, meaningless
number.

### Why these two assertions flip — read from the grader's own words

Not vagueness. Two different structural faults, diagnosed from the three passes' evidence strings.

**`rule-statable-from-table`: the enumeration is unbounded.** The text fails "if any of these hold
for any `@TableTest`", but nothing makes the grader visit them all, so each pass judges whichever
method it happened to read. On eval-27: `plain` PASSED citing `premium`'s description, `p1` FAILED
citing `appliesFragileSurcharge` and `appliesDimensionalWeight`, `p2` PASSED citing `premium` again.
On eval-28, three passes cited three different methods. The verdict tracks *which method was
sampled*, not the output.

*Proposed fix — force the enumeration into the evidence, not just the criterion:*

> Judge **every** `@TableTest` method in the class. Your evidence must list each method name with
> PASS or FAIL beside it; a verdict reported from a subset of the methods is not a verdict. The
> assertion FAILS if any single method fails. A value counts as published if it appears in that
> method's own `@DisplayName` or `@Description`, in a column of its table, or in the class-level
> `@Description`.

The last sentence closes a second gap: eval-18's `plain` FAIL complained a value was missing from
"this table's own description" while `p1`/`p2` found it in the `@Description` — the two passes
disagreed about which surface counts.

**`minimal-rows-per-concern`: two licensed readings, and it is already the longest assertion in the
suite.** Every past fix added a clause; it is now ~1,400 characters and the noisiest slot we have.

- *Direction.* eval-8: `plain` PASSED; `p1`/`p2` FAILED for "no scale/zero/format boundary rows" —
  a complaint about **missing** coverage under an assertion that polices **excess** rows. Nothing in
  the text forbids that reading.
- *Scope.* eval-25: `plain` FAILED because four sibling surcharge tables each repeat the 7.50
  baseline; `p1`/`p2` PASSED because each table is minimal within its own scope. The text asserts
  per-table minimality and then adds a class-level clause, licensing both.

*Proposed fix — split it, do not lengthen it.* This section's own heading is "split on decidability
boundaries", and the two readings above are exactly such a boundary:

> **`no-duplicate-rows-within-a-table`** — Judge each `@TableTest` in isolation. It FAILS if a row
> re-covers an obligation an earlier row in the *same* table already discharged. A row discharging an
> obligation no other row reaches earns its place however simple it looks; a value set covering
> several values in one row is the preferred discharge, never a failure. **Rows you think are missing
> are never a failure here** — coverage is judged by `2.19-covers-every-tier` and
> `quantifier-covered-by-rows`.
>
> **`no-table-reproves-another`** — Judge the class as a whole. It FAILS if a whole `@TableTest`
> re-proves rules earlier tables already established (the integration or end-to-end table is the
> common case). Sibling tables that each isolate one rule are **not** duplication merely because they
> share a baseline row.

Splitting also unblocks measurement: cluster 2 targets `minimal-rows-per-concern` on five evals, and
today none of those five can be read at n = 1.

**Cost and sequencing.** This is a suite change, so per `AGENTS.md` it lands as its own commit and is
re-baselined before skill iteration resumes — a `--grade-only` regrade, ~13 min / ~$1.83, no
generation. Two costs to plan for: splitting one assertion into two changes every host `eval.json`
and re-fingerprints those evals, and the answer-key rows for both assertions must be **re-read from
the artefacts**, since changing assertion text invalidates them.

## The noise floor, per slot — v1/v2/v3, distilled 2026-07-26

`benchmark-v1/v2/v3.json` are one generation graded three times under one regime (322 / 320 / 327),
on the **pre-tranche-2 instrument** (`42388839`, the same as `iteration-40/benchmark.json`). **18 of
365 slots flip; 33 fail in all three.** `p` = passed, `F` = failed, in v1/v2/v3 order:

| Eval | Assertion | v1 v2 v3 |
|---|---|---|
| 14 | `1.7-readability-scenario-names` | p F p |
| 14 | `minimal-rows-per-concern` | F p F |
| 15 | `rule-statable-from-table` | p F p |
| 18 | `depth-premium-boundaries` | F p F |
| 18 | `minimal-rows-per-concern` | p F p |
| 22 | `minimal-rows-per-concern` | p F p |
| 22 | `scenario-names-describe-conditions` | F p F |
| 23 | `concerns-decomposed` | p p F |
| 23 | `description-no-redundant-field-values` | F F p |
| 25 | `rule-statable-from-table` | F F p |
| 27 | `rule-falsifiable-by-a-row` | p F p |
| 27 | `rule-statable-from-table` | p F p |
| 27 | `scenario-names-describe-conditions` | F p p |
| 27 | `titles-form-a-family` | F F p |
| 28 | `rule-statable-from-table` | F F p |
| 29 | `business-language-columns` | F p p |
| 29 | `quantifier-covered-by-rows` | p F F |
| 30 | `minimal-rows-per-concern` | F F p |

By assertion: `rule-statable-from-table` ×4, `minimal-rows-per-concern` ×4,
`scenario-names-describe-conditions` ×2, and one each of `1.7-readability-scenario-names`,
`depth-premium-boundaries`, `concerns-decomposed`, `description-no-redundant-field-values`,
`rule-falsifiable-by-a-row`, `titles-form-a-family`, `business-language-columns`,
`quantifier-covered-by-rows`.

**A second, unplanned variance pair corroborates it.** `benchmark-t3` and `benchmark-t4` share an
instrument (`804b7490`) *and* a regime, so they are two more identical re-grades: 318 vs 314. A
4-slot gap, consistent with the v-probe's 320–327 range.

**Consequences.** A whole-suite score has a **±3–4 slot run-to-run spread**, so no single run's *net*
is evidence — judge a change by whether its *targeted* slots moved. A moved verdict landing on any
row above needs a confirming re-grade of the same stored outputs before it is attributed.

**This probe is on a dead instrument.** Tranche 2 and the mixed-effort change re-fingerprinted every
eval, so the current instrument (`8425750f`, `benchmark-t5`) has **no variance probe at all**. Three
`--grade-only` passes over iteration-40's stored outputs would cost ~$5.50 and ~40 minutes with no
generation spend. Until that exists, the table above is the best available prior, not a current
measurement.

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

## Assertion ids retired by the 2026-07-31 suite edit

Rows above naming these ids describe measurements that were made; they are kept as measured. The ids
themselves no longer exist, so a fresh grading will report them as `not-hosted`.

| Retired | Replaced by | Eval |
|---|---|---|
| `depth-decision-boundaries` | `decision-claim-cliff` + `decision-cliff-independent-of-age` | 18 |
| `depth-premium-boundaries` | `premium-claim-boundary` + `premium-age-boundary` | 18 |

Both were compound *and* scoped to a table named by role, and **neither had ever passed** — evals 18
ran in iterations 40, 42, 44, 45 and 50 and both failed every time, a standing −2 invisible in the
score history because it never moved. The split gives each clause its own slot and judges it wherever
the rule is derived.

Five texts were retargeted the same way, off a named table and onto the rule: `separates-decision-and-premium`
(18), and `2.1-decomposition-concern-separation`, `2.2-children-flat-discount`,
`2.17-zone-irrelevance-visible`, `2.18-adult-senior-value-set` (15). Five assertions were added to
eval-15 (`three-schemes-distinguished`, `scheme-derived-once`, `period-ticket-excluded-from-count`,
`count-derived-from-raw-history`, `new-purchase-inclusion-published`). Slot totals: eval-15 28 → 33,
eval-18 27 → 29.

Both evals are re-baselined by this and no comparison may span it. Gotcha:
`assertions-that-name-a-table-never-pass`.

### Two slots to watch after the 2026-07-31 split

- **`premium-claim-boundary` (18) is flip-prone from birth.** Its first two gradings on the same
  stored iteration-50 output disagreed (FAIL, then PASS), and the PASS is the correct verdict — the
  0-against-1-claim pair exists across two tables (`computesSeniorPremiumFromRiskScore` at NEW/65/0
  and `selectsPremiumFormulaByAge` at 65 with claims fixed at 1). The first grading simply did not
  assemble the pair. Cross-table pairing is what the retarget deliberately allows, so the fix is not
  to re-scope it; treat one verdict here as provisional and confirm on a re-grade.
- **`new-purchase-inclusion-published` (15) is the first assertion in the suite that tests a join
  between two tables.** Its first wording asked only whether the +1 was published somewhere, which a
  `@Description` discharged while the two tables still disagreed about what their shared column name
  meant. It now fails that shape. Watch it for over-fire on a solution that has no separate count
  table — the text exempts that case explicitly.

### What the cross-iteration regrade established — 2026-07-31 (`tn2` on iterations 40/42/44/45/46/50)

Six stored eval-15 solutions and five eval-18 solutions, re-graded under the repaired definitions.
$2.16, no generation. The question was whether the retargeted and split assertions **discriminate**
between solutions, or merely fail all of them the way the compound originals did.

| Assertion | 40 | 42 | 44 | 45 | 46 | 50 | Discriminates? |
|---|---|---|---|---|---|---|---|
| `2.1-decomposition-concern-separation` | F | F | F | F | **P** | F | 1 of 6 |
| `three-schemes-distinguished` | F | F | F | F | F | F | **no — 0 of 6** |
| `scheme-derived-once` | F | P | P | P | P | P | 5 of 6 |
| `2.17-zone-irrelevance-visible` | F | F | F | **P** | **P** | F | 2 of 6 |
| `new-purchase-inclusion-published` | P | P | P | P | P | **F** | 1 of 6 |
| `2.2`, `2.18`, `period-ticket-excluded-from-count`, `count-derived-from-raw-history` | P | P | P | P | P | P | no — 6 of 6 |
| `separates-decision-and-premium` | F | F | **P** | F | — | F | 1 of 5 |
| `decision-claim-cliff` | F | F | F | F | — | F | **no — 0 of 5** |
| `decision-cliff-independent-of-age` | F | F | F | F | — | F | **no — 0 of 5** |
| `premium-claim-boundary` | F | F | F | F | — | **P** | 1 of 5 |
| `premium-age-boundary` | P | P | P | P | — | P | no — 5 of 5 |

**The retarget works.** Four assertions that the table-scoped wording could not vary now vary, and
each flip is attributable to a specific structure: iteration-44 is the only eval-18 output whose
decision table carries no premium column; iteration-45 and -46 are the only eval-15 outputs putting
zone value sets where the percentage is decided; iteration-46 is the only one dispatching on category
without re-deriving the ladder.

**No firm answer-key entry was contradicted.** `2.17`/15 and `separates-decision-and-premium`/18 both
stay FAIL on iteration-40, as the key records, and the `depth-premium-boundaries` split reproduces
the key's own basis — age boundary covered, claim boundary not.

**Three assertions still never pass, and the reason is now a measurement rather than a defect.**
This is the distinction that matters against the retired `depth-*` pair: those never passed because
they were compound and table-scoped, so no output could satisfy them as written. These three are
single-clause, judged wherever the rule is stated, and **verified achievable against the domain**:

- `three-schemes-distinguished` (0 of 6) — no solution models the ticket type of the *purchase being
  made*; all six treat ticket type only as a property of past purchases. The prompt says Reis applies
  to single tickets, so a period-ticket purchase getting no discount is derivable.
- `decision-claim-cliff` and `decision-cliff-independent-of-age` (0 of 5) — `riskScore = age/10 +
  claims*15`, rejected above 75. So **4 claims approves at every realistic age** (rejection would need
  age > 150) and **5 claims rejects at every age ≥ 10**. The cliff is the observable rule. All five
  solutions instead pick age 9 to sit exactly on 75 — an unrealistic applicant age chosen to probe the
  internal threshold. Four of the five say so in a description ("not meant to represent a realistic
  applicant age", "no minimum applicant age is enforced").

That last one is a repeated skill gap worth teaching, not an assertion to soften: the outputs reach
for the implementation's boundary instead of the rule's. Same family as `black-box-columns` and
`no-reimplemented-internals`, showing up in row choice rather than column choice.

**One design defect in the new eval-15 ladder.** `three-schemes-distinguished` was authored as the
floor and `scheme-derived-once` as the ceiling, but measured across six solutions the floor passes 0
and the ceiling passes 5. The rungs are ordered wrong. `2.1` (1 of 6) is the genuinely hard middle.
Either relabel, or accept that the floor is really a headroom assertion.

### Re-hosting the group-confined assertions — 2026-08-01 (slice 4 step 2)

Cutting evals 26–28 would strand six assertions on eval-25. Only **two of the six could actually be
re-hosted**, and the reason the other four could not is a constraint worth stating once.

| Assertion | Re-hosted to | Verdict on stored outputs |
|---|---|---|
| `concern-not-over-split` | 14, 18, 29 | 14 PASS · **18 FAIL** · 29 PASS |
| `titles-form-a-family` | 15, 29, 30 | 15 PASS · 29 PASS · 30 PASS |

**`concern-not-over-split` earned its transfer immediately.** On iteration-50's eval-18 it fails, and
correctly: `computesStandardPremiumFromRiskScore` (age fixed at 30) and
`computesSeniorPremiumFromRiskScore` (age fixed at 65) vary the same input, produce the same
`Premium?` column, and differ only in the held age. One table with an `Age` column and six rows says
the same thing — a handful, not a cross-product, so the assertion's own collapse guards are met. That
is a real design defect the suite could not see before, and it is new attributable headroom on
eval-18.

**No contradiction with `separates-decision-and-premium`,** which permits splitting premium across
several tables. One decision table plus one premium table carrying an `Age` column satisfies both.

**The four that could not move, and the general rule.** `options-as-map`, `options-type-converter`,
`dimensions-as-list` and `numeric-types-correct` all depend on eval-25's *fixture*, and the grader is
never shown a fixture — see the gotcha `grader-never-sees-the-fixture`. Concretely: eval-29 already
carries `coupon-as-single-column` and `type-converters-for-complex-objects`, so the first two would
have been near-duplicates in a second wording; no eval in the suite has a fixed-arity same-kind tuple
for `dimensions-as-list` to judge; and `numeric-types-correct` **passed vacuously on eval-29** with
the evidence "no src/main API signatures were provided", so it was removed again.

**All four survive on eval-25 and lose nothing by the cut.** Their four hosts were evals 25–28 —
*the same task in four costumes* — so they have never demonstrated transfer and cutting three cannot
take away what was not there. Only genuinely domain-neutral assertions can be re-hosted, which is
exactly the two that were.

### F2 — `options-as-map` no longer licenses the broken form (2026-08-01, slice 4 step 5)

The old text ended "Rows with no options use a blank cell **or** `[:]`". A blank cell converts to
null *before* converter lookup (`products/core/gotchas/blank-cell-bypasses-type-converter.md`), so it
bypasses the `@TypeConverter` and hands the method null rather than a defaulted `PackageOptions`. The
assertion therefore licensed the one form that cannot work — **and contradicted its own eval's ground
truth**, which says so outright at `expected_output.md:40-44` ("The no-options row must be `[:]`
(empty map), not a blank cell").

Now decidable in both directions: FAILS on separate sparse columns, and FAILS when an options column
exists but its no-options row is blank.

**Re-graded both stored solutions to check the tightening broke nothing.** Neither verdict moved —
iteration-50 PASSes (real converter, `[:]` row), iteration-45 FAILs (three sparse columns, no
converter). What did change is the *evidence*: iteration-45's now reads "with blank cell for 'No
special handling', not a single `[:]` map column", naming a defect the old wording had permitted. Same
verdict, complete reason.

**The repair is preventive, not corrective** — no stored output was passing on the licensed form, so
nothing was being scored wrong today. It closes the hole before an output finds it.

**One coupling to watch:** the assertion now states current core behaviour, and that behaviour is
under review (`TODO.md`, core lane: "a blank cell never reaches a `@TypeConverter`" — decide + pin).
If core changes so blanks reach converters, this text must change with it.

### Seven of eval-20's syntax assertions converted to checkers — 2026-08-01 (slice 4 step 6)

`list-syntax-correct`, `set-syntax-correct`, `empty-list-explicit`, `special-chars-quoted`,
`pipe-quoted`, `no-blank-collection-elements` and `newline-in-cell` are now `deterministic`. All
seven agree with the LLM verdicts they replace — eval-20 stays 17/17 — so the conversion moved no
slot, which is the only acceptable outcome for a regime the suite compares across.

**A naive cell split cannot grade this eval.** The old idiom, `split("|").filter(c => c.length > 0)`,
breaks on the exact values eval-20 exists to test: it shreds `["tech:milestone|v2", "biz:sales"]` at a
pipe that is data, and it silently drops blank cells. The new `splitRowCells` tracks `[]`/`{}` depth
and quotes, and preserves blanks.

**Two things had to be read from the declared parameter types, not guessed from the cell:**

- `{tech, business, urgent}` on a `String` column is a **value set** that runs the row three times,
  not a `Set` literal. Only the declared type tells them apart, so `set-syntax-correct` checks
  Set-typed columns only.
- An unquoted colon is a defect **only in a List or Set column**, where `[tech:java]` parses as a map.
  In a Map column the colons are the syntax.

**The scenario column is optional, and assuming otherwise is an off-by-one that looks like a real
finding.** The first draft mapped column i to parameter i−1 always. On `sumsListElements(List<Integer>
numbers, int sum)` — two columns, two parameters, no scenario column — that made the `Sum?`
expectation column look like the List column, and the checker reported the skill's own correct
example as a violation. A table has a scenario column when it has one more column than the method has
parameters; anything else is guessing.

**Wired into `lint-skill-examples.js`, and it immediately found a real defect.**
`common-patterns.md:286` published a table using end-of-row `//` comments. `RowParser.line()` is
`either(comment(), row())` — **a comment must be a whole line**, so the trailing text was a fifth cell
against a four-column header. The example taught syntax the parser rejects. Fixed to whole-line
comments in the same commit.

**`empty-string-element-quoted` was a duplicate, and is now `empty-tag-case-covered`.** Its decidable
core — a blank element inside a collection — was *identical* to `no-blank-collection-elements`, so
converting both would have created two slots that can never disagree: one slot counted twice, the same
error as four conversion evals hosting one task.

**Resolved by rewording rather than deleting, because the prompt states a rule the suite was not
covering.** eval-20's prompt says "An empty tag string is never kept, whatever the category", and no
assertion asked whether any row exercises it. The slot now judges **coverage** — does a row feed an
empty tag in at all — and explicitly hands the syntax question back to `no-blank-collection-elements`.
Renamed because the id should say what it tests; it had no answer-key entry, so nothing was bound to
the old name.

### The two assertion repairs — 2026-08-01 (slice 4, batched with steps 2–6)

**`description-if-present-adds-information` flip-flopped on the same bytes.** iteration-49 failed
eval-7 for having no `@Description`; iteration-50 passed the identical output, calling it "vacuously
satisfied". The permission to omit was in the text all along — as the *last* sentence, after the text
had opened "If @Description is present…". The repair leads with the decidable case instead:

> PASSES when no @Description is present anywhere in the class — omitting it is always acceptable,
> and this assertion NEVER penalises its absence.

Applied to all five hosts (1, 2, 7, 8, 9), whose texts had drifted only in their per-eval example.
Re-graded: all five PASS, **no verdict moved**, and the three no-`@Description` outputs now pass with
evidence naming the absence rather than a coin-flip.

**`annotation-order` passed vacuously, and the fix was not where the plan expected.** An output
writing no `@DisplayName` and no `@Description` has nothing to order, so it passed with the evidence
"Annotations in correct order" — which reads as a win when the real change was the annotations
disappearing (iteration-49 recorded exactly that on eval-20). The checker now counts the methods it
actually ordered and returns a marked verdict when that count is zero:

> VACUOUS: no @TableTest method carries a @DisplayName or @Description, so there was no ordering to
> check. This pass is not evidence that ordering improved.

**Deliberately still a PASS.** Requiring the annotation would contradict `has-descriptive-title`,
which accepts a descriptive method name instead — so the honest fix is to make the vacuity legible,
not to invent a failure. The residual limit is real and worth stating: the slot can still move
FAIL→PASS when annotations vanish. The evidence now says so in the report.

**It moved no fingerprints, which the plan predicted it would.** The sequencing note expected this
repair to re-fingerprint 15 of 17 evals. It re-fingerprinted **none**: the defect was in
`scripts/assertions.js`, and the fingerprint covers `prompt.md`, `eval.json`, `expected_output.md`
and `project/` only. **Check where a repair actually lives before sizing its blast radius** — a
checker fix is free, an assertion-text fix costs a re-baseline on every host.

## Variance probe on the cut suite — predictions pre-registered 2026-08-01, before results

Three identical re-grades (`vp1`/`vp2`/`vp3`) of stored outputs across all 14 live evals —
`iteration-50` for 12, `iteration-51` for evals 29 and 30. ~$6, no generation. **First probe with the
grader's reasoning captured** (`grading-vp*-thinking.json`), so each flip can be read rather than
counted.

**Why the full suite rather than the known flippers.** The six evals a flipper-only probe would drop
(1, 2, 7, 8, 9, 20) cost **$0.14 per pass** in grading — the saving over three passes is $0.42.
Against that, a subset probe can confirm known flippers but cannot **discover** newly-unstable slots,
which is the thing most likely to have changed: the 4.9% baseline was measured on `iteration-40`'s
outputs under a v1.6.0-era skill, and `consistent-quantity-naming` has already flipped once after
being recorded as stable. A subset also breaks the denominator, so the rate would not be comparable
to 4.9%.

**Predictions, recorded before the results:**

1. **`rule-statable-from-table` stays the top flipper.** 4 flips in the v1/v2/v3 probe, 3 more between
   the anchor's two gradings. If it is not the worst slot here, something material changed.
2. **The seven converted eval-20 assertions contribute zero flips** — true by construction, so this
   is a check on the conversion, not a finding. A flip there means a checker reads mutable state.
3. **`premium-claim-boundary` (eval-18, new) flips.** It already disagreed across its first two
   gradings, where the second found a cross-table pair the first missed.
4. **`1.7-readability-scenario-names` (eval-14) flips.** Observed once already between the `rh` probe
   and the sweep — and `vp1` came in at 20/23 against the sweep's 19/23 before this was written.
5. **The overall rate lands at or below 4.9%**, since seven llm slots became deterministic and the
   suite shrank. **A materially higher rate would mean the new assertions are noisier than what they
   replaced** — the outcome that would most change what to do next.
6. **At least one slot flips that is on no prior list.** The outputs are two skill versions newer than
   the ones the 4.9% figure was measured on.

**What each outcome implies.** A slot that flips *and* whose two reasonings cite different parts of
the output needs a scoping fix (name the surface). One whose reasonings cite the same evidence and
reach opposite conclusions needs a decidability fix (or a deterministic checker). That distinction is
what the captured reasoning is for, and it is the first time it has been available.

### Variance probe RESULT — 2026-08-01 (`vp1`/`vp2`/`vp3`, cut suite)

Three re-grades of one generation, `claude-sonnet-5/default`, same instrument across all three.
`iteration-50` 231/233/233 of 254 · `iteration-51` 54/53/55 of 58.

**9 of 312 slots flip = 2.9%**, against **18 of 365 = 4.9%** on the pre-cut instrument. **21 fail in
all three.**

| Eval | Assertion | vp1 vp2 vp3 |
|---|---|---|
| 14 | `no-table-reproves-another` | F p p |
| 15 | `quantifier-covered-by-rows` | F p F |
| 18 | `no-duplicate-rows-within-a-table` | F F p |
| 18 | `premium-claim-boundary` | p F F |
| 23 | `description-no-redundant-field-values` | F p p |
| 29 | `business-language-columns` | p F p |
| 29 | `rule-falsifiable-by-a-row` | F F p |
| 29 | `uses-standard-map-syntax` | F F p |
| 30 | `native-collection-output` | p p F |

**Caveat on eval-18.** Its `vp3` was graded with `--capture-thinking` off while `vp1`/`vp2` had it on
(the retry straddled the code change), so eval-18's third pass is a different regime. Its two flips
are established by the `vp1`/`vp2` disagreement alone; excluding eval-18 entirely gives 7 of 282 =
**2.5%**, so the headline conclusion does not depend on it.

#### Predictions scored (registered `621c2df`, before results): 4 of 6 correct

| # | Prediction | Outcome |
|---|---|---|
| 1 | `rule-statable-from-table` stays top flipper | **WRONG — it flipped zero times, on any host** |
| 2 | converted eval-20 assertions contribute zero flips | correct (eval-20 17/17 in all three) |
| 3 | `premium-claim-boundary` flips | correct |
| 4 | `1.7-readability-scenario-names` flips | **WRONG — stable within-regime** |
| 5 | rate ≤ 4.9% | correct (2.9%) |
| 6 | ≥1 flip on a slot from no prior list | correct (`uses-standard-map-syntax`, `native-collection-output`) |

**Prediction 1 failing is the most useful result here.** `rule-statable-from-table` was the worst slot
in the suite — 4 flips in the v1/v2/v3 probe, 3 more between the anchor's two gradings — and it is now
**completely stable across five hosts and three passes**. The intervening change was the per-method
enumeration clause: *"your evidence must list each method name with PASS or FAIL beside it; a verdict
reported from a subset of the methods is not a verdict."* **Requiring exhaustive enumeration is a
demonstrated repair**, not a hypothesis.

**Prediction 4 failing sharpens what a flip means.** `1.7-readability-scenario-names` moved between the
*sweep* and the probe but never within the probe — that is a cross-regime difference, not grader noise.
Do not add a slot to the flip list on the strength of two runs from different regimes.

#### The diagnosis, which counting alone could not give

Each flip carries the grader's own reasoning (`grading-vp*-thinking.json`). Read across the
disagreeing passes, **7 of 9 are the grader looking at a different part of the output — not judging
the same evidence differently:**

| Slot | vp_x cites | vp_y cites | Fix |
|---|---|---|---|
| 14 `no-table-reproves-another` | an end-to-end method duplicating a row | that the method is a plain `@Test`, out of scope | scope: say plain `@Test` is excluded |
| 15 `quantifier-covered-by-rows` | an **uncovered** claim (zone/category) | a **covered** one (CHILD value set) | enumerate every quantifying claim |
| 18 `premium-claim-boundary` | the legitimate cross-table pair | a "same applicant type" rule not in the text | state the pair may span tables |
| 23 `description-no-redundant-field-values` | **scenario names** | the **`@Description`** | name the surface |
| 29 `business-language-columns` | `Product Id` a "minor nit" | `Product Id` a code-ism | decidability: is `Product Id` in or out |
| 29 `rule-falsifiable-by-a-row` | `appliesCoupon`'s constant column | `calculatesCartTotal`'s varying one | judge every table, not one |
| 29 `uses-standard-map-syntax` | the **converter body** special-casing a key | the **cell syntax** being standard | name the surface |
| 30 `native-collection-output` | native map-of-set output | hand-rolled `"a:b:c"` strings | both true of different columns — enumerate |

**Only `business-language-columns` is a genuine judgement split** — both passes saw `Product Id` and
disagreed on whether it counts. Everything else is a *scoping* defect: the assertion does not say
which surface or how many places to look, so two passes sample different evidence and both reason
correctly about what they happened to see.

**What to do, in order.** Apply the enumeration clause that fixed `rule-statable-from-table` to
`quantifier-covered-by-rows`, `rule-falsifiable-by-a-row` and `native-collection-output`; name the
surface on `description-no-redundant-field-values` and `uses-standard-map-syntax`; add the scope
sentence to `no-table-reproves-another` and `premium-claim-boundary`; decide `Product Id` explicitly
for `business-language-columns`. **All eight are wording fixes with a diagnosed cause** — none needs a
better grader, and none is a candidate for voting, which was already measured not to help.

### The repair, and what one pass of it measured — 2026-08-01 (`fx`, commit `b59dea7`)

All nine flippers repaired by wording. **All nine landed on the verdict the eval's own
`expected_output.md` supports.** One pass, so this measures *level* — that the slot now reads
correctly — and not yet stability. Live suite after the re-baseline: **285/312** (`iteration-50`
232/254, `iteration-51` 53/58); not comparable to the 285/312 before it, because the definitions
moved underneath.

| Eval | Slot | Probe | Repaired to | Fix applied |
|---|---|---|---|---|
| 14 | `no-table-reproves-another` | F p p | **p** | scope: @TableTest only, a plain @Test cannot fail it |
| 15 | `quantifier-covered-by-rows` | F p F | **F** | enumerate every quantifying claim |
| 18 | `no-duplicate-rows-within-a-table` | F F p | **F** | decidability: the third-sample-of-one-effect case |
| 18 | `premium-claim-boundary` | p F F | **p** | enumerate the risk-derived rows before pairing |
| 23 | `description-no-redundant-field-values` | F p p | **p** | report both surfaces; threshold exemption covers clause (1) |
| 29 | `business-language-columns` | p F p | **p** | decidability: judge header *form*; `Product Id` PASSES |
| 29 | `rule-falsifiable-by-a-row` | F F p | **F** | enumerate every method **and every expectation column** |
| 29 | `uses-standard-map-syntax` | F F p | **p** | surface: cell text, not the converter body |
| 30 | `native-collection-output` | p p F | **p** | surface: expectation columns, not the input shorthand |

**Two of the nine were defects in the ground truth, not the assertion.** `expected_output.md` is in
the grading prompt, and two of its lines contradicted the assertion the grader was being asked to
apply: eval-29 called `Product Id` "the kind of small polish `business-language-columns` exists to
catch" while also saying not to weight it — which is exactly the split — and eval-30 said a
string-encoded output "passes today because no assertion rewards native collections", which
`native-collection-output` does. **Read the expected output before rewriting an assertion**; a slot
can flip because the two halves of the grading prompt disagree.

**The enumeration clause now has three demonstrations, and one refinement.** On
`rule-falsifiable-by-a-row` the per-method form was not enough: the passing pass had named every
method and still missed that one method's `Cart After?` column was constant across all four rows.
Requiring *each method and each of its expectation columns* is what fixed it. **Enumerate at the
granularity the condition applies to, not at the method.**

**One slot moved that was never edited**, and it is the useful control: eval-14
`1.3-depth-overtime-boundary`, PASS in all three probe passes, FAIL here. It sits in a grading batch
containing none of the edited assertions, so its prompt is **byte-identical** to the probe's, and all
four gradings quote the same evidence — rows at 40 and 45 against an assertion asking for "41 or
similar". That is a fourth draw catching a flip three passes missed. Two things follow: **a
three-pass probe under-counts the flip list** (it found 9; a fourth pass immediately found a tenth),
and this slot needs the same decidability fix — say what "just above" means as a number.

**Not yet measured:** whether the repairs removed the *instability*. That needs a fresh multi-pass
probe on the new texts and is a separate window. Also note `docs/grader-answer-key.json` is bound to
`iteration-40` and to the pre-repair assertion texts, so `score-grader.js` **cannot** score these
nine — the check above is a read against each eval's expected output, not an accuracy score.

**One repair needed a second cut, and the correction is the reusable part.** On eval-22 the new
enumeration clause found a quantifying claim all three probe passes had denied existed ("No
quantifying language ... appears") — then failed it by demanding a "one present, one absent" row,
which is cross-multiplying two domains. The text already said it "never requires" that, but as an
aside at the end of a sentence. **A rule the grader must apply at the moment of decision has to sit
with the decision, not in a trailing clause** — moved to a decidable test in `ef5bef6` and eval-22
returned to 29/29 with eval-15 still correctly FAIL. Same lesson as the front-loaded
`description-if-present` clause.

**Naming a surface moves a defect to the assertion that owns it — check the neighbour.** Scoping
`uses-standard-map-syntax` to cell text turned it PASS on eval-29, and `coupon-before-after-columns`
went PASS → FAIL on the same output: the coupon state buried in the Cart map is a *column-design*
defect, which is what that assertion is for. `consistent-quantity-naming` also went PASS → FAIL, on
`Cart Before` in three tables against `Cart` in two — the assertion's own worked example. eval-29
reads 27/32 rather than 29/32 and **both new failures are correct**, so this is accuracy gained, not
a regression. When a scoping fix stops one assertion punishing something, verify the right assertion
picks it up.

**The tenth flipper, fixed the same way.** eval-14 `1.3-depth-overtime-boundary` asked for a value
"just above (41 or similar)" and never said what that meant as a number. Three probe passes read the
output's 45 as "similar" and a fourth did not, **on a byte-identical grading prompt** — it shares a
batch with none of the repaired assertions, so the assertion text was the only thing that could have
decided it, and it declined to. Replaced with the test the expected output already implies ("the
40/41 pair shows where overtime starts"): one row at exactly 40 and one greater than 40 and no
greater than 41, in the same column of the same table; 42 or above discharges nothing, because 41–44
could still be regular under a different threshold. The verdict is unchanged (40 and 45 still FAIL,
eval-14 still 19/23) — **this buys stability, not level** — and the grader's evidence now reads back
the test mechanically: *"no row with value >40 and <=41 exists"*.

**Give a boundary assertion an interval, not an adverb.** "Just above", "near", "close to" and "or
similar" are the shape to look for; every one of them is a slot waiting to flip. Write `> 40 and
<= 41`.

**Promoted.** `iteration-50` and `iteration-51` rebuilt from stored gradings and renamed to the plain
name; `check-baseline.js` exits 0 with all 14 evals live at **285/312**. Total grading spend for the
repair cycle: **$1.90** across both iterations, no generation.

**One process note worth keeping.** `git mv` fails silently-ish on an *untracked* file — the freshly
written `grading-fx2.json` was not yet in the index, so the promotion rename did not happen and the
rebuild reassembled from the stale `grading.json` while stamping it with the *current* fingerprint.
Score and verdict were identical so nothing looked wrong; only the evidence string differed. **After
promoting a regrade, grep one promoted `grading.json` for a phrase only the new assertion could have
produced** — the fingerprint guard will not catch this, by design (it protects comparison, not
regrade validity).

## Porting the repaired texts to the two thin suites — measured 2026-08-01 (`benchmark-s6b.json`)

Slice 6 § 0b unified two assertion ids across the three suites from one source
(`shared/assertions/`, rendered per suite by `scripts/build-skills.js`) and tagged five mechanical
`spec-by-example` assertions `deterministic`. The regrade holds the outputs, the grader model and the
prompts fixed and moves only the assertion texts, so it is a **clean A/B of the instrument**.

**Read the in-iteration pair, not the report.** `analysis-todo-s6b.md` compares against
`iteration-1`, which is void by regime, and its 25 moved lines mix the regime change with this one.
The comparison that means something is `benchmark.json` against `benchmark-s6b.json` inside
`iteration-2`: **110/130 → 105/130, 13 slots moved.** `table-driven-testing` is 42/44 → 42/44 with
**nothing moved** — the port cost it nothing, as a 42/44 tripwire should behave.

**The level fell and the accuracy rose.** Every changed-text verdict I read against the artefact is
correct in the new run and wrong in the old one.

| Slot | Old verdict | New verdict | Read against the artefact |
|---|---|---|---|
| `scenario-names-describe-conditions`/4 | PASS | **FAIL** | 'Senior applicant, mid-range score only qualifies due to age' beside `Approved? = yes`. Correct. |
| `scenario-names-describe-conditions`/21 | PASS | **FAIL** | 'Standard registration, no discount' beside `Early-Bird Applies? = no`, `Group Discount Applies? = no`. Correct. |
| `no-duplicate-rows-within-a-table`/5 | PASS | **FAIL** | '31 days' and '90 days' both discharge 'past boundary → no'. Correct. |
| `no-duplicate-rows-within-a-table`/24 | PASS | **FAIL** | 39/40/41 pin the boundary; 'Well into overtime, 50' re-shows the same arithmetic. Correct — it is the worked example in the assertion's own text. |
| `no-duplicate-rows-within-a-table`/10 | PASS | **FAIL** | 0h/12h/23h59 all restate 'no before the boundary'. Correct. |
| `no-duplicate-rows-within-a-table`/4 | FAIL | **PASS** | Old evidence complained the rows were *not decomposed into per-concern tables* — a `concerns-decomposed` judgement under an excess-rows assertion. |
| `no-duplicate-rows-within-a-table`/16 | FAIL | **PASS** | Old evidence: 'Table 1 **needs** 9 rows … since concerns weren't separated'. It failed a table for rows it had just called necessary. |

**Two failure shapes, both cured by the same edit.** The old 192-character text produced *direction*
errors (a decomposition or coverage complaint filed under an assertion that polices excess) and
*shallow* passes — eval-24's entire old evidence is "Table 1 has 5 rows, Table 2 has 4 rows, Table 3
has 3 rows, Table 4 has 3 rows", a row count with no judgement in it. Both are the confirming-instance
stop this document has recorded before. The replacement states the excess-only scope in capitals,
names the boundary-versus-linear-sample distinction, and requires per-table PASS/FAIL evidence — and
the new evidence strings cite specific row pairs every time.

**A short assertion does not fail safe.** It fails *quietly*: it passes on the first good example and
its evidence reads like a finding. Two of the five new FAILs above sat behind an old PASS whose
evidence was a list of *correct* names.

**6 untouched slots also moved, and 4 of them are one flip.** eval-16's `concerns-decomposed`,
`3.2-depth-fulfillment-scenarios`, `3.3-depth-delivery-address-scenarios` and
`3.4-depth-availability-scenarios` all turn on a single contested reading — whether Table 1 is a
legitimate combined table or three concerns merged. The old run read it merged (decomposition FAIL,
depth PASS); the new run reads it combined (decomposition PASS, depth FAIL). **Count that as one
unstable judgement with four consequences, not four flips**, or eval-16 will look four times noisier
than it is. `extreme-discount-row`/6 and `refund-table-shows-proportion`/10 are ordinary single
flips. Nothing here was edited, so the batched grading prompt is the only channel: changing one
assertion's text changes the prompt its batch-mates are judged in.

**The fix for eval-16 already exists and has a price.** `tabletest`'s `concerns-decomposed` text
carries the clause sbe's lacks — "closely-related concerns may share one method … four methods
covering five concerns is fine" — which is exactly the contested reading. Porting it is one file in
`shared/assertions/`, but `concerns-decomposed` holds **5 divergent texts inside `tabletest`**, so
unifying it rewrites `tabletest` assertion texts and forces a `tabletest --grade-only` regrade.
Same for `business-language-columns` (8 texts). Schedule those two deliberately.

**Cost.** $0.81 grading for 10 `spec-by-example` evals, $0.13 for 5 `table-driven-testing` evals, no
generation. Both runs exit 0 — the void-regime guard fires on the *report's* iteration-1 comparison
and warns rather than exiting 2, because the baseline is pre-guard.

## Two spec-by-example slots that flip — five observations each, from `iteration-14` (2026-08-08)

**`refund-table-shows-proportion`/10 is not a standing skill-versus-assertion conflict.** The slice-8
notes describe it as one the skill wins by instruction, so the assertion always loses. Five runs say
otherwise: **PASS (it-2), fail (it-3), PASS (it-4), fail (it-9), PASS (it-14)** — 3 of 5 pass, and
`iteration-14` passes with an explicit `Unused Days?` column between the inputs and `Refund Amount?`.
The grader's evidence tracks the artefact both ways (it-9: *"no proportion/rate column shown"*;
it-14: *"'Unused Days?' as an intermediate column … showing the proration step explicitly"*). **Stop
citing it as a standing loss.** It is a coin-flip on whether the run exposes the intermediate, which
is a skill question, not an assertion defect. Nothing to repair on the assertion side.

**`question-mark-only-on-outputs` flips on both its hosts.** eval-12: PASS/fail/PASS/PASS/fail/PASS
across it-2/3/4/9/13/14. eval-10: PASS/PASS/PASS/fail/PASS across it-2/3/4/9/14. The rule that owns
the convention (13, *Name Expectation Columns*) was untouched by the whole slice-8 batch, so neither
direction is attributable to a skill change. **Six flips across two hosts puts it in the same class
as `scenario-names-describe-conditions`** — read it against the baseline's own evidence every time,
and never quote a single observation as a result.

Neither is scheduled for an assertion edit. Both are recorded so a future run does not spend on
explaining a move that is noise.

## `consistent-quantity-naming` cannot see a before/after pair — reviewed 2026-08-08 (eval-29, `iteration-79`)

**The grader is applying the text correctly; the text is wrong.** The assertion's own FAILS list
names this exact pair as a worked counter-example: *"or 'Cart Items' in one table and 'Cart Before'
in another for the same input"*. Nothing the grader could have done would have passed it.

**The artefact splits on a rule, with no crossover.** In `iteration-79`'s `ShoppingCartTest`:

| Column head | Tables | Also has `Cart After?` |
|---|---|---|
| `Cart Before` | `addsItemsPricedFromTheCatalogue`, `removesItemsFromTheCart`, `appliesCouponsToTheCart` | yes, all three |
| `Cart Items` | `computesCartTotalByCouponType`, `floorsCartTotalAtZero`, `verifiesStockAtCheckout` | no, none |

Three tables mutate the cart and head the input `Cart Before` beside a `Cart After?` expectation;
three read it and head it `Cart Items`. **The pre-state of a transition is not the same quantity as
a read-only input** — it only exists where there is a post-state — so the two names carry
information rather than drift.

**The assertion also cannot see the improvement it should reward.** `iteration-65` had *three*
names, including a bare `Items` in one table, which is genuine drift and a correct FAIL.
`iteration-79` has two, on a stated rule. **Same verdict for a materially better artefact** — the
failure mode this document calls a shallow pass, in the other direction.

### The edit, and what it costs

Replace the cart clause in the FAILS list with a decidable carve-out. Keep the `Base Rate?`/`Rate?`/
`Total Cost?` example — that one is real drift.

> …FAILS when one output quantity appears under two or more names in sibling tables (e.g. 'Base
> Rate?' in one table, 'Rate?' in a second and 'Total Cost?' in a third for the same computed cost).
> A genuinely different quantity may of course have a different name — a base rate before surcharges
> and a final total are two quantities. **A before/after pair is not a second name for the input: a
> table that mutates a value may head its input 'X Before' beside an 'X After?' expectation while a
> table that only reads the same value heads it plainly. The check is the post-state column — 'Cart
> Before'/'Cart After?' in the mutating tables and 'Cart Items' in the read-only ones is one naming
> rule, not two names. It FAILS only when a third name appears, or when a 'Before' name is used in a
> table that has no matching 'After?' column.** PASSES when…

The check is mechanical: *does this table have the matching `After?` column?*

**Cost: two fingerprints.** `consistent-quantity-naming` is carried by **eval-25 and eval-29 with
byte-identical text**, and assertions feed `computeEvalFingerprint`, so editing it voids both evals'
comparisons until re-baselined. The cheap path is the one used for the 2026-08-01 port: a
`--grade-only` regrade with `--grading-suffix` over the **existing** outputs — grading API spend
only, no generation and no subscription quota. Note eval-25's live baseline is `iteration-78`, which
is truncated, so its re-baseline is unsound for a different reason and should be handled separately.

**APPLIED 2026-08-08**, closing run complete. One wording change against the draft above: *"Such a
pair FAILS only when…"* rather than *"It FAILS only when…"* — the bare pronoun reads as governing the
whole assertion, which would license the two-name `Base Rate?`/`Rate?` drift the first clause exists
to catch.

**MEASURED 2026-08-08** — `iteration-79 [cqn]` and `iteration-78 [cqn]`, regrades of the stored
outputs, $0.61 of grading and no generation. **The fix works on its host:** eval-29's slot went FAIL →
PASS and the new evidence field states the carve-out unprompted — *"'Cart Before'/'Cart After?' used
consistently in mutating tables (add/remove/coupon), while read-only tables use 'Cart Items'
consistently"*. eval-25's slot stayed PASS, as registered: it has no before/after pair, so the clause
is inert there.

**Both totals moved anyway, and both moves are batch-mate contamination.**
`description-not-redundant-with-scenarios` went PASS → FAIL on eval-29 and `concern-not-over-split`
FAIL → PASS on eval-25, on byte-identical artefacts. Assertions grade ten to a call
(`LLM_GRADING_BATCH_SIZE = 10`) and this text grew 871 → 1192 chars; eval-29's flipper shares batch 2
with it. `run-evals.js:578` names this as the accepted price of the calibration. **Consequence for
any future text edit: work out the batch and register its members as noise before the regrade** —
procedure now in `docs/grader-tuning.md` § Measuring whether a change worked. `description-not-redundant-with-scenarios`
had never been recorded as flipping before; this is its first observation and it is not evidence
about the artefact.

**The answer key moved with it.** `docs/grader-answer-key.json`'s eval-29 entry flipped `false` →
`true`: iteration-40's artefact has the same rule as iteration-79's (`Cart Before` beside `Cart
After?` in both mutating tables, `Cart Items` in the two read-only ones, no third name), so the
corrected text passes it. The old entry was right under the old text; it is the text that changed.
**A key entry that quotes an assertion's worked example dies with that example** — check the key
whenever an assertion's examples are edited, not only when its criterion is.

## eval-8's two moved slots are both defective — read 2026-08-08 (`iteration-81`)

Neither is evidence about the skill, and both were caught by the evidence-field check before any
attribution.

- **`no-duplicate-rows-within-a-table` — FAIL whose own evidence says PASS.** The verdict reads
  *"both tables PASS: parsesAmountsIntoMoney has no repeated rows, rejectsInvalidAmounts has no
  repeated rows"* and the slot is marked failed. A contradiction inside one verdict, not a judgement
  call. No text fix is available from one instance: the criterion is already decidable and the grader
  applied it correctly, then reported the opposite. **Watch for a second instance before touching the
  text** — if it recurs, it is a response-level sampling problem (`docs/grader-tuning.md` § Measuring),
  not wording.
- **`description-if-present-adds-information` — vacuous PASS.** `MoneyParserTest` has no
  `@Description` anywhere, so the conditional passes on an empty antecedent. This is the same
  conditional-assertion hole already recorded for this eval on `iteration-70`, where the grader went
  the other way and quoted a `@DisplayName` as the description. **The assertion is unstable in both
  directions on an artefact with no `@Description` at all.**

**What both cost.** eval-8 is flat on the artefact in `iteration-81` and reads as one win and one loss
in the benchmark. **Do not cite either slot in an attribution.** The conditional hole is the fixable
one: give the assertion an explicit vacuous-case verdict (*"PASSES with no further checks when the
class contains no `@Description` annotation"*) so the two readings collapse to one. ~~Not applied —
it re-fingerprints eval-8, and no comparison currently depends on that slot.~~

> **Correction 2026-08-09: it was already applied, on 2026-08-01 in `dd8d2cd`, a week before this
> note was written.** All five hosts open with *"PASSES when no `@Description` is present anywhere in
> the class — omitting it is always acceptable, and this assertion NEVER penalises its absence."* So
> `iteration-81`'s PASS was **correct by design, not a vacuous hole**, and the diagnosis above is
> wrong. The lesson is the cheap one: read the assertion text before filing a defect against it.
> What is genuinely wrong with the slot is different, and measured below.

## eval-15 re-baselined — two mis-scoped slots repaired 2026-08-09

From the perfect-answer pilot (`products/claude-plugin/plans/slice-7-perfect-answer-pilot.md`,
findings A-F1 and A-F4). eval-15 goes **34 → 33 slots**, and no comparison may span the change.

**`three-schemes-distinguished` retired, not retargeted.** It required "no discount for a period
ticket" as an outcome of the purchase being made, and the prompt scopes the feature to *"a new single
ticket purchase"* — so the row it asks for tests a call the feature cannot receive. It failed 0/10 in
the 34-assertion era and 0/6 before that: **0 of 16, never once passed.** Two narrations (iterations
60 and 80) reason the constraint out correctly and are charged a slot for it.

It was worse than a permanent fail: it **contradicted a sibling**.
`period-ticket-excluded-from-count` fails a solution that shows period tickets *only* where the
purchase's discount is decided, which is exactly the shape `three-schemes-distinguished` demanded.
No solution could satisfy both.

Retirement rather than retarget, because after removing the out-of-scope outcome nothing is left that
other slots do not already own: child flat 20% → `2.2-children-flat-discount`, the adult/senior
ladder → `2.3-depth-tier-boundaries` and `2.19-depth-all-tiers`, the period ticket →
`period-ticket-excluded-from-count`, the single derivation → `scheme-derived-once` and `2.1`.
Retargeting would have added a fourth wording of properties three slots already carry, on an eval
where 12 of 34 assertions already pass 10/10.

**`2.4-depth-rolling-window-boundary` rewritten to score a *stated* boundary, not a chosen side.**
Old text: *"a ticket at exactly 30 days is included, at 31 days is excluded."* The prompt decides
neither — it says only "in the last 30 days" — and the eval author's own published worked example of
this domain puts the boundary the other way. Iteration-58 documented the exclusive reading on its
published surface and was failed for it, with the grader citing `expected_output.md` as "spec". The
new text requires the straddling pair plus a stated side, and passes either convention.
`expected_output.md` changed in the same commit — the grader reads it, so relaxing the assertion
alone would have left it primed for the inclusive answer.

**Two other over-specifications in `expected_output.md` fixed while in there**, both instances of the
same fault: the ground truth naming a mechanism where it means a property. The `@TypeConverter`
sentence now says relative time at sub-day granularity is the property and the converter is one way
to get it (`Duration` is another, and needs no converter); § The dimensions no longer lists a
period-ticket purchase as a scheme outcome.

**The general check, which costs nothing and found three instances on this eval alone: where
`expected_output.md` says "intended" or "must", ask whether the prompt actually decides it.**

## Check the residue when you narrow an assertion

**When you narrow an assertion to its decidable core, name what the narrowing cut and check something
else still owns it.** § Bounded tells you to narrow; it does not tell you to look at what falls out,
and nothing else in the loop will — the loop only ever proposes additions justified by a slot that
moved.

Worked instance (eval-18, 2026-08-09). `black-box-columns` was narrowed to *"Judge ONLY the column
headers"*, correctly: it is 3/4 and its one failure is a true catch. What the narrowing cut was the
rest of black-box discipline. Three siblings picked up part of the residue — `observable-io-only`
(column names), `no-reimplemented-internals` (the method body), `description-no-internals`
(`@Description` text) — and between the four of them, **cell values have no owner**. All four runs in
the era duplicate the private inequality in their row values (approving at age 9, rejecting at age
10; ages 760, 800 and 999) and pay nothing; iteration-80 passes `black-box-columns` on the strength
of its column list alone.

The residue was visible in July and read as something else: § Grading effort swept records these same
age-9 rows as *"a repeated skill gap worth teaching, not an assertion to soften"*. Both are true. It
is a skill gap **and** an assertion hole, and only the second explains why five iterations of skill
work never moved it.

The check when narrowing, in three lines:

1. Write the property the assertion had before the narrowing.
2. Write the property it has after.
3. Name the assertion that owns the difference. If you cannot, you have traded variance for a blind
   spot — decide that deliberately, and record it.

## `description-if-present-adds-information` is ballast where nothing writes a description — 2026-08-09

**40 of 42 across five hosts, and both failures are the grader misfiring on an empty antecedent.**
The slot's condition is almost never satisfied:

| Eval | Slot | Runs whose output has a `@Description` |
|---|---|---|
| eval-7 | 9/10 | **0 of 10** |
| eval-8 | 6/7 | **0 of 7** |
| eval-1 | 10/10 | 2 of 10 |
| eval-2 | 7/7 | 6 of 7 |
| eval-9 | 8/8 | 8 of 8 |

**Retired from eval-7 (13 → 12 slots).** Its ground truth makes the slot unfalsifiable by design:
*"`@Description` is optional; there is no out-of-band context … in the prompt, so omitting it is
correct rather than a gap."* An assertion that the eval's own answer key says can only pass is
ballast, and its one failure in ten is the `iteration-70` misfire that quoted a `@DisplayName` as the
description. **eval-7 is newly re-fingerprinted by this; it was one of the four tabletest evals still
matching.**

**Kept on eval-8 and watched.** Same 0-of-7 observation, but eval-8's ground truth does not declare
omission correct, so the slot is live in principle. **Read its verdict in the next regrade before
deciding** — if the coming runs still write no description, retire it there too.

Kept without qualification on evals 2 and 9, where the antecedent is satisfied 6 of 7 and 8 of 8.

**The general shape, which A-F3 also found:** a conditional assertion whose antecedent the solutions
never satisfy is not a lenient assertion, it is an absent one — and its only observable behaviour is
the grader's error rate on the empty case.

## Boundary-row naming is an assertion question, not a skill repair — closed 2026-08-09

**§ J53 queue item 1, discharged here. The skill needs nothing.** The finding was that boundary rows
get named for their outcome rather than their condition. Reading the artefacts says the names are
sound and no better ones exist.

`table-driven-testing` eval-32's ladder, `iteration-7`:

```
"at the ticket-included limit"      "just over the ticket-included limit"
"at the heavy-bag limit"            "just over the heavy-bag limit"
"at the oversize limit"
```

Every id names the boundary by the band it bounds. **The bands have no vocabulary other than their
fees**, so there is no alternative name available — `"at the 23 kg allowance"` only restates the
weight column, which is the defect `scenario-names-describe-conditions` exists to catch. eval-29 is
the same shape. Only eval-14 has an alternative at all (name the input relation, *"corrections exceed
hours worked"*) and it is not clearly better.

**So the pressure runs the wrong way.** `scenario-names-describe-conditions` is the instrument's worst
slot — 3 flips on identical bytes, and recorded above as already majority-wrong and staying wrong.
§ J49 measured it pushing the agent into **deleting** eval-14's `-41/-40/-39` boundary triple. An
assertion that removes boundary rows to satisfy a naming preference is doing damage, and the repair
belongs to the assertion.

**Do not spend a skill loop on this.** If anything is written, it is a narrowing of
`scenario-names-describe-conditions` — and § Check the residue when you narrow an assertion applies
to it in full.
