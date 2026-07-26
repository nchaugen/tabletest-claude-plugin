# Analysis to-do — tabletest, iteration 41

Compared against **iteration 40**, grading claude-sonnet-5/default.

**4 of 4 evals comparable.**

**7 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `concern-not-over-split` — eval-26-convert-from-kotest

Grader said: _appliesOversizeSurcharge and appliesPackageOptionSurcharges both fix EU standard, 3.0kg and share Total Cost? column, splitting the surcharge concern into two tables_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-as-map` — eval-26-convert-from-kotest

Grader said: _Options | Total Cost? column with [handling: hazmat], [fragile: true, insuredValue: 200], [:]_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-type-converter` — eval-26-convert-from-kotest

Grader said: _fun parsePackageOptions(fields: Map<String, String>): PackageOptions { ... fields["fragile"]?.let { ... } }_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _"regardless of its quantity" claim in @Description but only quantity=2 tested for widget-1 in product-specific coupon rows_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `assertion-criteria-declared` — eval-30-order-splitting-tt

Grader said: _assertEquals(fulfillmentGroups, groupShipmentsBy(shipments, Shipment::getFulfillmentType)) is plain map/set equality with no added ordering or normalization_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `held-constants-declared` — eval-30-order-splitting-tt

Grader said: _splitsByFulfillmentType and splitsByDeliveryAddress have no @Description declaring that all items are IN_STOCK, single warehouse, and no companions, yet all rows silently hold these constant_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `native-collection-output` — eval-30-order-splitting-tt

Grader said: _[DELIVERY: {camera, lens}], [addr1: {camera, lens}], [IMMEDIATE: {camera, lens}], [W1: {camera, lens}] are native maps/sets, not encoded strings_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
