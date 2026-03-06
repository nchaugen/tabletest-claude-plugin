# Column Design Best Practices

How you structure columns significantly impacts table readability. This guide covers when to split, when to combine, and how to evolve column design iteratively.

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
    """)
void test(String primaryRequest, String secondaryRequest, String response) {
    // Need converter methods to parse each combined string
}

// ✅ EASIER TO READ - separate columns when structure is consistent
@TableTest("""
    Scenario          | Primary | Primary ms | Secondary | Secondary ms | Response? | Response ms?
    Both ok           | OK      | 100        | OK        | 10           | OK        | <50
    Primary fails     | ERROR   | 100        | OK        | 10           | OK        | <50
    """)
void test(String primaryStatus, Long primaryMs, String secondaryStatus, Long secondaryMs,
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
    Scenario          | Method | Timeout | Auth        | Retry | Cache | Expected?
    Basic request     | GET    |         |             |       |       | OK
    With timeout      | POST   | 5000    |             |       |       | OK
    With auth         | GET    |         | Bearer xyz  |       |       | OK
    Full config       | POST   | 5000    | Bearer xyz  | 3     | true  | OK
    """)
void test(String method, Integer timeout, String auth, Integer retry, Boolean cache, String expected) {
    // Need to handle nulls and provide defaults in test method
}

// ✅ EASIER TO READ - map with defaults when properties vary
@TableTest("""
    Scenario          | Request Config                                  | Expected?
    Basic request     | [method: GET]                                   | OK
    With timeout      | [method: POST, timeout: 5000]                   | OK
    With auth         | [method: GET, auth: Bearer xyz]                 | OK
    Full config       | [method: POST, timeout: 5000, auth: Bearer xyz, retry: 3, cache: true] | OK
    """)
void test(Map<String, String> config, String expected) {
    RequestConfig request = buildRequest(config);  // Applies defaults
    assertEquals(expected, process(request));
}

private RequestConfig buildRequest(Map<String, String> config) {
    return new RequestConfig(
        config.getOrDefault("method", "GET"),
        parseInt(config.getOrDefault("timeout", "3000")),
        config.get("auth"),
        parseInt(config.getOrDefault("retry", "1")),
        parseBoolean(config.getOrDefault("cache", "false"))
    );
}
```

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
       Scenario      | Config                                | Expected?
       Minimal       | [method: GET]                         | OK
       With timeout  | [method: POST, timeout: 5000]         | OK
       Full options  | [method: POST, timeout: 5000, retry: 3, auth: Bearer xyz] | OK
       """)
   void test(Map<String, String> config, String expected) {
       RequestConfig req = buildRequest(config);  // Provides defaults
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
   void testDistance(Point coords, int distance) { ... }
   ```

3. **The combined format is a domain standard**
   ```java
   @TableTest("""
       ISO Date   | Days Ago?
       2025-01-29 | 0
       2025-01-28 | 1
       """)
   void testDaysAgo(LocalDate date, int daysAgo) { ... }
   ```

4. **Need to pass the combined value directly to the system**
   ```java
   @TableTest("""
       Request Body           | Status?
       [user: alice, age: 30] | 200
       [user: bob]            | 400
       """)
   void testValidation(Map<String, String> body, int status) {
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
void test(String mdcStatus, Long mdcMs, String legacyStatus, Long legacyMs) {
    // Long allows null, primitive long does not
}
```

### Converter Methods for Formatting Only
Use `@TypeConverter` methods to handle domain-specific formatting conventions (like `<50` for thresholds), not to parse complex combined strings:

```java
// ✅ Good - handles formatting convention
@TypeConverter
public static Long parseResponseTime(String value) {
    if (value.startsWith("<")) return Long.valueOf(value.substring(1));
    return Long.parseLong(value);
}

// ❌ Bad - parsing complex format suggests wrong column design
@TypeConverter
public static Map<String, String> parseRequest(String value) {
    Pattern pattern = Pattern.compile("(OK|ERROR) in (\\d+)ms");
    Matcher matcher = pattern.matcher(value);
    // ... complex parsing logic ...
    // This complexity suggests you should split into separate columns instead
}
```

## Red Flags

These indicate you should split into separate columns:

1. **Regex parsing in converter methods** - If you're using regex to extract multiple values, split them into columns
2. **Multiple converter methods with same target type** - Can't have two `@TypeConverter` methods returning the same type; split the columns instead
3. **Values don't align vertically** - Hard to scan means hard to read
4. **Different rows have different structures** - Leads to jagged tables and empty map keys

## Encoding Related Values in Single Column

When multiple values have a cause-effect relationship, consider encoding them together rather than using separate columns.

### Example: Error Status with Suppressed Exception Count

```java
// ❌ Separate columns - but they're coupled
@TableTest("""
    Scenario              | Response? | Suppressed?
    Success               | OK        | 0
    Single failure        | ERROR     | 0
    Both fail (fallback)  | ERROR     | 1
    """)
void test(String response, int suppressedCount) {
    // suppressedCount only matters when response is ERROR
}

// ✅ Encoded - shows relationship
@TableTest("""
    Scenario              | Response?
    Success               | OK
    Single failure        | ERROR
    Both fail (fallback)  | ERROR+1
    """)
void test(String response) {
    if ("OK".equals(response)) {
        assertEquals(expectedResponder, router.invoke());
    } else {
        RuntimeException thrown = assertThrows(RuntimeException.class, router::invoke);
        assertEquals(getExpectedSuppressedCount(response), thrown.getSuppressed().length);
    }
}

private static int getExpectedSuppressedCount(String response) {
    return response != null && response.startsWith("ERROR+")
        ? Integer.parseInt(response.substring(6))
        : 0;
}
```

### When to Encode Together

Encode values in one column when:

1. **Cause-effect relationship** - One value causes the other (errors cause suppressed exceptions)
2. **Conditional relevance** - Second value only matters when first value has specific state
3. **Never independent** - They always appear together in specific combinations
4. **Reduces column count** - Fewer columns make table easier to scan

### When to Keep Separate

Keep separate columns when:

1. **Independent variation** - Values can vary independently across scenarios
2. **Need vertical scanning** - Comparing values vertically is important
3. **Different types** - Different data types that don't naturally combine
4. **Both always present** - No conditional relationship

### Pattern: Optional Qualifiers

Use `+` suffix for optional qualifiers that only apply in certain cases:

```java
| Response? |
| OK        |      // Success, no qualifier needed
| ERROR     |      // Error without qualifier
| ERROR+1   |      // Error with qualifier (1 suppressed exception)
| TIMEOUT+5 |      // Timeout after 5 retries
```

The base value (`ERROR`, `TIMEOUT`) works standalone, qualifier is optional context.

### Benefits of Encoding

1. **Fewer columns** - Table is narrower and easier to scan
2. **Shows relationship** - Reader immediately sees values are coupled
3. **Natural parsing** - Parse only when needed (in error branch)
4. **Documents constraints** - `ERROR+1` documents "suppressed only happens with errors"

## Removing Redundant Columns

After adding a column, ask: "Can this be encoded in an existing column?"

### Evolution Example: From Separate to Encoded

**Phase 1: Add new column**
```java
@TableTest("""
    Scenario          | Response? | Suppressed?
    Both routes ok    | OK        | 0
    Primary fails     | ERROR     | 0
    Both fail         | ERROR     | 1
    """)
```

**Phase 2: Notice correlation**
- Suppressed is always `0` for OK responses
- Suppressed can be `0` or `1` for ERROR responses
- The column only adds information for ERROR cases

**Phase 3: Encode together**
```java
@TableTest("""
    Scenario          | Response?
    Both routes ok    | OK
    Primary fails     | ERROR
    Both fail         | ERROR+1
    """)
```

### Red Flags for Redundant Columns

Watch for these patterns that suggest encoding opportunity:

1. **Perfect correlation** - Column B is always same value when Column A has specific value
   ```java
   // Suppressed is ALWAYS 0 when Response is OK
   | Response? | Suppressed? |
   | OK        | 0           |
   | OK        | 0           |
   | OK        | 0           |
   ```

2. **Conditional zero** - Column is always `0`/`null`/empty for certain scenarios
   ```java
   // Retry count only matters for timeout scenarios
   | Result?  | Retries? |
   | SUCCESS  | 0        |
   | SUCCESS  | 0        |
   | TIMEOUT  | 3        |
   ```

   Better: `TIMEOUT+3`

3. **Single-value columns** - Column has same value in every row except a few
   ```java
   // Cache hit only true in 2 of 10 rows
   | Scenario           | Cache Hit? |
   | Query user         | false      |
   | Query product      | false      |
   | Query user again   | true       |  <- Only 2 rows differ
   | Query product again| true       |
   | ...                | false      |
   ```

### Refactoring Process

1. **Identify correlation** - Find columns where one column's value depends on another
2. **Check for cause-effect** - Is there a cause-effect relationship?
3. **Try encoding** - Combine using `+`, `-`, or other natural delimiter
4. **Move parsing to branch** - Parse only where the qualifier matters
5. **Verify readability** - Is encoded version clearer? If not, keep separate

### Counter-Example: When NOT to Encode

Don't encode when values vary independently:

```java
// ❌ Bad - values vary independently
@TableTest("""
    Scenario         | Request?
    Fast success     | OK/10
    Slow success     | OK/100
    Fast error       | ERROR/10
    Slow error       | ERROR/100
    """)

// ✅ Good - separate columns for independent concerns
@TableTest("""
    Scenario         | Status? | Duration?
    Fast success     | OK      | 10
    Slow success     | OK      | 100
    Fast error       | ERROR   | 10
    Slow error       | ERROR   | 100
    """)
```

Status and duration can vary independently - no cause-effect relationship.

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
void test(Map<String, Boolean> registered, int expectedQueryCount, Optional<Boolean> expected)

// ✓ Refined - domain-focused names
@TableTest("""
    Scenario        | Feature Toggles             | Query Count?       | Result?
    Feature enabled | [feature-search-v2: true]   | 1                  | true
    """)
void test(Map<String, Boolean> toggles, int queryCount, Optional<Boolean> result)
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
void test(String input, Long maxMs, String result) { ... }

// Phase 2: Domain-refined names
@TableTest("""
    Scenario | Request | Response Time? | Status?
    Fast     | req1    | <50            | OK
    """)
void test(String input, Long maxMs, String result) { ... }  // Parameters unchanged
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

### Use Encoded Values When:
- Cause-effect relationship between values
- One value only relevant in specific cases of another
- Reduces redundant columns
- Benefits: Fewer columns, shows relationship, documents constraints

### Refactor Signals:
- **Combined → Separate**: Writing regex parsers, hard to scan, need multiple converter methods with same target type
- **Separate → Map**: Sea of blank cells, many optional columns, defaults scattered in test logic
- **Separate → Encoded**: Perfect correlation, one column only adds info for specific values in another column
