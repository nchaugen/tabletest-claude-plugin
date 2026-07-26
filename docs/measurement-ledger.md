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
