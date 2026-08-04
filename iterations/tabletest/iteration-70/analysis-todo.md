# Analysis to-do — tabletest, iteration 70

Compared against **iteration 52**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**6 of 7 evals comparable.** The rest were excluded from them:

- `eval-25-convert-from-spock` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

> **The exclusion message names the wrong run, and no re-run is owed.** `iteration-70`'s eval-25
> generated fine — 25/27, 1365770 tokens, 254861ms, no `error.log`, `errored_evals` empty. **The
> failed side is the baseline**: `iteration-52`'s eval-25 carries `"error": "Timed out after
> 900000ms"` with `assertions_passed: 0`, and a 1274982ms duration — one of the late-firing timeouts
> § I diagnosed. So it-52 holds no usable eval-25 result and never did.
>
> **eval-25's live baseline is `iteration-50` (25/27), same fingerprint `cf05d6e40ec6`.** Compared
> there, from the stored benchmarks and costing nothing:
>
> | | it-50 | it-70 |
> |---|---|---|
> | score | 25/27 | 25/27 |
> | failed | `concern-not-over-split`, `rule-statable-from-table` | `concerns-decomposed`, `rule-statable-from-table` |
>
> **Flat, with `concern-not-over-split` won and `concerns-decomposed` lost.** That is the same trade
> `iteration-69`'s eval-14 made — collapse tables, win the over-split slot, lose the decomposition
> slot — now on a second host and a different task. Two hosts make it a pattern, not variance.
>
> **Runner defect to fix separately:** the comparison excluded the eval because one *side* had an
> error, and the message assumes that side is the current run. It should name which iteration failed.

**6 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `cutoff-date-column-if-literal-dates` — eval-22-event-registration-tt

Grader said: _Column 'Early-Bird Cutoff (Policy) | 2025-03-01' present alongside literal Registration Date values._

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 10's threshold-as-column half fired, and it fired twice in one table.** `computesPriceAndDiscount` declares both policy values as columns — `Early-Bird Cutoff (Policy) | 2025-03-01` and `Min Group Size (Policy) | 5` — held constant down every {{row}} but visible. That is rule 10 verbatim: *a value the rule turns on, such as a threshold or a limit, always can be a column*. it-52 wrote the cutoff nowhere. **Clean win, and the one movement in this portion with no counterweight.** 

## LOST `quantifier-covered-by-rows` — eval-22-event-registration-tt

Grader said: _@Description: 'Registration date and group size do not affect validation, so both are held fixed at an obviously-valid value (2025-06-01, group size 1)' — a regardless-of-domain claim in validatesRegistration but the table shows no varying values or value set for date/group size at all, only a single fixed value never displayed._

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A return of an item this batch already fixed once, on a different host — and rule 10 is the cause on both sides.** `validatesRegistration`'s description reads *"Registration date and group size do not affect validation, so both are held fixed at an obviously-valid value (2025-06-01, group size 1)"*. That is precisely the sentence rule 10's `03aa1fb` addition forbids: *"If the declaration says the value does not matter, declaring it is not enough … a claim no row can contradict is not stated in the table at all. Vary it instead."* Neither column appears in the table at all.

  **The two halves of rule 10 are not firing together.** The held-constants half — declare what the table fixes — fires reliably and did here. The *vary-it-instead* half, three lines below it, did not. **§ G item 1 was CONFIRMED fixed on eval-29 in iteration-58 by this exact wording**; it has now returned on eval-22, so the fix did not generalise past the host it was measured on. **Same split as `iteration-69`'s eval-14 description win** — there the held-constants half fired and won a slot; here the missing half loses one. One rule, two measured effects, opposite signs. 

## LOST `scenario-names-describe-conditions` — eval-22-event-registration-tt

Grader said: _Neither discount applies | ... | Price? 100.00 | Discount? 0.00 — echoes the expectation cell as in the explicit FAIL example_

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Real, not a flip — the baseline settles it.** it-52 named the same {{row}} `On the early-bird cutoff, small group`, which states the input condition; it-70 renamed it `Neither discount applies`, which states the expectation. **The name genuinely got worse**, so the grader is right and this is a content change rather than instrument noise.

  **Check the baseline text before writing this slot off as its own flip rate.** It is the suite's worst flipper (3 flips over three identical re-grades) and it *did* flip in portion D — `iteration-10` lost it on eval-21 for `Neither discount applies` while it-3 passed for the near-identical `Neither applies`, and won it on eval-24 in the same run. Two movements, opposite directions, one run: that is the flip. This one is not.

  **Not attributed to any repair.** Nothing in the batch touches scenario naming (`3a16988` predates it). Record as real unexplained drift on eval-22 and watch it. 

## LOST `fewer-than-nine-rows` — eval-7-permission-check

Grader said: _Found 9 data row(s)_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **One event with `no-duplicate-role-output` below — count it once. This is the portion's real regression, and the batch caused it.** it-52 wrote five {{rows}} carrying value sets: `ADMIN | {READ, WRITE, DELETE} | true`, `USER | {READ, WRITE} | true`, `USER | DELETE | false`, `GUEST | READ | true`, `GUEST | {WRITE, DELETE} | false`. it-70 wrote the full nine-{{row}} cross-product with **no value set anywhere in the output**.

  **The narration names the guidance that fired, in the guidance's own vocabulary:** *"I exhaustively enumerated the matrix rather than collapsing rows, since the enum domain is small and closed (3 roles × 3 actions), and each combination is a distinct rule obligation per the spec."* That is `011c959` — *"'Exactly one' is a floor as well as a ceiling … When you cut a row, say which surviving row discharges its obligation. If none does, keep it."* it-52 predates that commit (2026-08-02 against 08-03 00:45).

  **And `4c348e1`'s guard against exactly this could not apply, because of how its test is phrased.** It says: *"Ask which rule names the branch. If the answer is a neighbouring table's, the difference is a value this rule ignores, and it collapses into a value set."* **eval-7 has one table.** There is no neighbouring rule to name the branch, so the test returns nothing and the floor wins by default. A single-table cross-product falls straight through the guard. 

## LOST `no-duplicate-role-output` — eval-7-permission-check

Grader said: _Admin reads/writes/deletes all ADMIN with Allowed? true; User reads/writes both USER true; Guest writes/deletes both GUEST false_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Same event as `fewer-than-nine-rows` above — the nine-{{row}} cross-product. Do not count it as a second finding.** The grader's evidence states the duplication exactly: `Admin reads/writes/deletes` all `ADMIN` with `Allowed? true`, `User reads/writes` both `USER true`, `Guest writes/deletes` both `GUEST false`. Those are the three groups it-52 had collapsed into value sets. **This assertion is eval-7's mechanical statement of the same obligation the value set discharges**, which is why the two slots move together and why the repair is one repair. 

## LOST `description-if-present-adds-information` — eval-8-money-parse

Grader said: _@Description("Rejects malformed or negative monetary input") merely restates rows, no added context on scale/format questions_

- Output: `eval-8-money-parse/outputs/`
- Narration: `eval-8-money-parse/narration.md`
- Raw transcript: `eval-8-money-parse/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Not a loss — a grader misfire, and the assertion's own text names the mistake.** `MoneyParserTest.java` contains **zero `@Description` annotations** and does not import the class. The grader quoted `@Description("Rejects malformed or negative monetary input")`; that string is the `@DisplayName` on `rejectsInvalidMoneyInput`.

  The assertion reads: *"PASSES when no @Description is present anywhere in the class — omitting it is always acceptable, and this assertion NEVER penalises its absence. Do not fail it for a missing @Description, and do not treat a @DisplayName as a substitute."* The grader did the one thing the text forbids twice over.

  **The delta is an artefact of it-52 having neither annotation.** it-52's output carries no `@DisplayName` and no `@Description`, so it passed vacuously. it-70 added descriptive `@DisplayName`s — an improvement, and what `1.10`-style slots reward — and was penalised for it. **No skill repair is indicated. This belongs in `docs/assertion-triage.md` as a grader defect**: the grader is matching on the quoted string of any annotation carrying prose, not on the annotation type. 
