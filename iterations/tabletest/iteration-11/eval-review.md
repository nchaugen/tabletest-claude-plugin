# Eval Review — Iteration 11

**Model:** sonnet · **Date:** 2026-03-29 · **Evals:** 4

## Summary

**with_skill:** 14/26 (53.8%) · 140525 tokens · 721.7s · $0.3998

## Delta vs Iteration 10

No changes.

## Resource Comparison vs Iteration 10

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | T/O | — | — | — | T/O | — |
| eval-15-reis-discount | 5/13 | — | 71577 | — | 369.5 | — |
| eval-16-order-splitting | 9/13 | — | 68948 | — | 352.2 | — |
| eval-17-shopping-cart | T/O | — | — | — | T/O | — |

## Per-Eval Results

### ⚠️ Eval eval-15-reis-discount [with_skill]

**5/13** · 71577 tokens · 369500ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > The response provides Table 1 (Reis Discount Tier for adults/seniors), Table 2 (Child Flat Discount), and Table 3 (Final ticket price arithmetic). While discount ladder and traveller eligibility are separated, no dedicated table tests rolling window boundary conditions (e.g., a ticket at exactly 30 days vs 31 days). The response mentions the 30-day window in the text but contains no structured test cases for the boundary.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > While the response discusses the 30-day rolling window ('discount is determined by how many single tickets the passenger has purchased in the last 30 days'), the scenario table shows various ticket counts but does NOT include explicit test cases comparing a ticket at exactly 30 days versus 31 days to verify boundary inclusion/exclusion behavior.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > The response uses human-readable values (Adult, Senior, Child, discount percentages like '5%' instead of 0.05). However, 'TypeConverter or similar mechanism' is not mentioned anywhere in the response, neither in the table descriptions nor in the open questions section.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > The tier boundaries table uses range notation (e.g., '4 – 8', '9 – 13') rather than explicit value set notation like '{4, 5, 6, 7, 8}' or '{4, 9}' as specified in the assertion. Although ranges avoid enumerating per-value rows, the format does not match the required value set representation.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response is a Markdown specification with tables, not Java/JUnit code. No @DisplayName annotations or method names are present.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > The response is a Markdown specification with no @Description annotations on code. While narrative text is provided (e.g., 'The central rule: discount is determined by...'), this is not an @Description annotation as required by the assertion.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > The response contains no mention of TypeConverterSources, @ParameterizedTest, or any implementation mechanism for human-readable value conversion. The response is a Markdown specification without code.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The response is a Markdown specification with no code annotations (@DisplayName, @Description, @ParameterizedTest, @TableTest) shown.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**9/13** · 68948 tokens · 352206ms

- ✅ **3.1-decomposition-concern-separation**: Trigger detection, shipment optimisation, and companion constraint are in separate tables. Not one monolithic table.
- ✅ **3.2-decomposition-trigger-interactions**: At least one row where multiple triggers fire simultaneously (e.g. mixed fulfilment AND mixed stock status).
- ❌ **3.3-depth-all-four-triggers**: All four split triggers are covered: mixed fulfilment, mixed stock status, no single warehouse has all items, different addresses.
  > Table 1 explicitly covers: mixed fulfillment (Row 3), mixed stock status (Row 4, Row 6), and different addresses (Row 7). However, the response states its triggers as "what triggers a split (fulfillment type, availability, address)" — only three triggers — and does not include a row where the sole reason for splitting is that 'no single warehouse has all items'. This concept appears in Table 2 (warehouse allocation) but not in Table 1 (trigger detection)
- ✅ **3.4-depth-companion-ambiguity-surfaced**: The 'no warehouse has both companions' case is included and recognised as a product decision — e.g. flagged as an open question, or the chosen resolution is explained. Not silently omitted.
- ✅ **3.5-depth-companion-edge-cases**: Companion constraint covers at least: companions at same location, companions split from non-companion items, and the ambiguous case (no location has both).
- ✅ **3.6-readability-scenario-names**: Optimisation scenarios describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.7-readability-item-property-mapping**: The trigger table represents multi-item orders where each item has its own fulfillment type, address, and availability. The notation used makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
- ✅ **3.8-readability-scalar-column**: Optimisation table uses a scalar count column (e.g. 'Shirts ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.9-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ❌ **3.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response is a design document providing tables and scenario descriptions but does not include @DisplayName annotations or method name definitions. Scenario names like "Single in-stock home delivery" appear only within table rows, not as method-level declarations
- ❌ **3.11-format-description**: @Description is present on at least the class and provides context beyond what the table rows already express — such as the splitting rules, constraints, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > The response does not include @Description annotations on any class or methods. While it provides narrative context (opening explanation of three concerns, 'Open questions' sections, final paragraph on companion/minimisation priority), these are not formatted as @Description annotations as the assertion requires
- ❌ **3.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The response is a design document without any Java annotations. No @DisplayName, @Description, @TableTest, or other annotations are present; therefore the order cannot be verified and the assertion cannot be satisfied
- ✅ **3.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

