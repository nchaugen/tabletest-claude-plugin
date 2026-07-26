# Analysis to-do — tabletest variant=next, iteration 8

Compared against **official (iterations 41, 40 merged)**, grading claude-sonnet-5/default.

**6 of 6 evals comparable.**

**28 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `1.14-depth-zero-rate` — eval-14-weekly-pay

Grader said: _Zero rate | 0.00 | tested only via calculateWeeklyPay(0,0,0,0,hourlyRate) - no nonzero hours shown at rate 0_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): REAL REGRESSION, caused by the combining-table guidance. The baseline's end-to-end table carried `Zero hourly rate is allowed | 40 | 5 | 0 | 0.00` — a zero rate against NONZERO hours. The variant deleted that table (which is what `no-table-reproves-another` wanted, and it won) and the obligation went with it: the surviving zero-rate row is `Zero rate | 0.00 |` inside `rejectsNegativeHourlyRate`, where hours are 0 too, so nothing shows that a zero rate zeroes real pay. **Lesson for the guidance: deleting a combining table can delete obligations that lived only in its rows.** Add — before deleting it, check which obligations only its rows discharge and move those rows into the table that owns the rule.

## LOST `compiles` — eval-14-weekly-pay

Grader said: _Compilation failed: Picked up JAVA_TOOL_OPTIONS: -Djava.net.preferIPv4Stack=true FAILURE: Build failed with an exception. * What went wrong: Gradle could not start your build. > Could not initialize native services._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): INSTRUMENT, not the skill — discard all six. Gradle cannot start under the Bash sandbox: `Failed to load native library 'libnative-platform.dylib' for Mac OS X aarch64`, identical in all six `build-result.json` files, with `tests_pass: null`. All six passed `compiles` in the baseline. Re-run generation unsandboxed. Side effect worth noting: the generating agent could not build either — eval-22's narration says "I've reviewed it as thoroughly as I can without a working shell" — so every output in this run was written blind. That weakens the run as evidence for anything build-related; the design assertions are unaffected because they are graded off the code text.

## WON `no-duplicate-rows-within-a-table` — eval-14-weekly-pay

Grader said: _classifiesWeekdayHours: PASS (below/at/just-past threshold, no repeats). calculatesWeeklyPay: PASS (regular/overtime/sunday/holiday/combined/floor rows each distinct). rejectsNegativeHourlyRate: PASS (positive/zero/negative rate, each distinct)._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real. `rejectsNegativeHourlyRate` dropped the baseline's `Large negative hourly rate | -50.00` row and now carries one negative row (`-0.01`) beside positive and zero rows that straddle the boundary. That is the guidance's "further past the same boundary" shape removed exactly as written.

## WON `no-table-reproves-another` — eval-14-weekly-pay

Grader said: _calculatesWeeklyPay takes regular/overtime hours as direct inputs (per its @Description) rather than re-deriving classification, so it doesn't re-prove classifiesWeekdayHours._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real. The baseline's fifth table, whose own `@Description` read "End-to-end scenarios combining the rules from the tables above", is gone. The guidance names that sentence as the symptom ("If the `@Description` you would write is ... the table has no rule of its own. Delete it.") and the agent deleted it. See the `1.14-depth-zero-rate` loss for the cost.

## WON `separates-classification-and-calculation` — eval-14-weekly-pay

Grader said: _classifyWeekdayHours(weekdayHours) in one @TableTest, calculateWeeklyPay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate) in another._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, and the clearest instance of the new guidance firing. The variant added `classifiesWeekdayHours` with `Regular Hours?` and `Overtime Hours?` as its expectation columns — precisely the "give the classification its own table, whose expectation columns *are* the classification" instruction — and `calculatesWeeklyPay` now takes those as input columns. The baseline went straight from `41 | 20.00` to `830.00` with the band split stated nowhere.

## LOST `2.1-decomposition-concern-separation` — eval-15-reis-discount

Grader said: _ReisDiscountLadderTest combines Passenger Type (CHILD vs ADULT/SENIOR) and Trip Number columns in one table, not separating discount ladder from eligibility_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): REAL REGRESSION, and a genuine tension inside eval-15's own instruments. `2.16` demands the tier mapping be expressed once; `2.1` demands the ladder be separated from category eligibility. The variant resolved it by merging category into the ladder table as `{ADULT, SENIOR}` value sets — winning `2.16`, `2.19` and `2.20`, losing `2.1`. The collapse guidance (`Do not over-split either`) supplied the pressure to merge. **This is why `2.16`/`2.19`/`2.20` were pre-registered as the weakest evidence in the run: they are eval-15-only instruments and they trade against each other.** Not obviously fixable by wording — decide whether `2.1` or `2.16` states the intended design before touching either.

## WON `2.16-no-duplicate-tier-mapping` — eval-15-reis-discount

Grader said: _Tier mapping appears only in ReisDiscountLadderTest; ReisTravelFrequencyTest and calculator test don't re-enumerate it_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED. The ladder is now stated once in `ReisDiscountLadderTest`; the baseline re-enumerated it in the category table and again in the end-to-end table. Won together with `2.19` and `2.20` — but see the `2.1` loss: the merge that achieved this is what lost `2.1`, and all four are eval-15-only instruments.

## WON `2.19-depth-all-tiers` — eval-15-reis-discount

Grader said: _rows cover 0,5,10,15,20,25,30,35,40 e.g. 'Fortieth trip reaches the cap | {ADULT, SENIOR} | {40, 45, 1000} | 40'_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, and the strongest single move in the run. The baseline showed 5 of 9 tiers (0/5/10/35/40); the variant enumerates all nine, each as one row. This is the assertion the published skill already addressed at line 752 and still failed — promoting it out of the syntax subsection into a first-class section is what changed.

## WON `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _Fifth trip reaches first tier | {ADULT, SENIOR} | {5, 9} | 5 - single row per tier with value sets_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real. The baseline split every tier into `Exactly at first tier | 5` plus `Within first tier | {6,7,8,9}`; the variant writes one row per tier with both boundaries inside the set (`{5, 9}`, `{10, 14}`, ...). Exactly the "Do not split a tier in two" instruction.

## LOST `compiles` — eval-15-reis-discount

Grader said: _Compilation failed: Picked up JAVA_TOOL_OPTIONS: -Djava.net.preferIPv4Stack=true FAILURE: Build failed with an exception. * What went wrong: Gradle could not start your build. > Could not initialize native services._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): INSTRUMENT, not the skill — discard all six. Gradle cannot start under the Bash sandbox: `Failed to load native library 'libnative-platform.dylib' for Mac OS X aarch64`, identical in all six `build-result.json` files, with `tests_pass: null`. All six passed `compiles` in the baseline. Re-run generation unsandboxed. Side effect worth noting: the generating agent could not build either — eval-22's narration says "I've reviewed it as thoroughly as I can without a working shell" — so every output in this run was written blind. That weakens the run as evidence for anything build-related; the design assertions are unaffected because they are graded off the code text.

## WON `no-table-reproves-another` — eval-15-reis-discount

Grader said: _ReisDiscountCalculatorTest uses plain @Test methods ('void combinesPurchaseHistoryWithTheDiscountLadder()'), not @TableTest, so no @TableTest table re-proves another table's rules_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real. `calculatesDiscountEndToEnd` is gone, replaced by two wiring `@Test` methods. The rule moved as intended; the `@Test` choice is what cost `quantifier-covered-by-rows` and `title-states-system-behaviour`.

## LOST `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _childrenGetTheFlatDiscountRegardlessOfHistory only records one history entry ('history.record(TicketType.SINGLE, PURCHASE_TIME.minus(1, ChronoUnit.DAYS));') despite the 'regardless of history' claim._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): COLLATERAL of replacing the end-to-end table with plain `@Test` methods. `childrenGetTheFlatDiscountRegardlessOfHistory` claims "regardless of history" while recording a single history entry — a `@Test` cannot quantify over anything, so the claim in its name is unbacked. Traceable to the guidance's "Wiring is not a rule ... that is one row, not a second pass over the ladder": the agent read "one row" as "one `@Test`". **Lesson: say the wiring row belongs in a table, so it can still carry a value set.**

## LOST `title-states-system-behaviour` — eval-15-reis-discount

Grader said: _childrenGetTheFlatDiscountRegardlessOfHistory' restates the domain rule (children always flat 20%) rather than describing tested code behavior._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): COLLATERAL of the same `@Test` restructure — the new method name `childrenGetTheFlatDiscountRegardlessOfHistory` states the domain rule rather than what the code does. Same root cause as `quantifier-covered-by-rows` above; fixing the wiring-row wording should take both.

## LOST `zone-independent-counting` — eval-15-reis-discount

Grader said: _TicketPurchase and FakeTicketPurchaseHistory have no zone field at all; no zone dimension exists in any table, so zone-independence for counting is never exercised._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): COLLATERAL of column economy going too far. The baseline modelled purchases as `[1/SINGLE/ZONE_1, ...]` and had a `Zone does not affect the count` row; the variant's `TicketPurchase` has no zone field at all (`[SINGLE/1]`), so zone-independence is not merely unproven, it is unmodelled. Column reduction is not something this cluster's guidance asks for, so read this as over-application of the general economy frame rather than a specific rule misfiring — but it is a real loss and the same frame produced the eval-26 loss below.

## LOST `compiles` — eval-18-convert-from-code

Grader said: _Compilation failed: Picked up JAVA_TOOL_OPTIONS: -Djava.net.preferIPv4Stack=true FAILURE: Build failed with an exception. * What went wrong: Gradle could not start your build. > Could not initialize native services._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): INSTRUMENT, not the skill — discard all six. Gradle cannot start under the Bash sandbox: `Failed to load native library 'libnative-platform.dylib' for Mac OS X aarch64`, identical in all six `build-result.json` files, with `tests_pass: null`. All six passed `compiles` in the baseline. Re-run generation unsandboxed. Side effect worth noting: the generating agent could not build either — eval-22's narration says "I've reviewed it as thoroughly as I can without a working shell" — so every output in this run was written blind. That weakens the run as evidence for anything build-related; the design assertions are unaffected because they are graded off the code text.

## WON `no-duplicate-rows-within-a-table` — eval-18-convert-from-code

Grader said: _decidesEligibility: PASS (distinct boundary/condition rows). classifiesRiskTier: PASS (uses {true, false} set for referral row). calculatesPremium: PASS ('Standard tier, full/less/no coverage' rows each show a distinct coverage-to-premium mapping)._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real. The baseline's `Well above threshold | 50 | 10 | REJECTED` (redundant beside `Risk score just above threshold | 10 | 5 | REJECTED`) is gone, and the age table now uses straddling pairs only (17/18 and 75/76, each pair differing in outcome).

## LOST `no-table-reproves-another` — eval-18-convert-from-code

Grader said: _calculatesPremium asserts 'assertEquals(expectedTier, result.riskTier());' re-proving the smoker/condition→tier mapping already established in classifiesRiskTier._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): REAL REGRESSION, caused by the classification/calculation guidance. The variant correctly split `classifiesRiskTier` out (that won `separates-decision-and-premium`), but `calculatesPremium` then kept `Risk Tier?` as an *asserted output* (`assertEquals(expectedTier, result.riskTier())`), which re-proves the mapping `classifiesRiskTier` owns. The guidance says "the pay table takes the classified hours as inputs" and eval-14 followed that exactly (`Regular Hours | Overtime Hours`, no `?`); eval-18 made it an output. **Lesson: state that the downstream table takes the classification as an input column without a `?`, never as a second asserted output.** Note this loss was caught by the same assertion the cluster targeted — the instrument working, not disagreeing with itself.

## LOST `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _'At referral threshold, refers to underwriting' paraphrases Decision? = REFERRED; 'Standard tier, full coverage' echoes Risk Tier? = STANDARD._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): WEAK EVIDENCE, and it moved both ways in this run. Lost on eval-18 (`At referral threshold, refers to underwriting` paraphrases `Decision? = REFERRED`) and WON on eval-23 in the same run. New tables need new scenario names, and eval-18's three restructured tables named several after their outcomes. The assertion is on the flip list (x2). Cluster 3 owns it; do not read either direction as this cluster's effect.

## WON `separates-decision-and-premium` — eval-18-convert-from-code

Grader said: _decidesEligibility only asserts decision/declineReason; calculatesPremium only asserts riskTier/premium — no method mixes decision and premium._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real. The baseline had `Premium?` asserted in both decision tables; the variant's `decidesEligibility` carries no premium column and premium lives only in `calculatesPremium`. This is the "a second expectation column that is a different rule's output" instruction firing.

## LOST `compiles` — eval-22-event-registration-tt

Grader said: _Compilation failed: Picked up JAVA_TOOL_OPTIONS: -Djava.net.preferIPv4Stack=true FAILURE: Build failed with an exception. * What went wrong: Gradle could not start your build. > Could not initialize native services._

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): INSTRUMENT, not the skill — discard all six. Gradle cannot start under the Bash sandbox: `Failed to load native library 'libnative-platform.dylib' for Mac OS X aarch64`, identical in all six `build-result.json` files, with `tests_pass: null`. All six passed `compiles` in the baseline. Re-run generation unsandboxed. Side effect worth noting: the generating agent could not build either — eval-22's narration says "I've reviewed it as thoroughly as I can without a working shell" — so every output in this run was written blind. That weakens the run as evidence for anything build-related; the design assertions are unaffected because they are graded off the code text.

## WON `no-duplicate-rows-within-a-table` — eval-22-event-registration-tt

Grader said: _Distinct malformed-email shapes (missing @, local part, domain, TLD); pricing table uses value sets for tiers_

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, won — but only half the intended mechanism worked, so treat it as a weaker win than the others. What fired: the three missing-name rows collapsed to two (`(null)` and `{'', '   '}`) and the missing-email pair likewise, which is the reworded Null/Empty/Blank note. What did NOT fire: the four malformed-email rows all survived. The narration shows why — it reasons about "distinct malformed-email shapes (missing `@`, local part, domain, TLD)", i.e. it read them as four structurally different reasons, which the restored original sentence licenses and the new bound does not clearly forbid. `expected_output.md` caps them at two. **Fix: make the discriminator the rule's own granularity — all four produce one undifferentiated error, so the rule does not distinguish them and the table should not either.**

## LOST `compiles` — eval-23-loan-approval-tt

Grader said: _Compilation failed: Picked up JAVA_TOOL_OPTIONS: -Djava.net.preferIPv4Stack=true FAILURE: Build failed with an exception. * What went wrong: Gradle could not start your build. > Could not initialize native services._

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): INSTRUMENT, not the skill — discard all six. Gradle cannot start under the Bash sandbox: `Failed to load native library 'libnative-platform.dylib' for Mac OS X aarch64`, identical in all six `build-result.json` files, with `tests_pass: null`. All six passed `compiles` in the baseline. Re-run generation unsandboxed. Side effect worth noting: the generating agent could not build either — eval-22's narration says "I've reviewed it as thoroughly as I can without a working shell" — so every output in this run was written blind. That weakens the run as evidence for anything build-related; the design assertions are unaffected because they are graded off the code text.

## WON `scenario-names-describe-conditions` — eval-23-loan-approval-tt

Grader said: _'Standard applicant, score at threshold (not above)' names the condition, not the REJECTED result._

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): UNPLANNED, and eval-23 was the over-firing sentinel, not a target. Its rows went 11 to 8 while keeping every obligation (both threshold straddles, the senior age boundary, unknown-income PENDING_REVIEW, and score-rejection overriding unknown income), with the two `below threshold regardless of income` rows collapsed into a `{true, false}` value set. The sentinel therefore held: economy applied without losing coverage. The scenario-name win is a side effect of rewriting rows and sits on a flip-list assertion — do not bank it.

## LOST `compiles` — eval-26-convert-from-kotest

Grader said: _Compilation failed: Picked up JAVA_TOOL_OPTIONS: -Djava.net.preferIPv4Stack=true FAILURE: Build failed with an exception. * What went wrong: Gradle could not start your build. > Could not initialize native services._

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): INSTRUMENT, not the skill — discard all six. Gradle cannot start under the Bash sandbox: `Failed to load native library 'libnative-platform.dylib' for Mac OS X aarch64`, identical in all six `build-result.json` files, with `tests_pass: null`. All six passed `compiles` in the baseline. Re-run generation unsandboxed. Side effect worth noting: the generating agent could not build either — eval-22's narration says "I've reviewed it as thoroughly as I can without a working shell" — so every output in this run was written blind. That weakens the run as evidence for anything build-related; the design assertions are unaffected because they are graded off the code text.

## WON `concern-not-over-split` — eval-26-convert-from-kotest

Grader said: _appliesSurchargesToBaseCost is one table with 6 rows covering oversize, fragile, insured, fragile+insured, hazmat_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, and this reverses the cluster-1 regression the plan set out to fix. `appliesOversizeSurcharge` is gone as a separate method; `appliesSurchargesToBaseCost` now carries a `Dimensions` column beside the `Options` map column with one `Total Cost?`, so the oversize case is a row rather than a table. That is the "a map column is a column decision, not a table decision" counterweight working as written. `concern-not-over-split` was a stable FAIL across eight prior observations, so this is not noise.

## WON `consistent-quantity-naming` — eval-26-convert-from-kotest

Grader said: _Base rate table and dimensional weight table both use 'Base Cost?'; surcharges and carrier tables both use 'Total Cost?'_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): UNPLANNED (cluster 6 material). The baseline mixed `Rate?` and `Total Cost?` for the same quantity across sibling tables; the variant uses `Base Cost?` and `Total Cost?` consistently. Plausibly a side effect of merging the surcharge tables, which forced one naming decision instead of several. Cluster 6 is two slots and below MDE alone, so bank this only if it survives the batch-closing run.

## WON `no-duplicate-rows-within-a-table` — eval-26-convert-from-kotest

Grader said: _determinesBaseRateByRegionSpeedAndWeight: PASS (8 distinct region/speed/tier rows); selectsEffectiveWeightFromActualOrDimensional: PASS (2 rows, actual vs dimensional); appliesSurchargesToBaseCost: PASS (6 distinct surcharge scenarios); carrierDoesNotAffectCost: PASS (1 value-set row {DHL, UPS, FEDEX})_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real. Two changes: the redundant `Large oversize package | [120, 5, 5]` beside `Just over size limit | [101, 5, 5]` (both 17.50) is gone, and the base-rate table dropped from a full 16-row region x speed x weight cross-product to 8 rows. The cross-product was the assertion's own named FAIL example and the baseline grader missed it, so this table improved more than the baseline verdict suggested. See the `rule-statable-from-table` loss for the possible cost of that cut.

## LOST `rule-statable-from-table` — eval-26-convert-from-kotest

Grader said: _determinesBaseRateByRegionSpeedAndWeight: FAIL (no boundary rows at 1/5/15kg, bracket cutoffs never published in table or @Description); selectsEffectiveWeightFromActualOrDimensional: PASS (description states 'volume / 5000, rounded to 3 decimal places'); appliesSurchargesToBaseCost: PASS (description names each surcharge and order); carrierDoesNotAffectCost: PASS (method name states invariance)_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): NOT SETTLED — do not attribute at n = 1. The base-rate table went 16 rows to 8 and the grader now fails it for publishing no bracket cutoffs at 1/5/15 kg. But the baseline's 16-row version had no boundary rows either (0.5/3.0/10.0/25.0 per region and speed), so by the assertion's own words it should have failed in the baseline too and did not. `rule-statable-from-table` is on the flip list (3-4 flips across identical re-grades) and this is exactly its signature. **Confirm with `--grade-only --grading-suffix` on these stored outputs before treating it as a regression.** If it is real, the mechanism is row economy cutting boundary rows, which the guidance explicitly forbids ("keep the straddling pair"), so the wording would need strengthening rather than reverting.
