# Measurement ledger

One row per measurement, ever. **Rows are never deleted** — this is the exception to the repo's
distil-then-delete habit, and it is what makes trimming iteration directories safe. When a benchmark
is swept, its row is the only thing left that says the measurement happened.

## What a row means, and when two of them can be compared

**`Instrument`** is a hash over the eval ids and their fingerprints. Two rows sharing an instrument
id ran against the identical eval set with identical definitions. Two rows sharing **both
`Instrument` and `Regime`** are directly comparable; anything else needs the per-eval fingerprints
checked, and across a change of grading regime is not a comparison at all — see
`docs/grader-tuning.md`.

Per-eval `grading_effort` lives in `eval.json` and therefore feeds the fingerprint, so a mixed-effort
suite is captured by `Instrument`. `Regime` records the run-level flags only — a row reading
`sonnet/default` may still have graded most evals at `medium`.

A partial run hashes only the evals it ran, so a 4-eval loop never shares an id with the full suite
even when its definitions match. That is a limitation of the convenience column, not of the run:
comparability is governed per eval by the fingerprint, and `--compare-official` merges accordingly.

**Do not read the `Score` column down the page as a trend.** The instrument changes — every suite
edit re-fingerprints the evals it touches — so the series is a chain of locally valid deltas, not a
line. Where the instrument id changes, the line breaks, and this table shows the break rather than
hiding it.

**`Gen $` and `Dur` are `—` on regrade rows.** A regrade re-uses stored outputs and spends nothing on
generation; the figures inside its benchmark are inherited from the run that produced them, and only
`Grade $` was actually spent. A dash in `Grade $` means unknown rather than free — either the run
predates grading-cost recording, or its model had no price entry at the time
(`summary.grading.priced: false`).

**`Note`** is the only hand-written column. Fill it in when the analysis gate closes — what the
measurement established, or why it is not to be trusted. The runner cannot know this; everything
else in the row it can.

## Maintenance

The runner appends a row at the end of each run. You annotate. Measurements made by hand — a
benchmark diffed outside the runner — get a hand-appended row with `by hand` in the note.

Every row below was backfilled on 2026-07-26 from the benchmarks then on disk, ahead of the
iteration-40 sweep. Backfilled rows carry no baseline or moved-verdict columns: what each run was
compared against at the time is not recoverable from the artefacts. Rows appended by the runner from
here on do carry them.

| Date | Run | Kind | Evals | Skill | Regime | Instrument | Score | Gen $ | Grade $ | Dur | Note |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 2026-07-07 | `iteration-39` | run | 17 | — | claude-haiku-4-5/default | `2a3d82fe` | 279/308 | $15.01 | — | 73m | Superseded on every axis — haiku regime, pre-tranche definitions, pre-1.6 skill. Nothing derivable from its outputs. Swept 2026-07-26; this row is all that remains. |
| 2026-07-21 | `next/iteration-1` | run | 1 | — | claude-haiku-4-5/default | `bfee8ac1` | 20/21 | $0.57 | — | 2m | |
| 2026-07-21 | `next/iteration-2` | run | 5 | — | claude-haiku-4-5/default | `4256ed0a` | 93/108 | $4.84 | — | 28m | |
| 2026-07-22 | `next/iteration-3` | run | 4 | — | claude-haiku-4-5/default | `758955ed` | 83/89 | $2.62 | — | 14m | |
| 2026-07-24 | `next/iteration-4` | run | 5 | `932f63ab3e` | claude-haiku-4-5/default | `bd22932c` | 102/123 | $3.72 | — | 18m | |
| 2026-07-24 | `next/iteration-5` | run | 5 | `c24b795902` | claude-haiku-4-5/default | `bd22932c` | 83/123 | $2.85 | — | 29m | |
| 2026-07-24 | `next/iteration-6` | run | 5 | `d5ecfa9c6e` | claude-haiku-4-5/default | `bd22932c` | 100/123 | $4.82 | — | 23m | |
| 2026-07-25 | `iteration-40` | run | 17 | — | claude-sonnet-5/default | `42388839` | 324/365 | $14.90 | — | 73m | Was the plain-named baseline until 2026-07-26. **Stale** — its definitions matched no current eval, and it voided a cluster-1 comparison before this was noticed. Swept 2026-07-26; `[t5]` took the plain name. |
| 2026-07-25 | `iteration-40 [h1]` | regrade | 17 | — | claude-haiku-4-5/default | `804b7490` | 294/365 | — | — | — | Haiku retested on the rebuilt instrument — 294, 24 below sonnet. Settled the model question. |
| 2026-07-25 | `iteration-40 [h2]` | regrade | 5 | — | claude-sonnet-5/default | `3236a746` | 110/128 | — | $1.03 | — | |
| 2026-07-25 | `iteration-40 [h2b]` | regrade | 2 | — | claude-sonnet-5/default | `59598f03` | 46/55 | — | $0.63 | — | |
| 2026-07-25 | `iteration-40 [h3]` | regrade | 5 | — | claude-sonnet-5/default | `3236a746` | 110/128 | — | $0.96 | — | |
| 2026-07-25 | `iteration-40 [h3b]` | regrade | 2 | — | claude-sonnet-5/default | `59598f03` | 46/55 | — | $0.55 | — | |
| 2026-07-25 | `iteration-40 [haiku]` | regrade | 17 | — | claude-haiku-4-5/default | `42388839` | 315/365 | — | — | — | Early model comparison on the `iteration-40` instrument: 315 vs 324 sonnet. |
| 2026-07-25 | `iteration-40 [m1]` | regrade | 17 | — | claude-sonnet-5/medium | `804b7490` | 322/365 | — | $1.65 | — | Medium effort across the suite, on the `[t3]`/`[t4]` instrument: 322 vs 318/314 at default. |
| 2026-07-25 | `iteration-40 [m2]` | regrade | 5 | — | claude-sonnet-5/medium | `3236a746` | 112/128 | — | $0.67 | — | |
| 2026-07-25 | `iteration-40 [m2b]` | regrade | 2 | — | claude-sonnet-5/medium | `59598f03` | 48/55 | — | $0.32 | — | |
| 2026-07-25 | `iteration-40 [m3]` | regrade | 5 | — | claude-sonnet-5/medium | `3236a746` | 109/128 | — | $0.67 | — | |
| 2026-07-25 | `iteration-40 [m3b]` | regrade | 2 | — | claude-sonnet-5/medium | `59598f03` | 49/55 | — | $0.29 | — | |
| 2026-07-25 | `iteration-40 [sonnet]` | regrade | 6 | — | claude-sonnet-5/default | `5d981bfb` | 133/155 | — | — | — | |
| 2026-07-25 | `iteration-40 [t2]` | regrade | 17 | — | claude-sonnet-5/default | `b7b80fce` | 323/365 | — | — | — | |
| 2026-07-25 | `iteration-40 [t3]` | regrade | 17 | — | claude-sonnet-5/default | `804b7490` | 318/365 | — | — | — | Same instrument and regime as `[t4]` — an unplanned second variance pair, 318 vs 314, corroborating the v-probe spread. |
| 2026-07-25 | `iteration-40 [t4]` | regrade | 17 | — | claude-sonnet-5/default | `804b7490` | 314/365 | — | — | — | See `[t3]`. Also the sonnet arm of the n=3 effort probe. |
| 2026-07-25 | `iteration-40 [t5]` | regrade | 17 | — | claude-sonnet-5/default | `8425750f` | 318/365 | — | $1.83 | — | **Live baseline** — promoted to the plain `benchmark.json` on 2026-07-26 (benchmark, review, to-do and all 17 gradings together). Matches the current eval definitions on all 17 evals (`check-baseline.js`). Mixed per-eval effort — captured by `Instrument`, not `Regime`. |
| 2026-07-25 | `iteration-40 [v1]` | regrade | 17 | — | claude-sonnet-5/default | `42388839` | 322/365 | — | — | — | Variance probe 1 of 3 — same instrument as `iteration-40`. |
| 2026-07-25 | `iteration-40 [v2]` | regrade | 17 | — | claude-sonnet-5/default | `42388839` | 320/365 | — | — | — | Variance probe 2 of 3. |
| 2026-07-25 | `iteration-40 [v3]` | regrade | 17 | — | claude-sonnet-5/default | `42388839` | 327/365 | — | — | — | Variance probe 3 of 3. Across v1/v2/v3: **18 of 365 slots flip, 33 fail in all three** — a ±3–4 slot spread. Flip list distilled to `assertion-triage.md` before the probe was swept 2026-07-26. |
| 2026-07-26 | `next/iteration-7` | run | 4 | `85686277bf` | claude-sonnet-5/default | `9eb3645a` | 88/96 | $4.91 | $0.57 | 26m | Cluster 1 loop; promoted as `e0d88f5`. 7 verdicts moved vs `[t5]` — 4 real wins, 1 real regression, 1 noise, 1 unresolved. Compared by hand at the time: `--compare-official` returned a void comparison against `iteration-40`. Copied into the official tree as `iteration-41` on 2026-07-26 and re-reported correctly there — same measurement, not a new one, so it keeps this single row. |
| 2026-07-26 | `iteration-40 [p1]` | regrade | 17 | — | claude-sonnet-5/default | `8425750f` | 323/365 | — | $1.85 | — | Variance probe on the current instrument, pass 2 of 3 (`benchmark.json` is pass 1). Accuracy 61/68. |
| 2026-07-26 | `iteration-40 [p2]` | regrade | 17 | — | claude-sonnet-5/default | `8425750f` | 322/365 | — | $1.95 | — | Pass 3 of 3. Across the three: **17 of 365 slots flip**, score spread 318/323/322 — the plain baseline is the LOW draw (mean 321). Majority-of-3 accuracy 61/68, no better than one pass. Per-slot list and diagnosis in `assertion-triage.md`. |
| 2026-07-26 | `iteration-41 [p1]` | regrade | 4 | `85686277bf` | claude-sonnet-5/default | `9eb3645a` | 90/96 | — | $0.70 | — | vs iteration 40: 4/4 comparable, 11 moved. Variance probe on the post-promotion outputs, pass 2 of 3. |
| 2026-07-26 | `iteration-41 [p2]` | regrade | 4 | `85686277bf` | claude-sonnet-5/default | `9eb3645a` | 89/96 | — | $0.67 | — | vs iteration 40: 4/4 comparable, 8 moved. Pass 3 of 3. **6 of 96 flip.** Settles two open slots: `concern-not-over-split`/26 is a stable failure (real cluster-1 regression), `held-constants-declared`/30 flips (noise). Answer key does NOT apply to these outputs. |
| 2026-07-26 | `iteration-40 [s1]` | regrade | 17 | — | claude-sonnet-5/default | `bab90af8` | 331/378 | — | $2.02 | — | |
| 2026-07-26 | `iteration-40 [s2]` | regrade | 17 | — | claude-sonnet-5/default | `bab90af8` | 334/378 | — | $1.91 | — | |
| 2026-07-26 | `iteration-40 [s3]` | regrade | 17 | — | claude-sonnet-5/default | `bab90af8` | 328/378 | — | $2.15 | — | |
| 2026-07-26 | `iteration-41 [s1]` | regrade | 4 | `85686277bf` | claude-sonnet-5/default | `f096477b` | 90/99 | — | $0.67 | — | vs iteration 40: 1/4 comparable, 0 moved. |
| 2026-07-26 | `iteration-40 [c1]` | regrade | 11 | — | claude-sonnet-5/default | `18b57bb6` | 248/290 | — | $1.83 | — | Re-baseline after correcting the rule-statable clause and splitting minimal-rows. rule-statable 4/5 (was 1/5-2/5 with the broken clause, 3/5-4/5 before it); evals 15 and 18 correct again. 58/61 overall. **Promoted to the plain benchmark.json.** |
| 2026-07-26 | `iteration-40 [c1]` | regrade | 17 | — | claude-sonnet-5/default | `b6b46cc0` | 333/378 | — | $1.98 | — | Re-baseline after correcting the rule-statable clause and splitting minimal-rows. rule-statable 4/5 (was 1/5-2/5 with the broken clause, 3/5-4/5 before it); evals 15 and 18 correct again. 58/61 overall. **Promoted to the plain benchmark.json.** |
| 2026-07-26 | `iteration-41 [c1]` | regrade | 3 | `85686277bf` | claude-sonnet-5/default | `0c92a172` | 71/82 | — | $0.75 | — | vs iteration 40: 0/3 comparable, 0 moved. |
| 2026-07-26 | `iteration-41 [c1]` | rebuild | 4 | `85686277bf` | claude-sonnet-5/default | `0b0dff73` | 88/99 | — | — | — | vs iteration 40: 1/4 comparable, 0 moved. Rebuild of the row above plus eval-20 (carried from s1). No API calls; the $0.75 above is the whole spend. |
| 2026-07-26 | `next/iteration-8` | regrade | 6 | `faea8d4a26` (by hand) | claude-sonnet-5/default | `62765833` | 134/156 | ~$5 (see note) | $0.91 | 26m gen | Cluster 2 loop. **Read the note before using this row.** Generation ran with the Bash sandbox on and was degraded twice over: Gradle could not start (`libnative-platform.dylib`), so all 6 lost `compiles` against a baseline where all 6 passed — discard those 6 of the 28 moved verdicts, they are instrument. Worse, no Bash meant no directory listing, so on evals **15 and 18** the agent could not discover the provided `src/main` and invented parallel APIs (eval-15 narration: "Read/Write/Edit work fine even though Bash is down"; eval-18: "no `InsuranceEvaluator` existed yet"). Those two evals answer a different task than the baseline did — **their 6 wins and 5 losses are all unusable.** Sound evals are 14, 22, 23, 26: **6 of 6 targeted slots won**, plus `consistent-quantity-naming`/26 and `scenario-names`/23 unplanned, against 1 real regression (`1.14-depth-zero-rate`/14) and 1 unsettled flip-prone slot (`rule-statable-from-table`/26). Corrected net excluding `compiles` is +6. `skill_digest` stamped `unknown` by `--grade-only` because the failed first grading wrote no benchmark; the real variant digest is `faea8d4a26353180` at repo commit `eebbe81`. Gen cost not recorded for the same reason (first run wrote no benchmark); ~$5 from the per-eval baseline figures. Causes for all 28 filled in `analysis-todo.md`. |
| 2026-07-26 | `next/iteration-9` | run | 2 | `faea8d4a26` | claude-sonnet-5/default | `10159e60` | 46/55 | $2.03 | $0.39 | 10m | vs official (iterations 41, 40 merged): 2/2 comparable, 8 moved. **Sound re-run of the two evals `next/iteration-8` lost to the sandbox** — run unsandboxed, `compiles: true` on both, and both outputs use the real provided APIs (`evaluateApplication(applicantType, age, claimCount)`; `PastPurchase`/`TravelerCategory`/`PurchaseHistoryRepository`). Supersedes iteration-8's eval-15 and eval-18 rows entirely. WON `2.16`/`2.19`/`2.20`/`no-table-reproves` (15) and `no-duplicate-rows` (18); LOST `zone-independent-counting`, `quantifier-covered-by-rows`, `2.21-readability-relative-time` (15). Two things the void run got wrong and this corrects: `2.1-decomposition`/15 and `no-table-reproves`/18 did **not** regress (artefacts of the invented APIs), and `separates-decision-and-premium`/18 did **not** win — it still fails, because all three tables assert `Decision?` and `Premium?` together. Together with iteration-8's sound evals (14, 22, 23, 26): **11 of 12 targeted slots won, +8 slots on the loop.** Causes for all 8 filled in `analysis-todo.md`. |
| 2026-07-26 | `iteration-42` | rebuild | 6 | `faea8d4a26` (by hand) | claude-sonnet-5/default | `62765833` | 141/156 | — | — | — | **Cluster 2's promotion partial** (`AGENTS.md` § Promoting a variant, step 4) for plugin `e779801`. No new measurement — assembled from the two runs that measured the promoted skill, taking the **sound** eval from each: 14, 22, 23, 26 from `next/iteration-8` and 15, 18 from `next/iteration-9`. Iteration-8's own eval-15 and eval-18 were deliberately **not** copied; they are void (the sandbox left those agents unable to find the provided `src/main`, so they invented APIs). The "1/6 comparable, 4 moved" line is meaningless — it compares against iteration-41, which shares only eval-26. With this in place the merged official baseline is **338/378, 40 failing** (was 331/378, 47 failing), resolving 8 evals from iteration-40, 6 from here, 3 from iteration-41; `check-baseline.js` confirms all 17 current. `skill_commit`/`skill_digest` came out `unknown` from the rebuild and were stamped by hand: the digest `faea8d4a26353180` is the promoted skill's and is identical for both source runs (the variant was not edited between them); the commit is mixed because the two runs generated at different HEADs. **Two `compiles` slots were corrected by hand, and one was not — see the caveat below this table.** |

## Hand-corrected `compiles` slots in `iteration-42` — 2026-07-26

`iteration-42` is the only benchmark in this ledger with assertion verdicts edited by hand. What was
changed, why, and how to check it:

`next/iteration-8` ran with the Bash sandbox on, so Gradle could not start and all six evals recorded
`compiles: false` with `tests_pass: null` against a baseline where all six compiled. Left as-is in the
promotion partial, that would put a false failure into the official baseline and make the *next*
cluster show a phantom `compiles` win — the artefact `AGENTS.md` § Timeouts warns to suspect.

The verdicts were not re-graded, because `--grade-only` re-rolls every LLM assertion and several of
this loop's slots are flip-prone; re-rolling them would destroy the promotion evidence to fix a
deterministic slot. Instead each build was **reproduced directly**: the eval's `project/` scaffolding
overlaid with the stored `outputs/`, then `gradle compileTestJava` (or `compileTestKotlin`) and
`gradle test`, run unsandboxed. Only `compiles`/`tests-pass` were touched; no LLM verdict was altered.

| Eval | Verified | Corrected in `iteration-42` |
|---|---|---|
| 22 | compiles ✅, 21 tests fail (stubs throw — baseline also `tests_pass: false`) | `compiles` → pass |
| 23 | compiles ✅, 9 tests fail (stubs throw — baseline also `tests_pass: false`) | `compiles` → pass |
| 26 | compiles ✅, tests pass ✅ (baseline also `tests_pass: true`) | `compiles` → pass; `tests-pass` evidence corrected — it had been scored pass via the `tests_pass: null` "skipped" branch, i.e. right by accident |
| **14** | **not reproducible** | **nothing — `compiles` stays FAIL** |

**eval-14 is the residual, and it is deliberate.** `collectTestFiles` stores only test files, so the
`src/main` stubs its agent wrote are not in `outputs/` and its build cannot be reproduced from stored
artefacts. Writing replacement stubs would measure those stubs, not the agent's output. So one known-
false `compiles` failure sits in the official baseline: **expect a phantom `compiles` win on eval-14 in
the next run that includes it, and do not read it as an improvement.** Cluster 3's loop (7, 9, 22, 23,
29) does not include eval-14; the batch-closing full run does.

`next/iteration-8` itself was left exactly as measured — it records what that run actually produced.
The correction lives only in the forward-looking baseline copy.
| 2026-07-27 | `c4-published/iteration-1` | run | 4 | `f20c533445` | claude-sonnet-5/default | `78de5797` | 87/103 | $3.97 | $0.73 | 21m | vs official (iterations 42, 41, 40 merged): 4/4 comparable, 15 moved. **Gate closed 2026-07-27; not promoted.** Cluster 4 (descriptions & titles). +3 gross is **−1 after discounts**: eval-14's `compiles` is the known phantom, and three of eval-28's four wins are **cluster 1's**, arriving late because eval 28's baseline is `iteration-40`, predating cluster 1's promotion (its loop was 20/26/29/30). Targeted 4 of 7; the release blocker's target `separates-decision-and-premium` did not move. Two draft defects found: the title illustration taught noun phrases (cost `title-states-system-behaviour` on 18, a slot the variant *introduced* — baseline had no `@DisplayName`), and the combining-table check licensed keeping the table (cost `no-table-reproves-another` on 14). |
| 2026-07-27 | `c3-names/iteration-1` | run | 5 | `4f4cf14e9e` | claude-sonnet-5/default | `f2ae3fa5` | 101/110 | $3.50 | $0.76 | 17m | vs official (iterations 42, 41, 40 merged): 5/5 comparable, 9 moved. **Gate closed 2026-07-27; not promoted.** **Promoted 2026-07-27 as `iteration-43`.** Digest `4f4cf14e9e` is the variant's and does **not** match the promoted skill: c3 was promoted by applying its diff onto a `skills/` that had already taken the F1/F5/F6/F7 corrections (`a6b43e2`), so the promoted skill is c3 + those fixes while these results come from c3 alone. Expected, not an error — see `AGENTS.md` § Promoting a variant. Cluster 3 (scenario names). +3, targeted 3 of 5 — evals 7 and 9 to 13/13, 22 won. Misses on 29 and 30 share one shape, the consequence verb phrase ("force two shipments", "floors total at zero"). Whole −1 sits on eval 30, on two non-naming slots (`all-outputs-same-table`, `assertion-criteria-declared`). |
| 2026-07-27 | `c4-published/iteration-2` | run | 2 | `0171b21f20` | claude-sonnet-5/default | `4b0a753a` | 41/49 | $1.57 | $0.34 | 8m | vs official (iterations 43, 42, 41, 40 merged): 2/2 comparable, 4 moved. **Gate closed 2026-07-27.** Targeted re-run of the two evals cluster 4's draft defects cost, after fixing them. Both repaired: `title-states-system-behaviour` (18) back to passing and `no-table-reproves-another` (14) fixed, plus `separates-decision-and-premium` (18) won for the first time — though the same wording was present in iteration-1 and did not fire, so that is a second draw, not proof of cause. Eval 14 stayed level at 18/22 with changed composition: `1.14-depth-zero-rate` and `1.15-format-clean-method` lost. **A first attribution of those two to the combining-table salvage step was wrong** and is corrected here: the zero-rate row was never in the combining table (iteration-1 had it in the pay table), so deleting that table could not displace it. The cause is boundary guidance — the agent built a `0 accepted` / `-0.01 rejected` straddle in the rate-validation table, and `-0.01` rather than `-10.00` is the tell. That mixed throw/no-throw shape had no worked example in the skill, so it wrote `if (throws_ == null)` and lost `1.15`; and representing zero rate there felt like discharging it, so no zero-rate row reached the pay table and `1.14` went with it. Both gaps closed in `skills/` the same day. |
| 2026-07-27 | `iteration-44` | rebuild | 4 | `f20c533445` | claude-sonnet-5/default | `78de5797` | 89/103 | — | — | — | vs iteration 43: 0/4 comparable, 0 moved — **the void comparison is expected and not a finding**: iteration-43 ran evals 7/9/22/29/30 and this partial holds 14/15/18/28, so there was nothing to compare. **Cluster 4's promotion partial** (`d2f2b52`), assembled per eval from the best available run: 14 and 18 from `c4-published/iteration-2` (post-defect-fix), 15 and 28 from `iteration-1`. Note 15 and 28 therefore predate the two defect fixes, and 28's `titles-form-a-family` win was measured under the *pre-fix* noun-phrase illustration — the batch-closing run re-measures both. Digest is the variant's and does not match the promoted skill, which is c4 applied over the F-fixes and cluster 3; see `AGENTS.md` § Promoting a variant. |
