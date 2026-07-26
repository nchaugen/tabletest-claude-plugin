# Analysis to-do — tabletest, iteration 41

Compared against **iteration 40**, grading claude-sonnet-5/default.

**4 of 4 evals comparable.**

**11 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `concern-not-over-split` — eval-26-convert-from-kotest

Grader said: _appliesOversizeSurcharge and appliesPackageOptionSurcharges both fix EU standard/3.0kg and each vary one surcharge_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `minimal-rows-per-concern` — eval-26-convert-from-kotest

Grader said: _"Just over size limit | [101, 5, 5] | 17.50" and "Large oversize package | [120, 5, 5] | 17.50" both re-cover the same oversize-triggered rule with no new obligation_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-as-map` — eval-26-convert-from-kotest

Grader said: _Options column: '[handling: hazmat]', 'No options | [:] | 7.50'_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `options-type-converter` — eval-26-convert-from-kotest

Grader said: _@TypeConverter fun parsePackageOptions(fields: Map<String, String>): PackageOptions_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `consistent-quantity-naming` — eval-29-shopping-cart-tt

Grader said: _Cart Items used consistently in checksOutCart and calculatesCartTotal; Cart Before/After consistently in add/remove tables_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _"regardless of its quantity" claim in calculatesCartTotal description, but all product-specific coupon rows use quantity 2 only; also 'Cart items are irrelevant... so every row uses an empty cart' with only empty cart tested_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-29-shopping-cart-tt

Grader said: _'Zero quantity rejected', 'Expired code does not replace active coupon', 'Discount exceeds subtotal, floored at zero' -- name the rule/situation, matching the reference's own naming style_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `type-converters-for-complex-objects` — eval-29-shopping-cart-tt

Grader said: _@TypeConverter public static Cart parseCart(...) / ProductCatalogue parseCatalogue(...) / InventoryService parseInventory(...) / Coupon parseCoupon(...)_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `assertion-criteria-declared` — eval-30-order-splitting-tt

Grader said: _assertEquals(fulfillmentGroups, groupShipmentsBy(...)) is plain equality on native Map<K,Set<String>> values, no undeclared ordering/tolerance/subset logic_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `held-constants-declared` — eval-30-order-splitting-tt

Grader said: _splitsByFulfillmentType and splitsByDeliveryAddress have no @Description declaring held-fixed warehouse/stock status/companions constants_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `native-collection-output` — eval-30-order-splitting-tt

Grader said: _Shipments By Warehouse? = [W1: {camera, lens}] and Availability Groups? = [IMMEDIATE: {camera, lens}] use native maps/sets, no string-encoded outputs_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
