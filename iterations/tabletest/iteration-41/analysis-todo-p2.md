# Analysis to-do — tabletest, iteration 41

Compared against **iteration 40**, grading claude-sonnet-5/default.

**4 of 4 evals comparable.**

**8 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `concern-not-over-split` — eval-26-convert-from-kotest

Grader said: _appliesOversizeSurcharge uses same fixture (EU standard, 3.0kg) as appliesPackageOptionSurcharges, splitting one surcharge concern into its own table_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-as-map` — eval-26-convert-from-kotest

Grader said: _"Options | [:] | 7.50" ... "[fragile: true, insuredValue: 200] | 11.625"_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-type-converter` — eval-26-convert-from-kotest

Grader said: _@TypeConverter\nfun parsePackageOptions(fields: Map<String, String>): PackageOptions {_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _'Cart items are irrelevant to coupon validation, so every row uses an empty cart' but cart items never vary across rows_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `type-converters-for-complex-objects` — eval-29-shopping-cart-tt

Grader said: _@TypeConverter public static Cart parseCart(...), parseCatalogue(...), parseInventory(...), parseCoupon(...) convert raw table values; method bodies only call CartService and assertEquals._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `assertion-criteria-declared` — eval-30-order-splitting-tt

Grader said: _groupShipmentsBy collects into Set<String>, matching the table's curly-brace set notation, e.g. '{camera, lens}'_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `native-collection-output` — eval-30-order-splitting-tt

Grader said: _Shipments? columns use native maps/sets like [DELIVERY: {camera, lens}], [W1: {camera, lens}]_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scenario-names-describe-conditions` — eval-30-order-splitting-tt

Grader said: _"No overlap across three warehouses forces three shipments" echoes the expectation's 3-entry map [W1: {camera}, W2: {lens}, W3: {tripod}]_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
