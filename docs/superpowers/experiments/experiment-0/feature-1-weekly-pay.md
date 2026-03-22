# Feature 1: Weekly Pay Calculation

**Type:** Simple calculation
**Source:** [Fit CustomerGuide](http://fit.c2.com/wiki.cgi?CustomerGuide) (adapted)

## Rules

- Weekday hours up to 40: base rate
- Weekday hours over 40: 1.5× base rate (time-and-a-half)
- Sunday hours: 2× base rate (double time)
- Public holiday hours: 2× base rate (double time)
- Total pay is floored at zero (no negative pay)

---

## Step 1: Ideal Spec Table

Optimised for readability by a non-developer. A product owner or payroll manager should be able to read each table and say "yes, that's how we calculate pay."

Design choices:
- Plain table format (markdown / `.table` file) — no Java annotations
- The interesting rule is how different hour types convert to payable hours — overtime is worth 1.5×, Sunday/holiday is worth 2×
- Once we have payable hours, pay is just multiplication — no need to test arithmetic
- Keep it minimal: the rules, not the math

### Payable hours

How different hour types contribute to total payable hours. Overtime hours are worth 1.5 standard hours; Sunday and holiday hours are worth 2 standard hours.

| Scenario | Weekday hours | Sunday hours | Holiday hours | Regular? | Overtime? | Payable hours? |
|---|---|---|---|---|---|---|
| Standard full-time week | 40 | | | 40 | 0 | 40 |
| Five hours overtime | 45 | | | 40 | 5 | 47.5 |
| Full Sunday shift | 0 | 8 | | 0 | 0 | 16 |
| Full week plus Sunday | 40 | 8 | | 40 | 0 | 56 |
| Overtime, Sunday, and holiday | 45 | 8 | 4 | 40 | 5 | 71.5 |

### Weekly pay

Once payable hours are known, pay is simply payable hours × hourly rate.

| Scenario | Payable hours | Hourly rate | Weekly pay? |
|---|---|---|---|
| Part-time worker | 24 | 15.00 | 360.00 |
| Standard full-time week | 40 | 15.00 | 600.00 |
| Overtime week with Sunday | 71.5 | 15.00 | 1072.50 |

### Edge cases

| Scenario | Condition | Result? |
|---|---|---|
| Negative hours | -5 weekday hours | 0.00 (floored) |
| Negative rate | Rate is -10.00 | Error |
| No rate provided | Rate is null | Error |

**Strengths:** Focuses on the interesting rules (hour classification and conversion to payable hours), not on testing multiplication. The two-step decomposition separates the domain logic (what counts as payable) from arithmetic (multiplication). Edge cases cover error conditions.

**Weaknesses:** Few scenarios. The payable hours conversion formula is implicit in the table values rather than stated. Sunday and Holiday columns filled with 0 when not relevant obscure which inputs matter for each scenario — empty cells would better signal "not part of this scenario".

---

## Step 2: Ideal Test Table

Optimised for test quality.

Design choices:
- Same two-step decomposition — test the conversion rules, then the multiplication
- Value sets for rate to verify the rate is used, not hardcoded
- Boundary values around the 40-hour threshold
- Error conditions for invalid inputs

```java
@TableTest("""
    Scenario                     | Weekday hours | Sunday hours | Holiday hours | Regular? | Overtime? | Payable hours?
    No hours worked              | 0             |              |               | 0        | 0         | 0
    Part-time                    | 24            |              |               | 24       | 0         | 24
    At overtime threshold        | 40            |              |               | 40       | 0         | 40
    Just over threshold          | 41            |              |               | 40       | 1         | 41.5
    Five hours overtime          | 45            |              |               | 40       | 5         | 47.5
    Full Sunday shift            | 0             | 8            |               | 0        | 0         | 16
    Public holiday               | 0             |              | 8             | 0        | 0         | 16
    Full week plus Sunday        | 40            | 8            |               | 40       | 0         | 56
    Overtime, Sunday, and holiday| 45            | 8            | 4             | 40       | 5         | 71.5
    """)
@DisplayName("Payable hours by rate category")
@Description("Overtime hours count as 1.5 standard hours; Sunday and holiday hours count as 2")
void shouldCalculatePayableHours(int weekdayHours, Integer sundayHours, Integer holidayHours,
        int regularHours, int overtimeHours, BigDecimal payableHours) {
    // Empty cells → null (Integer); null treated as 0 hours
    // payable = regular + overtime×1.5 + sunday×2 + holiday×2
}

@TableTest("""
    Scenario                    | Payable hours | Hourly rate | Weekly pay?
    Zero hours                  | 0             | {15.00, 20.00} | 0.00
    Standard week               | 40            | 15.00       | 600.00
    Standard week, higher rate  | 40            | 20.00       | 800.00
    With overtime               | 47.5          | 15.00       | 712.50
    """)
@DisplayName("Weekly pay is payable hours times hourly rate")
void shouldCalculateWeeklyPay(BigDecimal payableHours, BigDecimal hourlyRate,
        BigDecimal weeklyPay) {
    // pay = payableHours × hourlyRate
}

@TableTest("""
    Scenario         | Weekday hours | Sunday hours | Holiday hours | Hourly rate | Result?
    Negative hours   | -5            |              |               | 15.00       | 0.00
    Negative rate    | 40            |              |               | -10.00      | error
    Null rate        | 40            |              |               |             | error
    """)
@DisplayName("Pay cannot go below zero; invalid rates are rejected")
void shouldHandleEdgeCases(int weekdayHours, int sundayHours, int holidayHours,
        BigDecimal hourlyRate, String result) {
    // negative hours → floor at 0; negative/null rate → error
}
```

**Strengths:** Payable hours table tests the conversion rules with boundary values. Empty cells for Sunday/Holiday hours signal which inputs are relevant to each scenario. Edge cases include error conditions. Clean decomposition.

**Weaknesses:** The pay table is thin because it's testing arithmetic — but that's the point: the interesting rules are in the payable hours conversion. Value sets are only used for "Zero hours" (where any rate × 0 = 0) — the "Standard week" rows use explicit rates since the results differ.

---

## Step 3: Comparison

### Dimension A — Content Convergence

| Aspect | Spec | Test | Agreement? |
|--------|------|------|------------|
| **Decomposition** | Payable hours → multiplication | Same | **Agree** |
| **Core insight** | The rules are in hour classification, not in pay arithmetic | Same | **Agree** |
| **Edge cases** | Negative hours, negative rate, null rate | Same | **Agree** |
| **Boundary values** | 40 and 45 | 40, 41, 45 | Near-aligned |
| **Rate variation** | Single rate | Explicit rates per row (value set only where result is identical) | Near-aligned — test adds a second rate row |

**Convergence:** ~80%. The highest of any feature. Both versions share the same decomposition, the same insight about what's interesting, and the same edge cases.

### Dimension B — Table Structure

| Aspect | Spec | Test |
|--------|------|------|
| **Number of tables** | 3 (payable hours, pay, edge cases) | 3 (payable hours, pay, edge cases) |
| **Table boundaries** | Same three concerns | Same three concerns |

**Structural alignment:** High. Both versions independently arrive at the same three-table structure.

### Dimension C — Value Representation

| Aspect | Spec | Test | Resolvable? |
|--------|------|------|-------------|
| **Format** | Plain markdown | `@TableTest` Java code | Different roles — spec is documentation, test is code |
| **Rate variation** | Single rate | Value sets | Value sets are the right answer for "test multiplication without testing multiplication" |
| **Error representation** | Prose ("Error") | String "error" | TypeConverter or assertion strategy |

---

## Step 4: Merged Table

The merge is straightforward because spec and test already converge strongly.

```java
@DisplayName("Weekly Pay Calculation")
@Description("Calculates weekly pay by converting all hours to payable hours "
    + "(overtime at 1.5×, Sunday and holiday at 2×), then multiplying by the hourly rate")
class WeeklyPayTest {

    @TableTest("""
        Scenario                      | Weekday hours | Sunday hours | Holiday hours | Regular? | Overtime? | Payable hours?
        // Up to 40 weekday hours at base rate
        No hours worked               | 0             |              |               | 0        | 0         | 0
        Part-time, three days         | 24            |              |               | 24       | 0         | 24
        Standard full-time week       | 40            |              |               | 40       | 0         | 40
        // Beyond 40 weekday hours at 1.5× rate
        Just over the threshold       | 41            |              |               | 40       | 1         | 41.5
        Five hours overtime           | 45            |              |               | 40       | 5         | 47.5
        // Sunday and holiday at 2× rate
        Full Sunday shift             | 0             | 8            |               | 0        | 0         | 16
        Public holiday                | 0             |              | 8             | 0        | 0         | 16
        // Combined
        Full week plus Sunday         | 40            | 8            |               | 40       | 0         | 56
        Overtime, Sunday, and holiday  | 45            | 8            | 4             | 40       | 5         | 71.5
        """)
    @DisplayName("Payable hours by rate category")
    @Description("Overtime hours count as 1.5 standard hours; "
        + "Sunday and holiday hours count as 2 standard hours")
    void shouldCalculatePayableHours(int weekdayHours, Integer sundayHours, Integer holidayHours,
            int regularHours, int overtimeHours, BigDecimal payableHours) {
        // Empty cells → null (Integer); null treated as 0 hours
        PayableHours result = PayableHours.from(weekdayHours, sundayHours, holidayHours);
        assertEquals(regularHours, result.regular());
        assertEquals(overtimeHours, result.overtime());
        assertEquals(payableHours, result.total());
    }

    @TableTest("""
        Scenario                    | Payable hours | Hourly rate | Weekly pay?
        Zero hours                  | 0             | {15.00, 20.00} | 0.00
        Standard week               | 40            | 15.00       | 600.00
        Standard week, higher rate  | 40            | 20.00       | 800.00
        Overtime week               | 47.5          | 15.00       | 712.50
        """)
    @DisplayName("Weekly pay is payable hours times hourly rate")
    void shouldCalculateWeeklyPay(BigDecimal payableHours, BigDecimal hourlyRate,
            BigDecimal weeklyPay) {
        assertEquals(weeklyPay, payableHours.multiply(hourlyRate));
    }

    @TableTest("""
        Scenario         | Weekday hours | Sunday hours | Holiday hours | Hourly rate | Result?
        Negative hours   | -5            |              |               | 15.00       | 0.00
        Negative rate    | 40            |              |               | -10.00      | error
        No rate provided | 40            |              |               |             | error
        """)
    @DisplayName("Pay cannot go below zero; invalid rates are rejected")
    void shouldHandleEdgeCases(int weekdayHours, Integer sundayHours, Integer holidayHours,
            BigDecimal hourlyRate, String result) {
        // negative hours → floor at 0; negative/null rate → error
    }
}
```

### Tension Catalogue

| # | Tension | Spec pulls toward | Test pulls toward | Resolution |
|---|---------|-------------------|-------------------|------------|
| 1 | **What to test** | The rules (hour classification) | The rules + arithmetic verification | Focus tables on the rules. Multiplication is verified with minimal value-set rows, not a dedicated table. |
| 2 | **Pay table depth** | Just show that pay = payable hours × rate | Verify with multiple values | Explicit rate per row. Value set only for "Zero hours" (where result is identical regardless of rate). |
| 3 | **Error conditions** | Part of the spec ("what happens if…") | Boundary and null testing | Shared table — error cases are business rules too. |
| 4 | **Format** | Plain markdown | `@TableTest` Java code | Different roles. `@Description` bridges the gap. |

### Scores

| Dimension | Score (1–5) | Notes |
|-----------|-------------|-------|
| **Spec readability** | **5** | The payable hours table IS the interesting rule. The pay table is trivially readable. Edge cases cover error conditions. A payroll manager understands all three tables without explanation. |
| **Test quality** | **4** | Payable hours conversion tested with boundaries. Pay verified with value sets. Error conditions covered (negative hours, negative rate, null rate). Loses one point: the pay table is deliberately thin — it's testing multiplication, which is fine but minimal. |

### Observations

1. **Focus tables on rules, not arithmetic.** The original version had many tables testing pay calculation at different rate/hour combinations — effectively testing multiplication. The refactored version puts the interesting rules (hour classification and conversion to payable hours) in one table and reduces pay to "it's just multiplication." This is cleaner for both spec (the rules are what matter) and test (don't test the language's arithmetic).

2. **The payable hours concept is the key abstraction.** Converting overtime to 1.5× equivalent hours and Sunday/holiday to 2× equivalent hours creates a clean intermediate representation. The spec can explain the conversion; the test can verify it; the pay calculation is trivially derived. Good abstractions serve both goals.

3. **Error conditions are business rules.** "What happens with a negative rate?" is a question a payroll manager would ask. Including these in the experiment validates that error handling specs and error handling tests converge.

4. **Spec and test converge most when the decomposition is right.** At ~80% convergence, this is the highest of all features — because the two-step decomposition (classify → multiply) is the natural way both a spec author and a test author would think about it.

5. **Empty cells signal "not part of this scenario".** Using blank cells for Sunday and Holiday hours when they are zero makes the table easier to scan — the reader immediately sees which inputs are relevant. This requires `Integer` parameter types (since `int` cannot be `null`) with null-to-zero handling in the test body, or a type converter. The readability gain justifies the small implementation overhead.

6. **Value sets mean "any value gives the same result".** A value set `{15.00, 20.00}` asserts that the result is identical regardless of which value is chosen. Using a value set where results differ (e.g., 40 × 15 ≠ 40 × 20) is a semantic error — use separate rows with explicit expected values instead. Value sets are appropriate for "Zero hours" (any rate × 0 = 0) but not for non-zero payable hours.

7. **Self-check arithmetic in expected values.** The original "Overtime, Sunday, and holiday" row had payable hours = 55.5, which is wrong (correct: 71.5). The error persisted through all four steps without detection. Expected values in example tables should be verified independently — the whole point of concrete examples is that they are checkable.
