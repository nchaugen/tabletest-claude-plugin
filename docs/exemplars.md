# Exemplar Outputs

An index of model-produced eval outputs worth returning to — proof of what the
bar looks like when it is met, used for regression analysis ("does the new
output do what the exemplar does?") and for calibrating assertions when evals
evolve. The code itself lives in git history; this file records where it is
and *why* it is exemplary.

**Rules:**
- This is an answer key. It lives in `docs/`, which the eval runner strips
  from worktrees — never move or copy entries elsewhere in the repo.
- Never source SKILL.md examples from this file. Skill text must not contain
  eval-derived examples; this index makes them one glance away, so the
  temptation is highest exactly here.
- Each entry names the iteration and commit it was recorded in. If the eval's
  definition changes later (new fingerprint), the entry is historical context,
  not a current target.

## Entries

### eval-15 — tier value sets enabled by API decomposition

`iterations/tabletest/next/iteration-1/eval-15-reis-discount/outputs/src/test/java/com/example/ReisDiscountRuleTest.java` (commit `f3d6143`, 19/20)

`appliesLadderByRecentTicketCount` is the ideal tier table: nine tiers, one
row each, `{min, max}` boundary value sets per tier, `{40, 41, 100}` making
the cap self-evident — the table reads as the business rule itself. It was
*affordable* because the run stubbed a pure `ReisDiscountRule(category, zone,
count)` function, so a row cost nothing. The same model tested through the
full calculator in the next iteration and truncated the ladder to 5 of 9
tiers. Patterns: tier grouping, one row per tier, table-driven API seams.
Known blemish: Adult/Senior enumerated as separate rows (its one failed
assertion) — see eval-28's carrier table for that axis done right.

### eval-2 — error and null cases as rows, built-in conversion

`iterations/tabletest/next/iteration-2/eval-2-parse-dates/outputs/src/test/java/com/example/DateParserTest.java` (commit `f3d6143`, 15/15)

The lone empty-string rejection stays a table row — quoted `''` in a
single-row `@TableTest` with a `Throws?` column — instead of being extracted
to a plain `@Test` with a hardcoded exception (the failure mode of two prior
runs). Null input is a blank-cell row in the main parsing table; `Parsed?` is
typed `LocalDate` so ISO cells use built-in conversion with no converter
method. `@Description` carries the documented assumption plus an open
question. Patterns: exceptions as expected columns, null/empty/blank idioms,
prefer built-in conversion, ambiguity policy.

### eval-28 — options object collapsed into a map column

`iterations/tabletest/next/iteration-2/eval-28-convert-from-methodsource/outputs/src/test/java/com/example/ShippingCostCalculatorTest.java` (commit `f3d6143`, 18/18)

`appliesPackageOptionSurcharges` converts a setter-built options object into
one map column — `[:]` for no options, `[fragile: true, insuredValue: 200]`
for combinations — with a `@TypeConverter` taking `Map<String, String>` and
applying defaults, keeping construction out of every method body.
`totalCostIsIndependentOfCarrier` states irrelevance as a single row with a
`{DHL, UPS, FEDEX}` value set, and the insurance minimum-premium floor is
decomposed into its own boundary table. Patterns: sparse columns → map
column, converter-built parameter objects, irrelevance via value sets,
threshold decomposition.
