# Advanced Rewrite Evals — Shipping Cost Calculator

## Context

The eval suite lacks coverage for converting tests from non-JUnit frameworks (Spock, Kotest, TestNG) and for JUnit `@MethodSource` patterns. The existing conversion evals (18, 19) cover simple `@Test` → TableTest and `@CsvSource` → TableTest, but don't exercise advanced TableTest features like type converters, maps, nested collections, value sets, and numeric types together.

This spec defines 4 new evals (25–28) sharing the same shipping cost domain, each presenting messy/verbose parameterized tests in a different framework for conversion to TableTest.

## The Domain: Shipping Cost Calculator

### Function under test

```java
BigDecimal calculateShippingCost(
    ShippingZone zone,        // region + speed
    BigDecimal weightKg,      // package weight
    List<Integer> dimensions,  // [length, width, height] in cm
    PackageOptions options,    // fragile, insuredValue, handling — sparse fields
    Carrier carrier            // enum: DHL, UPS, FEDEX
)
```

### Business rules

1. **Base rate** = zone base rate × weight tier multiplier
   - Zones: EU/standard (€5), EU/express (€8), US/standard (€7), US/express (€12)
   - Weight tiers: 0–1kg (×1.0), 1–5kg (×1.5), 5–20kg (×2.5), 20+kg (×4.0)

2. **Dimensional weight override** — if `(L × W × H) / 5000 > weightKg`, use dimensional weight for tier lookup instead of actual weight

3. **Surcharges** (additive, applied after base rate):
   - `fragile: true`: +15% of base rate
   - `insuredValue: N`: +€3.00 flat (any non-null value triggers fee)
   - `handling: hazmat`: +€8.00 flat
   - `oversize` (any dimension > 100cm): +€10.00 flat

4. **Carrier equivalence** — DHL, UPS, FEDEX all produce the same rate for identical inputs

### Feature mapping

| TableTest feature | How it appears in the converted output |
|-------------------|---------------------------------------|
| **TypeConverter** | Map `[fragile: true, insuredValue: 500]` → `PackageOptions` via `@TypeConverter` method |
| **Maps** | Package options as `[key: value]` pairs — sparse fields that vary per scenario (most rows only need 0–2 of 3 fields) |
| **Nested collections** | Dimensions as `[30, 20, 15]` list in the table |
| **Value sets** | `{DHL, UPS, FEDEX}` carriers that produce the same cost |
| **Numbers** | Weight (`BigDecimal`), dimensions (`int`), cost (`BigDecimal`), insuredValue (`int`) |

### Why maps for options (not zone)

Zone has only 2 fields (region, speed) both always populated — two simple columns are clearer. Package options have 3 fields (fragile, insuredValue, handling) where most scenarios only use 0–1 of them. Separate columns would create a sea of blank cells. A map column like `[fragile: true]` or `[fragile: true, insuredValue: 200]` is more readable and justifies a `@TypeConverter`.

### Expected concern decomposition

The model should produce multiple `@TableTest` methods, not one monolithic table:

1. **Base rate by zone and weight** — zone map + weight → base cost
2. **Dimensional weight override** — large dimensions that override actual weight
3. **Surcharges** — option flags → additional charges on top of base rate
4. **Carrier equivalence** — value sets showing carriers produce same result

## The 4 Evals

All evals present the same shipping cost scenario. The source test is written in that framework's idiomatic messy/verbose parameterized style — mixed concerns in one test method, manual object construction, duplicated setup.

### Eval 25: Spock Framework (Groovy)

**Directory:** `eval-25-convert-from-spock`

**Source style:** Single `def "test shipping"()` with `where:` block. Inline Groovy object construction (`new ShippingZone(region: 'EU', speed: 'express')`), computed expected values via Groovy expressions in the `where:` table, all concerns mixed into one data table with many columns.

**Framework-specific assertion:** `no-groovy-syntax` — no `def`, `where:`, GString, Groovy closures, or Spock assertions (`thrown()`, `old()`) in output.

### Eval 26: Kotest Data-Driven (Kotlin)

**Directory:** `eval-26-convert-from-kotest`

**Source style:** `forAll` block with `row()` calls. Manual data class construction per row (`ShippingZone(region = "EU", speed = "express")`), verbose lambda setup, all concerns in one `forAll`.

**Framework-specific assertion:** `no-kotest-syntax` — no `forAll`, `row()`, `withData`, `kotest` imports, or Kotlin-specific test DSL in output.

### Eval 27: TestNG @DataProvider (Java)

**Directory:** `eval-27-convert-from-testng`

**Source style:** `@DataProvider` method returning `Object[][]` with `new ShippingZone("EU", "express")` inline. Single `@Test(dataProvider = "shippingData")` method consuming all concerns. Verbose array construction.

**Framework-specific assertion:** `no-testng-artifacts` — no `@DataProvider`, `@Test(dataProvider=...)`, `Object[][]`, or TestNG imports in output.

### Eval 28: JUnit @MethodSource (Java)

**Directory:** `eval-28-convert-from-methodsource`

**Source style:** `@MethodSource("shippingData")` with static `Stream<Arguments>` factory method. `Arguments.of(new ShippingZone(...), ...)` per row. All concerns in one stream.

**Framework-specific assertion:** `no-methodsource-artifacts` — no `@MethodSource`, `Stream<Arguments>`, `Arguments.of`, or JUnit Jupiter parameterized imports in output.

## Assertions

### Shared assertions (all 4 evals)

| ID | Text |
|----|------|
| `has-tabletest-annotation` | Output contains a `@TableTest` annotation |
| `options-as-map` | Package options (fragile, insuredValue, handling) are collapsed into a single map column like `[fragile: true, insuredValue: 500]` — not kept as three separate columns with mostly-blank cells |
| `options-type-converter` | A `@TypeConverter` method is present that accepts `Map<String, String>` (or similar) and returns `PackageOptions`, applying defaults for missing keys |
| `dimensions-as-list` | Dimensions are represented as a `[L, W, H]` list in the table — not as three separate length/width/height columns |
| `uses-value-sets` | At least one table uses value set syntax `{DHL, UPS, FEDEX}` (or subset) to express carrier equivalence, rather than duplicating rows per carrier |
| `concerns-decomposed` | Multiple `@TableTest` methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence |
| `numeric-types-correct` | Weight and cost parameters use `BigDecimal` (not `double` or `String`). Dimensions use `Integer` or `int`. |
| `scenario-column-present` | Each table has a scenario/description column as the leftmost column |
| `scenario-names-describe-conditions` | Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('€12.00', 'Surcharge applied') |
| `has-question-mark-column` | At least one output column name ends with `?` (e.g. 'Cost?', 'Base rate?') |
| `business-language-columns` | Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims') |
| `no-if-switch-in-method` | Test method bodies contain no `if` or `switch` statements |
| `annotation-order` | Annotations appear in order: `@DisplayName` (if present), `@Description` (if present), `@TableTest` |
| `has-descriptive-title` | Each test method has a `@DisplayName` or a method name that reads as a clear title |

### Per-eval framework assertions

One additional assertion per eval as described above (`no-groovy-syntax`, `no-kotest-syntax`, `no-testng-artifacts`, `no-methodsource-artifacts`).

**Total: 15 assertions per eval** (14 shared + 1 framework-specific).

## Source Test Guidelines

Each prompt.md should contain:
1. A brief intro: "I have this [framework] test and I'd like to convert it to use TableTest:"
2. The messy source test in a code block (~60-80 lines)
3. A closing question: "Can you convert this to TableTest?"

The source tests should be messy/verbose to simulate real legacy code:
- All concerns in one test method/data provider
- Manual object construction (not factories)
- Separate columns for length/width/height instead of dimensions list
- Separate columns for fragile/insuredValue/handling instead of options map (sea of blanks)
- Duplicated rows for each carrier instead of using equivalence
- Hardcoded numeric values without business context
- ~12-15 data rows mixing all concerns

## Verification

After creating the evals, run:
```bash
node scripts/run-evals.js --iteration N --evals 25,26,27,28
```

Check that:
- Each eval runs without errors
- Grading produces meaningful pass/fail for each assertion
- The model's output demonstrates conversion to proper TableTest with all 5 advanced features
