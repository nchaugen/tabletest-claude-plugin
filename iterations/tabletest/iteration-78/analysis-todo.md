# Analysis to-do — tabletest, iteration 78

Compared against **iteration 76**, grading claude-sonnet-5/default. 1 of 1 comparable, 5 moved.

**Repair 8 measured** (`a60d3e1`, restore of the clause `0019d10` replaced in *Putting a Composite
Value in a Cell*). Supersedes `iteration-77`, which was void (`timeout-after-api-retry`, nothing
delivered).

## ⚠️ Read this before the score

**`failure_kind: timeout-after-delivery`, `duration_ms: 901626`, `harvested_files: 1`.** The run
delivered a test file and was killed by the 900s deadline before it finished the build file. Per
`AGENTS.md` that is scored on what it delivered — but **it is not the same event as it-76**, which
completed in 514s. **The 22/27 is not comparable to it-76's 23/27 and must not be read as a
regression.**

Three of the five failures are truncation artefacts with one cause — `build.gradle.kts` was never
updated:

- `has-tabletest-dependency` — _Build file does not contain tabletest-junit dependency_
- `spock-dependency-removed` — _Build file still references Spock/Groovy_
- `compiles` — fails because `@TableTest` cannot resolve without that dependency

The agent wrote the test file as its last act (`Write ./src/test/kotlin/com/example/ShippingCostCalculatorTest.kt (150 lines)` is the final line of the narration). The build edit was next and never happened.

## Verdict against the predictions registered in § J37

| # | Prediction | Outcome |
|---|---|---|
| 1 | `options-as-map`, `options-type-converter`, `concern-not-over-split` return together; 25/27 | **Partly confirmed** — the first two returned, the third did not; score confounded by truncation |
| 2 | `scenario-names-describe-conditions` lost again | **Not confirmed, and not cleanly tested** — it passes, but the output is not the merged shape the pattern is about |
| 3 | Falsifier: `dimensions-as-list` stays passing | **Held** — `dimensions: List<Int>` in the signature |
| 4 | `rule-statable-from-table` still fails on the volumetric divisor | **Confirmed** — _"rely on the volumetric divisor (L×W×H/5000) which appears nowhere"_ |

## WON `options-as-map` + `options-type-converter` — repair 8 confirmed on its target

The output carries one composite column and a converter for it:

```kotlin
fun appliesPackageHandlingSurcharges(options: PackageOptions, cost: BigDecimal)

@TypeConverter
fun parsePackageOptions(fields: Map<String, String>): PackageOptions
```

against it-76's four thin tables with `handling: String?`, `fragile: Boolean`,
`insuredValue: BigDecimal?` unpacked into separate columns.

**The narration shows the restored clause doing exactly the work the analysis claimed, and it
reverses it-76's reasoning on the same example.** narration:71 — _"Looking at the Quick Examples and
how PackageOptions **mirrors RequestConfig's structure** — both have optional fields that **should
probably collapse into a single map-cell converter rather than sprawl across separate columns**. …
PackageOptions with its three optional fields (fragile, insuredValue, handling) creates the same
readability problem if spread as individual columns."_

it-76 read the same `RequestConfig` example as *excluding* its case (narration:87 — _"applies to
cases like RequestConfig where one cell holds a map that needs to be unpacked, **not** to multiple
columns each providing a specific field value"_). it-78 reads it as *including* its case. Same eval,
same example, opposite readings; the restored clause is the only difference between the two skill
states.

**This is the finding, and it does not depend on the score.** It is an output-and-narration fact
about what the guidance caused, unaffected by the run being cut short.

## LOST `concern-not-over-split` — did not return, and the cause is a different decision

Grader said: _addsAnOversizeFee and appliesPackageHandlingSurcharges both target the surcharges
concern with a similar EU-standard/3kg fixture, splitting oversize out into its own single-sub-rule
table._

**Cause (from artefact): a deliberate and separately-argued split, not the it-76 field-per-table
spread.** narration:93 — _"oversize isn't even part of the PackageOptions family at all. It's driven
by the `dimensions` parameter, which is completely separate from the options object that contains
fragile, insurance, and handling."_ narration:97 — _"I'm settling on keeping the oversize logic in
its own dedicated table (Table B)."_

Four tables, not it-76's seven, and the three options surcharges are merged into one. The remaining
split is oversize-vs-options, which the agent reasoned about on parameter identity rather than on
"each table exercises a different field". **Repair 8 was never aimed at this**, and the § J37
prediction that grouped it with the other two was wrong to to do so on this evidence — the three
moved together in it-74→it-75→it-76 because one shape change drove all three; here the shape only
partly changed.

## Not settled by this run

- **The score.** 22/27 is a truncated run's number. A clean re-run is the only way to compare
  against it-76's 23/27 or it-75's 25/27.
- **Prediction 2.** `scenario-names-describe-conditions` passes, but the merged-table baseline row
  the § J34 pattern is about is not present in this four-table shape. The 3-for-3 pattern is
  neither confirmed nor broken.
- **`concern-not-over-split`.** Open, with a new and better-argued cause than it-76's.

## Flag: eval-25 has now failed its budget twice running

it-76 completed in 514s. it-77 died at 1,010s (`timeout-after-api-retry`, void). it-78 delivered and
was cut at 901s. Mean across 12 stored runs is 577s. Two consecutive budget failures on the batch's
only near-ceiling eval is worth watching — `AGENTS.md` reads a newly-truncating slowest eval as a
sign the skill got longer, though repair 8 added one sentence and cannot plausibly account for a
390s swing. Re-baselining the timeout is ruled out (§ J36: editing `eval.json` re-fingerprints).
