All files look correct. Here's a summary of what was delivered:

## Files Created

**`src/main/java/WeeklyPayCalculator.java`** — stub with `calculatePay(int weekdayHours, int sundayHours, int holidayHours, double hourlyRate)` throwing `UnsupportedOperationException`.

**`src/test/java/WeeklyPayCalculatorTest.java`** — 6 focused `@TableTest` methods:

| Test Method | Concern | Data Rows |
|---|---|---|
| `base_pay_at_base_rate` | Weekday hours ≤ 40 at base rate; 40 boundary | 3 |
| `overtime_after_40_weekday_hours` | Hours > 40 at 1.5× base rate | 3 |
| `sunday_double_time` | Sunday hours at 2× base rate | 2 |
| `holiday_double_time` | Holiday hours at 2× base rate | 2 |
| `negative_hourly_rate_rejected` | Validation: negative rate → exception | 1 (`@Test`) |
| `combined_multiple_rules` | Integration: all rules together | 1 |

**`build.gradle`** — added `tabletest-junit:1.2.1` test dependency.

### Design Decisions

- **7 independent concern tables** (per skill guidance): one per rule, each with irrelevant inputs blanked
- **Separation verified**: row names need no "and" qualifiers; scenario names state conditions not outcomes
- **Thresholds visible**: base_pay and overtime share boundary values (40/41) making the threshold explicit
- **Zero-hour rows** in double-time tables test that non-applicable rules don't contribute pay
- The validation row uses `@Test` + `assertThrows` (per skill: error cases separate from value tables)

### Arithmetic Verification

All expected values trace to the rules: e.g., `overtime_test` "One hour over" → 40×$25 + 1×$37.50 = $1037.50; `combined` → 40×$30 + 10×$45 + 8×$60 + 4×$60 = $2820.00