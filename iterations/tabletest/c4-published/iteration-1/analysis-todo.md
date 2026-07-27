# Analysis to-do — tabletest variant=c4-published, iteration 1

Compared against **official (iterations 42, 41, 40 merged)**, grading claude-sonnet-5/default.

**4 of 4 evals comparable.**

**15 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.14-depth-zero-rate` — eval-14-weekly-pay

Grader said: _Zero hourly rate | 40 | 5 | 8 | 8 | 0.00 | 0.00_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `1.7-readability-scenario-names` — eval-14-weekly-pay

Grader said: _Negative hours floor total pay describes an outcome, not a work pattern_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `compiles` — eval-14-weekly-pay

Grader said: _Compilation succeeded_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `no-table-reproves-another` — eval-14-weekly-pay

Grader said: _calculatesWeeklyPayEndToEnd uses '45, 8, 8, new BigDecimal("15.00")' -> '1192.50', matching the 'All hour types combined' row in calculatesPayByHourType, re-proving that established rule._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.1-decomposition-concern-separation` — eval-15-reis-discount

Grader said: _singleTicketDiscount table merges CHILD flat discount and ladder tiers in one table; no separate eligibility table exists_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _[29d SINGLE ADULT ZONE_1, 20d SINGLE ADULT ZONE_1, ...]_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _Zone quantification is covered by row: "Zone travelled does not affect the count | [5d SINGLE ADULT ZONE_1, 5d SINGLE ADULT ZONE_2, 5d SINGLE ADULT ZONE_3]"_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `zone-independent-counting` — eval-15-reis-discount

Grader said: _Zone travelled does not affect the count | [5d SINGLE ADULT ZONE_1, 5d SINGLE ADULT ZONE_2, 5d SINGLE ADULT ZONE_3] | 4_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _'Renewal with no claims auto-approves' echoes Decision? value AUTO_APPROVED; 'does not auto-approve' rows likewise state the outcome._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `title-states-system-behaviour` — eval-18-convert-from-code

Grader said: _"Risk score rejection threshold" and "Auto-approval precedence for renewals with no claims" are noun-phrase domain facts, not statements of what the code does._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `consistent-quantity-naming` — eval-28-convert-from-methodsource

Grader said: _Column 'Weight (kg)' in baseRateByZoneAndWeight vs 'Actual Weight (kg)' in effectiveWeightUsesGreaterOfActualAndDimensional name the same input quantity differently._

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-as-map` — eval-28-convert-from-methodsource

Grader said: _"Options | Cost?" columns with cells like "[fragile: true]" and "[:]"_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-type-converter` — eval-28-convert-from-methodsource

Grader said: _@TypeConverter public static PackageOptions parsePackageOptions(Map<String, String> config) {...}_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `rule-statable-from-table` — eval-28-convert-from-methodsource

Grader said: _baseRateByZoneAndWeight: PASS (lookup table); carrierDoesNotAffectShippingCost: PASS (title states invariance); effectiveWeightUsesGreaterOfActualAndDimensional: PASS (formula in @Description '(length x width x height) / 5000'); oversizeSurcharge: PASS (threshold '100 cm' in @Description); fragileSurcharge: PASS ('15% surcharge' in @DisplayName); insurancePremium: PASS ('0.6%... $3.00 minimum' in @DisplayName); hazmatHandlingFee: PASS ('flat $8.00 fee' in @DisplayName, exact string rule in @Description); feeOrderingRelativeToFragileAndInsurance: PASS (order stated in @DisplayName)_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `titles-form-a-family` — eval-28-convert-from-methodsource

Grader said: _Titles like 'Effective weight is the greater of...', 'Oversize dimensions add a flat surcharge', 'Fragile packages incur a 15% surcharge' share subject-first, subject-verb shape across the majority._

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
