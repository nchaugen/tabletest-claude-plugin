# Analysis to-do — tabletest, iteration 76

Compared against **iteration 75**, grading claude-sonnet-5/default.

**1 of 1 evals comparable. 4 assertion verdicts moved.**

**This is the only eval in the closing run whose baseline differs from HEAD by the cut pass
alone** (§ J36). Attribution here is clean; everywhere else it is not.

## Result

eval-25 **23/27**, against 25/27 at it-75. Digest `620427e1277fae25` vs `a28fa4fefdf67ba1`.
Generation 514s of the 900s ceiling — not truncated, no errored evals.

| iteration | digest | score | failed |
|---|---|---|---|
| it-71 | `043a425932cd18b7` | 25/27 | `concern-not-over-split`, `rule-statable-from-table` |
| it-73 | `8349171bf2a50f6a` | 24/27 | + `scenario-names-describe-conditions` |
| it-74 | `3a1f2492c7dd07a8` | 22/27 | `options-as-map`, `options-type-converter`, `dimensions-as-list`, `concern-not-over-split`, `rule-statable-from-table` |
| it-75 | `a28fa4fefdf67ba1` | 25/27 | `rule-statable-from-table`, `scenario-names-describe-conditions` |
| **it-76** | `620427e1277fae25` | **23/27** | `options-as-map`, `options-type-converter`, `concern-not-over-split`, `rule-statable-from-table` |

**The cut pass reverted most of repair 7.** it-76 is the it-74 shape again on three of the four
slots repair 7 won.

## LOST `options-as-map` + `options-type-converter` + `concern-not-over-split` — one decision, one cause

Grader said: _Separate columns 'Handling', 'Fragile', 'Insured Value' used across different tables
instead of a single Options map column._ / _No @TypeConverter for PackageOptions map is present._ /
_addsOversizeSurcharge, addsHazmatHandlingFee, appliesFragileMultiplier, addsInsurancePremium each
fix the same fixture (3kg, EU standard, 10x10x10) and vary a single surcharge, split into four
separate tables._

- Output: `eval-25-convert-from-spock/outputs/src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`
- Narration: `eval-25-convert-from-spock/narration.md` lines 87 and 89

**Cause (from artefact): real regression, attributable to the cut pass. These three are one
decision, as they were in § J31 and § J34.**

The output shape reverted. it-75 wrote two tables with a single composite column —
`appliesSurchargesToTheBaseFee(dimensions: List<Int>, options: PackageOptions, fee: BigDecimal)`.
it-76 wrote seven, four of them one thin table per surcharge with a single unpacked field each:
`addsOversizeSurcharge(dimensions, …)`, `addsHazmatHandlingFee(handling: String?, …)`,
`appliesFragileMultiplier(fragile: Boolean, …)`, `addsInsurancePremium(insuredValue: BigDecimal?, …)`.

**The narration shows the agent reasoning its way out of the rule, and names the reading that let
it.** narration:87 — _"The rule about converters applies to cases like RequestConfig where one cell
holds a map that needs to be unpacked, **not to multiple columns each providing a specific field
value**. So constructing ShippingZone and PackageOptions from their respective column inputs
directly in the test doesn't violate the converter principle, since we're not building domain
objects from single composite cell values."_ It read the composite-cell section as governing how to
*unpack* a map cell, not as governing whether to *choose* one.

narration:89 shows what it did instead, and the problem it was solving is the one the deleted
sentence answered — _"if I make the method signature accept all possible parameters as primitives,
I'd be forcing every table to include columns for values that aren't actually part of that table's
concern … I should create a private helper function that accepts only the parameters relevant to
each concern table, keeping the signature lean and the table focused."_ Lean per-concern signatures
with Kotlin default parameters, rather than one options column with the converter supplying the
rest.

**What the cut pass removed is exactly the sentence that closed that route** (`0019d10`,
*state each rule once, where it is decided*):

> ~~**Leave out what this table says nothing about.** An object with twelve properties whose rule
> reads two is a two-part cell, and the converter supplies valid values for the rest. The only field
> that may not leave is one some surface makes a claim about — see *Assume the Table Is Published*.~~

replaced by a pointer plus a mechanic:

> **Leave out what this table says nothing about** — *Assume the Table Is Published* states the rule
> and its one exception. The mechanic here is that the `@TypeConverter` supplies valid values for
> every part the cell omits, which is what lets the cell carry only the parts the rule reads.

The load-bearing clause — *an object with twelve properties whose rule reads two is a two-part
cell* — is what made a composite column survivable across concern-split tables. Without it the
agent hit the same tension (a table shouldn't carry columns it doesn't read) and resolved it by
splitting the object instead of shortening the cell.

**Note the rule itself was not cut.** *"Decide this from the signature, before drafting columns.
Where a parameter is an object with several optional fields, it is one column — never one column
per field"* is still present, verbatim, and the agent still went the other way. Consistent with the
standing finding that placement and salience beat presence.

**`dimensions-as-list` survived** (`dimensions: List<Int>` in both runs), so repair 7 is partly
intact. The composite-object half is what regressed.

## WON `scenario-names-describe-conditions` — mechanical consequence, not an independent win

Grader said: _Names like 'At the oversize threshold', 'Not fragile', 'Premium below the minimum'
describe conditions, not results._

**Cause (from artefact): the split removed the merged-table baseline row that fails this slot.**
§ J34 recorded the pattern as repeatable, and it-76 makes it 3-for-3: merged-table runs (it-73,
it-75) fail it; split-table runs (it-71, it-74, it-76) pass it. This win is the cost of the loss
above, not a separate gain. It should be read as confirming the trade repair 7 registered, not as
offsetting the regression.

## Verdict for the Part 1 gate

**The cut pass caused a real regression on the one eval that can prove it.** −3 slots
(`options-as-map`, `options-type-converter`, `concern-not-over-split`) against +1 that is the
known mechanical consequence of the same shape change.

This is repairable and the target is identified: restore the *object with twelve properties whose
rule reads two* clause, in the composite-cell section where the decision is made. That is a
free edit and eval-25 is its host, so it is measurable for ~$1.3.

`rule-statable-from-table` still fails on the volumetric divisor — the standing gap, untouched by
this batch, unchanged here (§ J36 action 3).
