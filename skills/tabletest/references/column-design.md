# Column Design Best Practices

How you structure columns significantly impacts table readability. This guide covers when to split, when to combine, and how to evolve column design iteratively.

## Table of Contents
- [It Depends: Maps vs Separate Columns](#it-depends-maps-vs-separate-columns)
- [Decision Criteria](#decision-criteria)
- [Iterative Column Evolution](#iterative-column-evolution)
- [Guidelines for Column Design](#guidelines-for-column-design)
- [Red Flags](#red-flags)
- [Do Not Encode Two Values into One Cell](#do-not-encode-two-values-into-one-cell)
- [Column Naming Evolution](#column-naming-evolution)
- [Empty Cells for Optional Inputs](#empty-cells-for-optional-inputs)
- [When Reviewing Multiple Tables](#when-reviewing-multiple-tables)
- [Summary](#summary)

## It Depends: Maps vs Separate Columns

Both approaches have valid use cases. Choose based on your specific scenario:

### Example 1: When All Scenarios Have Same Properties

When every scenario uses the same set of properties, separate columns are clearer:

```java
// ❌ HARDER TO READ - combined format when structure is consistent
@TableTest("""
    Scenario          | Primary Request  | Secondary Request | Response?
    Both ok           | OK in 100ms      | OK in 10ms        | OK in <50ms
    Primary fails     | ERROR in 100ms   | OK in 10ms        | OK in <50ms
    Both fail         | ERROR in 100ms   | ERROR in 10ms     | ERROR in <150ms
    """)
void respondsWithTheFirstSuccess(String primaryRequest, String secondaryRequest, String response) {
    // Need converter methods to parse each combined string
}

// ✅ EASIER TO READ - separate columns when structure is consistent
@TableTest("""
    Scenario          | Primary | Primary ms | Secondary | Secondary ms | Response? | Response ms?
    Both ok           | OK      | 100        | OK        | 10           | OK        | <50
    Primary fails     | ERROR   | 100        | OK        | 10           | OK        | <50
    Both fail         | ERROR   | 100        | ERROR     | 10           | ERROR     | <150
    """)
void respondsWithTheFirstSuccess(String primaryStatus, Long primaryMs, String secondaryStatus, Long secondaryMs,
          String responseStatus, Long responseMs) {
    // Values directly usable, only need parseResponseTime converter for <50 format
}
```

**Why separate columns work here:**
- Every row has the same structure (status + ms)
- Values align vertically, easy to scan
- Relationships between properties are visible
- Less parsing code needed

### Example 2: When Properties Vary by Scenario

When different scenarios need different properties, maps with defaults are clearer:

```java
// ❌ HARDER TO READ - many blank cells with separate columns
@TableTest("""
    Scenario          | Method | Timeout | Auth        | Retry | Cache | Timeout Used?
    Basic request     | GET    |         |             |       |       | 3000
    With timeout      | POST   | 5000    |             |       |       | 5000
    With auth         | GET    |         | Bearer xyz  |       |       | 3000
    Full config       | POST   | 5000    | Bearer xyz  | 3     | true  | 5000
    """)
void appliesRequestConfiguration(String method, Integer timeout, String auth, Integer retry, Boolean cache, int timeoutUsed) {
    // Need to handle nulls and provide defaults in test method
}

// ✅ EASIER TO READ - map with defaults when properties vary
@TableTest("""
    Scenario          | Request Config                                  | Timeout Used?
    All defaults      | [:]                                             | 3000
    Basic request     | [method: GET]                                   | 3000
    With timeout      | [method: POST, timeout: 5000]                   | 5000
    With auth         | [method: GET, auth: Bearer xyz]                 | 3000
    Full config       | [method: POST, timeout: 5000, auth: Bearer xyz, retry: 3, cache: true] | 5000
    """)
void appliesRequestConfiguration(RequestConfig config, int timeoutUsed) {
    assertEquals(timeoutUsed, gateway.timeoutFor(config));
}

@TypeConverter
public static RequestConfig parseRequestConfig(Map<String, String> config) {
    return new RequestConfig(
        config.getOrDefault("method", "GET"),
        parseInt(config.getOrDefault("timeout", "3000")),
        config.get("auth"),
        parseInt(config.getOrDefault("retry", "1")),
        parseBoolean(config.getOrDefault("cache", "false"))
    );
}
```

Declare the parameter as the **domain type**, not `Map<String, String>` — a map parameter plus a
private `buildRequest` helper leaves construction in the test, which is what the map column was
meant to remove. The "all defaults" row is `[:]`; a blank cell would skip the converter and pass
`null`.

**Why maps work better here:**
- Each scenario only specifies what's relevant
- Converter method provides sensible defaults
- No sea of blank cells
- Table focuses on what varies

## Decision Criteria

### Choose Maps When:

1. **Different scenarios need different properties**
   ```java
   @TableTest("""
       Scenario      | Config                                | Retries Used?
       Minimal       | [method: GET]                         | 1
       With timeout  | [method: POST, timeout: 5000]         | 1
       Full options  | [method: POST, timeout: 5000, retry: 3, auth: Bearer xyz] | 3
       """)
   void appliesRequestConfiguration(RequestConfig config, int retriesUsed) {   // @TypeConverter applies defaults
       assertEquals(retriesUsed, gateway.retriesFor(config));
   }
   ```

   **Why maps win:** Avoids a sea of blank cells. Converter method provides sensible defaults. Table focuses on what varies.

2. **Properties form one indivisible concept**
   ```java
   @TableTest("""
       Coordinates | Distance?
       (0,0)       | 0
       (3,4)       | 5
       """)
   void measuresDistanceFromOrigin(Point coords, int distance) { ... }
   ```

3. **The combined format is a domain standard**
   ```java
   @TableTest("""
       ISO Date   | Days Ago?
       2025-01-29 | 0
       2025-01-28 | 1
       """)
   void countsDaysSince(LocalDate date, int daysAgo) { ... }
   ```

4. **Need to pass the combined value directly to the system**
   ```java
   @TableTest("""
       Request Body           | Status?
       [user: alice, age: 30] | 200
       [user: bob]            | 400
       """)
   void validatesRequestBody(Map<String, String> body, int status) {
       assertEquals(status, api.validate(body));  // API accepts Map
   }
   ```

### Choose Separate Columns When:

1. **All scenarios use the same properties**
   ```java
   @TableTest("""
       Status | ms  | Expected?
       OK     | 10  | PASS
       ERROR  | 100 | FAIL
       """)
   ```

   **Why separate wins:** Every row has the same structure. Easy to scan vertically.

2. **Need to compare values vertically**
   ```java
   | Primary ms | Secondary ms |
   | 10         | 100          |  <- Easy to see Primary is faster
   | 100        | 10           |  <- Easy to see Secondary is faster
   ```

3. **Properties are independent inputs**
   ```java
   | Build Dir | JUnit Dir | Configured Dir |  // Three independent settings
   ```

## Iterative Column Evolution

Start simple and refine as you add scenarios:

### Phase 1: Initial Implementation
```java
@TableTest("""
    Scenario | Time
    Fast     | 10
    Slow     | 100
    """)
```

### Phase 2: Add Status
When you need error cases, you realize you need both status and time:

```java
// First attempt - combined
@TableTest("""
    Scenario | Response
    Fast     | OK in 10ms
    Error    | ERROR in 10ms
    """)
// Requires converter method with regex parsing
```

### Phase 3: Split for Clarity
Combined format is hard to read, split into columns:

```java
// Better - separate columns
@TableTest("""
    Scenario | Status | Time
    Fast     | OK     | 10
    Error    | ERROR  | 10
    """)
// No converter method needed, direct parameters
```

### Phase 4: Add Threshold Semantics
Response time is actually a threshold, not exact time:

```java
// Final - with domain convention
@TableTest("""
    Scenario | Status | Response Time?
    Fast     | OK     | <50
    Slow     | OK     | <150
    Error    | ERROR  | <50
    """)
// Converter method only for parseResponseTime to handle "<50"
```

**Key insight:** Don't try to design perfect columns upfront. Let the table structure emerge as you add scenarios.

## Guidelines for Column Design

### Start With Separate Columns
Default to one column per property. Only combine if there's a compelling reason.

### Look for Scanning Difficulty
If you find yourself squinting to parse values in a column, that's a sign to split it:
- Hard to scan: `OK in 100ms`, `ERROR in 10ms`
- Easy to scan: `OK` | `100` vs `ERROR` | `10`

### Consider Blank Cells
Blank cells are natural for "not provided":
```java
@TableTest("""
    Scenario        | Primary Status | Primary ms | Secondary Status | Secondary ms
    Primary only    | OK             | 10         |                  |
    Both            | OK             | 10         | OK               | 100
    """)
```

Clearer than combined format with special notation:
```java
@TableTest("""
    Scenario        | Primary Request | Secondary Request
    Primary only    | OK in 10ms      |
    Both            | OK in 10ms      | OK in 100ms
    """)
```

### Use Boxed Types for Nullable Values
When blank cells should be null, use boxed types:
```java
void recordsResponseTimes(String mdcStatus, Long mdcMs, String legacyStatus, Long legacyMs) {
    // Long allows null, primitive long does not
}
```

### A Regex in a Converter Means the Cell Holds Two Values

Converters own construction: any domain object built from a table value belongs in one, whatever the
construction idiom (main skill file, *Custom Type Converters*). What a converter should not be doing
is **unpicking a cell that carries more than one value**:

```java
// ✅ One value in a domain convention
@TypeConverter
public static Long parseResponseTime(String value) {
    if (value.startsWith("<")) return Long.valueOf(value.substring(1));
    return Long.parseLong(value);
}

// ✅ One value with several fields, from a map column
@TypeConverter
public static RequestConfig parseRequestConfig(Map<String, String> config) { ... }

// ❌ The regex is the tell — "OK in 10ms" is a status and a duration in one cell
@TypeConverter
public static Map<String, String> parseRequest(String value) {
    Pattern pattern = Pattern.compile("(OK|ERROR) in (\\d+)ms");
    Matcher matcher = pattern.matcher(value);
    // ... complex parsing logic ...
}
```

The repair is a column, not a simpler converter: `Status | ms`. **Converter complexity is a signal
about the cell — never a reason to move construction back into the test body**, which is where it
costs the reader most.

Where the cell genuinely holds a composite *value* rather than two, the notation already has a shape
for it and you should not invent one — main skill file, *Putting a Composite Value in a Cell*.

## Red Flags

These indicate you should split into separate columns:

1. **Regex parsing in converter methods** - If you're using regex to extract multiple values, split them into columns
2. **Multiple converter methods with same target type** - Two `@TypeConverter` methods returning the same *erased* type throw at runtime (every `Optional<T>` is `java.util.Optional`); give the class one converter per type, or split the columns
3. **Values don't align vertically** - Hard to scan means hard to read
4. **Different rows have different structures** - Leads to jagged tables and empty map keys

## Do Not Encode Two Values into One Cell

Encoding a pair into one cell — `ERROR+1`, `TIMEOUT+3`, `OK in 10ms` — reads as a way to cut
columns. It is not, and SKILL.md's *A compound result stays a collection* rules it out: a flattened
cell has to be parsed back in the method body, which tests your formatter rather than the rule and
puts the branching the table exists to remove back into the test.

Two cases and their repairs:

- **The pair is one value.** A responder plus its outcome is one enum — `Primary OK` in the cell, a
  `@TypeConverter` to `ServiceResponse`, one column. That is a domain type, not an encoding. See
  *Pattern: Consolidating Identity + Status* in `common-patterns.md`.
- **The pair is two values.** Two columns. Where one is blank for most rows, the answer is the map
  column (above), not a suffix — and where one column is constant down every row or moves only as a
  side effect of another, SKILL.md's *One rule per table* applies: give it rows that vary it, or move
  it to the table whose axis does.

## Column Naming Evolution

Column names should evolve as understanding grows. Don't expect perfect names on first implementation.

### Refinement Pattern

**After tests pass**, review and improve names with your pair:

```java
// ✗ First draft - implementation-focused names
@TableTest("""
    Scenario        | registered                  | expectedQueryCount | expected?
    Feature enabled | [feature-search-v2: true]   | 1                  | true
    """)
void findsFeatureToggle(Map<String, Boolean> registered, int expectedQueryCount, Optional<Boolean> expected)

// ✓ Refined - domain-focused names
@TableTest("""
    Scenario        | Feature Toggles             | Query Count?       | Result?
    Feature enabled | [feature-search-v2: true]   | 1                  | true
    """)
void findsFeatureToggle(Map<String, Boolean> toggles, int queryCount, Optional<Boolean> result)
```

### From Implementation to Domain

Replace technical/parameter names with domain terminology:

| Implementation Name    | Domain Name         | Why Better                           |
|------------------------|---------------------|--------------------------------------|
| `registered`           | `Feature Toggles`   | Describes what it is, not how stored |
| `expectedQueryCount`   | `Query Count?`      | Proper expectation suffix, concise   |
| `expected`             | `Result?`           | Clearer what's being checked         |
| `junitOutputDirOverride`| `JUnit Dir`        | Domain concept, not variable name    |
| `isPrimary`            | `Primary`           | Business terminology, not code name  |

### Progressive Refinement

**Phase 1**: Get tests working
- Use parameter names or simple labels
- Focus on structure and logic
- Names are "good enough" to understand intent

**Phase 2**: Refine for clarity (after tests pass)
- Replace implementation terms with domain language
- Add proper `?` suffixes to expectations
- Make names self-documenting
- No need to update parameter names - only column headers matter

### Example Evolution

```java
// Phase 1: Working test
@TableTest("""
    Scenario | input | maxMs  | result?
    Fast     | req1  | 50     | ok
    """)
void respondsWithinBudget(String input, Long maxMs, String result) { ... }

// Phase 2: Domain-refined names
@TableTest("""
    Scenario | Request | Response Time? | Status?
    Fast     | req1    | <50            | OK
    """)
void respondsWithinBudget(String input, Long maxMs, String result) { ... }  // Parameters unchanged
```

**Key insight**: Column headers are documentation. Parameters are just bindings. Improve the documentation after understanding emerges.

### Cross-Table Consistency

When multiple `@TableTest` methods exist in the same class, maintain consistent naming:

**Consistent notation:**
```java
// Table 1
@TableTest("""
    Scenario | Response Time?
    Fast     | <50
    """)

// Table 2 - uses same timing notation
@TableTest("""
    Scenario | Report Time?
    Fast     | <50
    """)

// Shared parser used by both
public static Long parseResponseTime(String value) { ... }
```

**Inconsistent (avoid):**
```java
// Table 1 uses < notation
| Response Time? |
| <50            |

// Table 2 uses exact values (different meaning!)
| Response Time? |
| 50             |  // Is this max or exact?
```

### Consistency Checklist

When you have multiple tables in a class:

- [ ] Similar concepts use same column names (`Response Time?` not sometimes `Timing?`)
- [ ] Same notation for similar values (`<50` not mixed with `50max`)
- [ ] Shared `@TypeConverter` methods/parsers (don't duplicate parsing logic)
- [ ] Same representation for errors (`ERROR`, not mixed with `FAIL` or `ERR`)
- [ ] Consistent special values (sentinel constants used the same way across all tables)
- [ ] Shared helper methods for setup and observation (don't duplicate `createResponder`, `QueryCounter`, etc.)

**Benefits:**
- Change once, affects all tables
- Easier to understand (patterns repeat)
- Fewer bugs (shared infrastructure tested once)
- Clear intent (consistency signals related concerns)

## Empty Cells for Optional Inputs

When an input is genuinely not present in certain scenarios (e.g., a peak surcharge on an off-peak ride), use blank cells to signal "absent" — do not fill with 0 or a default value. An input that *is* present but does not change the outcome is the other case, and takes a value set rather than a blank. This makes it immediately clear which inputs matter for each row.

```java
@TableTest("""
    Scenario              | Base fare | Peak surcharge | Airport fee | Total fare?
    Off-peak city ride    | 12.00     |                |             | 12.00
    Peak-hour city ride   | 12.00     | 5.00           |             | 17.00
    Airport pickup        | 12.00     |                | 8.00        | 20.00
    Peak airport pickup   | 12.00     | 5.00           | 8.00        | 25.00
    """)
void calculatesTotalFare(BigDecimal baseFare, BigDecimal peakSurcharge,
        BigDecimal airportFee, BigDecimal totalFare) {
    assertThat(FareCalculator.calculate(baseFare, peakSurcharge, airportFee))
            .isEqualTo(totalFare);
}
```

A blank cell arrives as `null` — **the converter is not consulted**, so it cannot supply the default.
That is the right shape when the system under test accepts an absent surcharge. When it needs a
value, write the empty string `''` instead of leaving the cell blank; that reaches the converter:

```java
@TypeConverter
public static BigDecimal toBigDecimal(String value) {
    return value.isBlank() ? BigDecimal.ZERO : new BigDecimal(value);
}
```

Use `Integer` (not `int`) for primitive parameters that may be blank — primitive types cannot represent null.

## When Reviewing Multiple Tables

- Look for similar columns with different notations (e.g., one table uses `<50`, another uses `50`)
- Extract common parsers and helpers where found
- Align assertion patterns (all upper-bound or all exact, not mixed)

## Summary

Choose based on your scenario:

### Use Separate Columns When:
- All scenarios use the same properties
- Values need to align vertically for comparison
- Properties are independent inputs
- Benefits: Easy to scan, clear relationships, minimal parsing

### Use Maps When:
- Different scenarios need different properties
- Many optional properties with sensible defaults
- Properties form one indivisible concept
- Need to pass combined value directly to system
- Benefits: Avoid blank cells, converter provides defaults, table focuses on variations

### Refactor Signals:
- **Combined → Separate**: Writing regex parsers, hard to scan, need multiple converter methods with same target type
- **Separate → Map**: Sea of blank cells, many optional columns, defaults scattered in test logic
