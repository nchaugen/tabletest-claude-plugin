A **conversion** eval: the source is one flat, single-method parametrised test with 17 rows that mix
every rule together. The task is not a 1:1 port — it is to *re-decompose* that one table into several
`@TableTest` methods by concern, and to use the TableTest features the source framework lacked. Four
concerns fall out: base-rate lookup, dimensional-weight override, surcharges on the base cost, and
carrier independence. The 17 source rows are evidence of the rules, not a row budget to preserve.

## This conversion (eval-25)

Source: a Spock `@Unroll` data-table spec (Groovy). Target language: **Kotlin**. Beyond the shared
decomposition, this conversion must leave **no Groovy/Spock syntax** behind (`where:`, `expect:`, `def`,
`Specification`, GString `#interpolation`) and **remove the Spock dependency** from the build. The four
conversion evals (25 Spock, 26 Kotest, 27 TestNG, 28 @MethodSource) share one SUT and one target
solution; only the source framework, output language, and the cleanup obligations differ.

## The concerns

`calculateShippingCost(zone, weight, dimensions, options, carrier)` runs a pipeline, and the source test
exercises all of it through one 17-row table. Pulled apart:

| Concern | Governs | Held fixed to isolate it |
|---|---|---|
| **Base rate** | region × speed × weight-bracket lookup on the effective weight | small dimensions (so effective = actual), no options, carrier DHL |
| **Dimensional weight** | effective weight = max(actual, volumetric); volumetric = L×W×H / 5000 | zone EU standard, no options |
| **Surcharges** | oversize (+10 flat, any dim > 100), hazmat (+8 flat), fragile (×1.15), insurance (0.6%, $3 floor), and their **order** | zone EU standard, weight 3kg (base 7.50) |
| **Carrier independence** | carrier never changes the price | zone/weight/options fixed |

## Modelling decisions this eval fixes

- **Carrier is not a pricing input.** The SUT ignores `carrier` entirely; the three source rows
  `same rate DHL/UPS/FEDEX` exist only to prove that. In TableTest that is one row with a **value set**
  `{DHL, UPS, FEDEX}` and a single expected cost — not three rows, and certainly not a carrier column
  threaded through every table.
- **Package options collapse into a single map column with a `@TypeConverter`.** `PackageOptions` has
  three optional fields that are absent in most rows — the skill's own `column-design.md` names this the
  case where maps beat separate columns ("a sea of blank cells"). The rewarded shape is one `Options`
  column holding `[fragile: true]`, `[insuredValue: 500]`, `[handling: hazmat]`, or `[:]`, converted to
  `PackageOptions` by a `@TypeConverter` so the method body stays pure arrange-act-assert. Three sparse
  boolean/BigDecimal/String columns (what 3 of the 4 iteration-40 solutions did) is the lesser choice
  this eval marks down.
  - **The no-options row must be `[:]` (empty map), not a blank cell.** A blank cell converts to `null`
    and **bypasses the `@TypeConverter` entirely**; `[:]` invokes it with an empty map, which returns a
    default `PackageOptions`. This is the exact trap that pushed iteration-40 solutions to a private
    helper or to null-typed map params instead of a real converter — `[:]` is the one move that keeps the
    converter on every row's path.
- **Dimensions are a `[L, W, H]` list**, converted to `List<Integer>` — not three scalar columns.
- **Money and weight are `BigDecimal`, compared with `compareTo` (== 0)**, never `equals` (scale differs:
  `12.5` vs `12.50`) and never `double`.

## Reference decomposition

Values verified against the SUT. Base rate is `max(actual, volumetric)` weight → a 4×4 (region×speed by
weight-bracket ≤1 / ≤5 / ≤15 / >15) lookup; brackets are inclusive at the boundary.

**Base rate** — dimensions fixed small so effective = actual; no options; carrier DHL.

| Scenario | Region | Speed | Weight | Base rate? |
|---|---|---|---|---|
| EU standard, ≤1kg | EU | standard | 0.5 | 5.00 |
| EU standard, 1kg boundary | EU | standard | 1 | 5.00 |
| EU standard, ≤5kg | EU | standard | 3.0 | 7.50 |
| EU standard, 5kg boundary | EU | standard | 5 | 7.50 |
| EU standard, ≤15kg | EU | standard | 10.0 | 12.50 |
| EU standard, 15kg boundary | EU | standard | 15 | 12.50 |
| EU standard, >15kg | EU | standard | 25.0 | 20.00 |
| EU express | EU | express | 3.0 | 12.00 |
| US standard | US | standard | 0.5 | 7.00 |
| US express | US | express | 10.0 | 30.00 |

Enumerating the full 16-cell matrix instead is equally acceptable — a lookup table with one row per cell
is complete coverage, not over-enumeration. The boundary rows (1 / 5 / 15) are the falsifiability-strong
part: they pin the `≤` brackets, which a sampled table alone leaves ambiguous.

**Dimensional weight override** — zone EU standard; only dimensions vary.

| Scenario | Weight | Dimensions | Base rate? |
|---|---|---|---|
| Actual weight governs | 3.0 | [30, 20, 15] | 7.50 |
| Dimensional weight governs | 1.0 | [70, 50, 10] | 12.50 |

`[70, 50, 10]` → volume 35 000 / 5000 = 7.0kg, which beats the 1.0kg actual → the ≤15 bracket, 12.50.

**Surcharges** — zone EU standard, weight 3.0kg (base 7.50); one table, `Options` as a map.

| Scenario | Dimensions | Options | Total cost? |
|---|---|---|---|
| No surcharge (baseline) | [30, 20, 15] | [:] | 7.50 |
| Oversize (a dimension > 100) | [120, 5, 5] | [:] | 17.50 |
| Hazmat handling | [30, 20, 15] | [handling: hazmat] | 15.50 |
| Fragile multiplier | [30, 20, 15] | [fragile: true] | 8.625 |
| Insured below floor → $3 min | [30, 20, 15] | [insuredValue: 200] | 10.50 |
| Insured where the premium meets the floor | [30, 20, 15] | [insuredValue: 500] | 10.50 |
| Insured above floor → 0.6% | [30, 20, 15] | [insuredValue: 1000] | 13.50 |
| Fragile + insured (order proof) | [30, 20, 15] | [fragile: true, insuredValue: 200] | 11.625 |

Arithmetic on base 7.50: oversize +10 → 17.50; hazmat +8 → 15.50; fragile ×1.15 → 8.625; insurance is
`max(value×0.006, 3.00)` so 200 and 500 both floor to +3.00 → 10.50 while 1000 → +6.00 → 13.50. Note what the 500 row does
and does not do: at 500 the premium and the floor are *equal*, so it cannot separate `max(premium,
3.00)` from any other reading of the floor — 200 already does that. What it fixes is how far the
floor reaches, ruling out a crossover below 500. That is a real obligation, but it is a boundary of
two published constants rather than one the requirement names, so a solution omitting it has not
missed a stated rule.

The last row proves **order**: fragile multiplies the base (7.50×1.15 = 8.625) and insurance is added *after*,
un-multiplied (+3.00) → 11.625. A `fragile + hazmat` row (`(7.50+8)×1.15 = 17.825`) optionally completes
the order proof by showing the flat fee *is* inside the multiplier.

**Carrier independence** — zone EU express, weight 3.0kg (base 12.00); value set.

| Scenario | Carrier | Total cost? |
|---|---|---|
| Carrier does not affect price | {DHL, UPS, FEDEX} | 12.00 |

## Scenario coverage — the obligations each concern must hit

- **Base rate** — (a) each of the four region/speed pairings selects its own rate column; (b) each weight
  bracket (≤1 / ≤5 / ≤15 / >15) is selected, ideally at its inclusive boundary. Full-matrix (16) or
  boundary-anchored sample (~10) both cover this; a sample with no boundary row leaves (b) unproven.
- **Dimensional weight** — (a) actual weight governs when larger; (b) volumetric governs when larger.
  Two rows, complete.
- **Surcharges** — one obligation per surcharge (oversize strict `>100`, hazmat flat, fragile ×1.15,
  insurance floor with below/at/above rows) plus one **order/composition** obligation (fragile+insured,
  optionally fragile+hazmat). One table, ~8 rows — *not* four tables. Splitting each surcharge into its
  own table (iteration-40 eval-27/28) re-pays the same fixture four times for one concern; it still
  passes `concerns-decomposed`, but it is the over-decomposition to avoid.
- **Carrier independence** — one obligation: price is invariant across carriers. One value-set row.

Target shape: **four `@TableTest` methods**. eval-26's 4-method structure is the right altitude (it just
missed the options map); eval-25 has the map but splits surcharges further and used a helper, not a
converter; eval-27/28 over-split surcharges into 8 methods.

## Out of scope, but foreseeable

- **US express / EU express upper brackets** the source never exercised (e.g. US-express >15 → 45.00).
  Filling the base matrix to 16 is fine; it is not a coverage gap when a boundary-anchored sample is used
  instead.
- **Invalid region/speed** throws `IllegalArgumentException`. The source doesn't test it and the prompt is
  a pure conversion, so an error-path table is neither expected nor penalised if added.
- **Carrier as an enum value vs string** — the value set uses the `Carrier` enum directly; no converter
  needed.

## Judging

Score the **re-decomposition** first: four concerns in separate `@TableTest` methods, surcharges kept as
*one* table (not four), carrier as a value set (not three rows or a threaded column), and the 17 flat
source rows not merely copied across. Then the **feature use** the assertions reward: options as a single
map column with a `@TypeConverter` (`[:]` for the no-options row so the converter runs), dimensions as a
list, BigDecimal via `compareTo`. Then the **cleanup**: none of the source framework's syntax or
dependency survives.

No assertion changes for this eval. Unlike eval-30's removed `scalar-quantity` assert, the failing
`options-as-map` and `options-type-converter` here are **correct** — they catch a real gap: 3 of 4
iteration-40 solutions used sparse columns, and the fourth used a helper instead of a converter after
misreading the blank-cell-bypasses-converter behaviour. The fix is a skill lesson (`[:]`-not-blank for an
all-optional map column), not a rubric change. Decomposition granularity (4 vs 8 methods) is currently
unpenalised latitude; the target is 4, and over-decomposition is a legibility cost worth a note but not a
failure under the present assertions.
