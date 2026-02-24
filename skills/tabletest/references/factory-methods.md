# Factory Methods for Custom Type Conversion

When JUnit's built-in converters don't support your parameter type, add factory methods.

## Java

Place factory methods as `public static` methods in **a public test class** or a class listed in `@FactorySources`.

**IMPORTANT:** The test class must be declared `public` for TableTest to discover factory methods:
```java
@ExtendWith(MockitoExtension.class)
public class MyTest {  // Must be public, not package-private
    // Factory methods here will be found
}
```

```java
@TableTest("""
    Date       | Days Until?
    today      | 0
    tomorrow   | 1
    """)
void testDaysUntil(LocalDate date, int expected) {
    assertEquals(expected, ChronoUnit.DAYS.between(LocalDate.now(), date));
}

public static LocalDate parseLocalDate(String input) {
    return switch (input) {
        case "today" -> LocalDate.now();
        case "tomorrow" -> LocalDate.now().plusDays(1);
        default -> LocalDate.parse(input);
    };
}
```

## Kotlin

For Kotlin tests, there are two ways to declare factory methods:

### Option 1: Package-Level Functions

Declare factory methods at package level in the same file as the test class:

```kotlin
// At package level (top of file, outside class)
fun parseLocalDate(input: String): LocalDate = when (input) {
    "today" -> LocalDate.now()
    "tomorrow" -> LocalDate.now().plusDays(1)
    else -> LocalDate.parse(input)
}

class DateTest {
    @TableTest("""
        Date       | Days Until?
        today      | 0
        tomorrow   | 1
        """)
    fun testDaysUntil(date: LocalDate, expected: Int) {
        assertEquals(expected, ChronoUnit.DAYS.between(LocalDate.now(), date))
    }
}
```

### Option 2: Companion Object with @JvmStatic

Declare factory methods in the companion object with `@JvmStatic`:

```kotlin
class DateTest {
    @TableTest("""
        Date       | Days Until?
        today      | 0
        tomorrow   | 1
        """)
    fun testDaysUntil(date: LocalDate, expected: Int) {
        assertEquals(expected, ChronoUnit.DAYS.between(LocalDate.now(), date))
    }

    companion object {
        @JvmStatic
        fun parseLocalDate(input: String): LocalDate = when (input) {
            "today" -> LocalDate.now()
            "tomorrow" -> LocalDate.now().plusDays(1)
            else -> LocalDate.parse(input)
        }
    }
}
```

**Note**: `@Nested` inner classes in Kotlin cannot have companion objects. Use package-level functions or outer class companion object instead.

## Using @FactorySources

For shared factory methods across multiple test classes:

**Java:**
```java
@FactorySources(DateFactories.class)
class DateTest {
    @TableTest("""
        ...
        """)
    void testWithSharedFactories(LocalDate date, Duration duration) { ... }
}
```

**Kotlin** — use an `object` declaration with `@JvmStatic`:
```kotlin
object DateFactories {
    @JvmStatic
    fun parseLocalDate(input: String): LocalDate = when (input) {
        "today" -> LocalDate.now()
        "tomorrow" -> LocalDate.now().plusDays(1)
        else -> LocalDate.parse(input)
    }
}

@FactorySources(DateFactories::class)
class DateTest {
    @TableTest("""
        ...
        """)
    fun testWithSharedFactories(date: LocalDate, duration: Duration) { ... }
}
```

## Factory Method Requirements

A factory method will be used when it:
1. Is defined as a public static method in a public class
2. Accepts exactly one parameter
3. Returns an object of the target parameter type
4. **Is the only method matching the above criteria in the class**

There is no specific naming pattern required, but `parse<TypeName>` (e.g., `parseLocalDate`, `parseMoney`) is conventional.

### One Factory Method Per Signature

You cannot have multiple factory methods with the same signature (same input and return types), even with different names:

```java
// ❌ WRONG - TableTest cannot determine which to use
public static Map<String, String> parseRequest(String value) { ... }
public static Map<String, String> parseResponse(String value) { ... }

// ✅ CORRECT - Single factory handles both formats
public static Map<String, String> parseRequestOrResponse(String value) {
    if (value.startsWith("<")) {
        // Handle response format
    } else {
        // Handle request format
    }
    return ...;
}
```

**Even better - split into separate columns instead:**
```java
// ✅ BEST - No factory method needed
@TableTest("""
    Request Status | Request ms | Response Status? | Response ms?
    OK             | 10         | OK               | <50
    """)
void test(String reqStatus, Integer reqMs, String respStatus, Long respMs) {
    // Use parseResponseTime factory for respMs to handle "<50" format
}

public static Long parseResponseTime(String value) {
    if (value.startsWith("<")) {
        return Long.valueOf(value.substring(1));
    }
    return Long.parseLong(value);
}
```

## Search Strategy

TableTest searches for factory methods in this order (stops at first match):

**Java:**
1. Current test class (including inherited methods)
2. Enclosing classes (for `@Nested` tests, starting with direct outer class)
3. Classes listed in `@FactorySources` (in order listed)
4. `@FactorySources` of enclosing classes (for `@Nested` tests)

**Kotlin:**
1. Current file (package-level functions and outer class companion object)
2. Classes listed in `@FactorySources` (in order listed)
3. `@FactorySources` of enclosing classes (for `@Nested` tests)

If a factory method isn't being found, check that it meets the requirements above and is in a location that matches the search order.

## Handling Null Values

When table cells can be blank (representing null), use appropriate return types:

### Primitive vs Boxed Types

```java
// ❌ WRONG - primitives cannot be null
@TableTest("""
    Value | Time
    OK    | 10
          |        <- Blank cell causes error: "null cannot be assigned to primitive type long"
    """)
void test(String value, long time) { ... }

// ✅ CORRECT - use boxed type
@TableTest("""
    Value | Time
    OK    | 10
          |        <- Blank cell becomes null
    """)
void test(String value, Long time) { ... }
```

### Factory Methods with Null Input

Factory methods receive `null` for blank cells. Handle this appropriately:

```java
// Return null for blank input
public static Long parseResponseTime(String value) {
    if (value == null || value.isBlank()) {
        return null;  // Blank cell → null value
    }
    if (value.startsWith("<")) {
        return Long.valueOf(value.substring(1));
    }
    return Long.parseLong(value);
}

// Return empty collection for blank input
public static List<String> parseItems(String value) {
    if (value == null || value.isBlank()) {
        return List.of();  // Blank cell → empty list
    }
    return Arrays.asList(value.split(","));
}
```

**Common pattern:** Blank cells in the table map to null/empty in the domain.

## Domain-Specific Formatting

Factory methods enable domain-specific formatting conventions that improve table readability:

```java
@TableTest("""
    Scenario     | Response Time?
    Fast         | <50
    Acceptable   | <150
    Slow         | <500
    """)
void testResponseTime(Long maxResponseTimeMs) {
    // maxResponseTimeMs is 50, 150, 500 respectively
}

public static Long parseResponseTime(String value) {
    if (value == null || value.isBlank()) {
        return null;
    }
    // Handle "less than" notation
    if (value.startsWith("<")) {
        return Long.valueOf(value.substring(1));
    }
    return Long.parseLong(value);
}
```

**Benefits:**
- `<50` clearly communicates "less than 50ms" in the table
- Parser handles the formatting detail
- Table is self-documenting without code comments

**Other examples:**
- Duration: `5m`, `30s` → milliseconds
- Currency: `$100`, `€50` → numeric values
- Percentage: `50%` → 0.5
- File size: `10KB`, `5MB` → bytes

## Factory Methods with Defaults

Factory methods can provide sensible defaults for missing map entries, making tables clearer when properties vary by scenario:

```java
@TableTest("""
    Scenario          | Config                                  | Expected?
    Basic request     | [method: GET]                           | OK
    With timeout      | [method: POST, timeout: 5000]           | OK
    Full config       | [method: POST, timeout: 5000, retry: 3] | OK
    """)
void testRequest(Map<String, String> config, String expected) {
    RequestConfig request = buildRequestConfig(config);
    assertEquals(expected, process(request));
}

public static RequestConfig buildRequestConfig(Map<String, String> config) {
    if (config == null || config.isEmpty()) {
        return RequestConfig.defaults();
    }
    return new RequestConfig(
        config.getOrDefault("method", "GET"),           // Default: GET
        parseInt(config.getOrDefault("timeout", "3000")), // Default: 3000ms
        config.get("auth"),                             // Default: null (no auth)
        parseInt(config.getOrDefault("retry", "1")),     // Default: 1 retry
        parseBoolean(config.getOrDefault("cache", "false")) // Default: no cache
    );
}
```

**Benefits:**
- Each scenario only specifies what varies from defaults
- Table focuses on the interesting variations
- Defaults are documented in one place (factory method)
- No sea of blank columns

**When to use:**
- Different scenarios need different subsets of properties
- Most properties have sensible default values
- Alternative is many blank cells in separate columns

**When not to use:**
- All scenarios use the same properties (use separate columns instead)
- No sensible defaults exist (every property is required)

## Common Wrapper Types

JUnit's built-in converters don't handle wrapper types like `Optional`, `Result`, or `Either`. Factory methods are needed for these common patterns.

### Optional<T>

```java
@TableTest("""
    Scenario        | Feature Toggles      | Result?
    Feature enabled | [feature-x: true]    | true
    Not registered  | [:]                  | empty
    Feature disabled| [feature-x: false]   | false
    """)
void testFeatureToggle(Map<String, Boolean> toggles, Optional<Boolean> result) {
    assertEquals(result, featureResolver.find("feature-x", toggles));
}

public static Optional<Boolean> result(String value) {
    return switch (value) {
        case "empty" -> Optional.empty();
        case "true" -> Optional.of(true);
        case "false" -> Optional.of(false);
        default -> throw new IllegalArgumentException("Unknown result: " + value);
    };
}
```

**Pattern**: Use simple string representations (`empty`, `true`, `false`) rather than trying to parse `Optional.empty()` syntax.

### Optional<String>

```java
@TableTest("""
    Scenario     | Input  | Result?
    Found        | active | active
    Not found    | unused | empty
    """)
void testLookup(String input, Optional<String> result) {
    assertEquals(result, lookup(input));
}

public static Optional<String> result(String value) {
    return "empty".equals(value) ? Optional.empty() : Optional.of(value);
}
```

### Result<T, E> (or Either<L, R>)

For Result/Either types, use string notation to indicate success vs failure:

```java
@TableTest("""
    Scenario       | Input | Result?
    Valid input    | 42    | OK
    Invalid input  | abc   | ERROR: Not a number
    Out of range   | -1    | ERROR: Must be positive
    """)
void testValidation(String input, Result<Integer, String> result) {
    assertEquals(result, validator.validate(input));
}

public static Result<Integer, String> result(String value) {
    if (value.equals("OK")) {
        return Result.ok(42);  // Use value from Input column
    }
    if (value.startsWith("ERROR: ")) {
        return Result.error(value.substring(7));
    }
    throw new IllegalArgumentException("Unknown result: " + value);
}
```

**Pattern**: Prefix error cases with `ERROR:` to distinguish from success cases.

**Better approach**: Use separate columns for success value and error message:

```java
@TableTest("""
    Scenario       | Input | Success? | Error?
    Valid input    | 42    | 42       |
    Invalid input  | abc   |          | Not a number
    Out of range   | -1    |          | Must be positive
    """)
void testValidation(String input, Integer success, String error) {
    Result<Integer, String> expected = success != null
        ? Result.ok(success)
        : Result.error(error);
    assertEquals(expected, validator.validate(input));
}
```

This avoids the factory method entirely and makes the table clearer.

### Multiple Optional Parameters

When you have multiple `Optional` parameters in the same test, use different column naming:

```java
@TableTest("""
    Scenario          | Primary?  | Fallback?
    Both present      | primary   | fallback
    Primary only      | primary   | empty
    Fallback only     | empty     | fallback
    Neither present   | empty     | empty
    """)
void testOptionals(Optional<String> primary, Optional<String> fallback) {
    // Test with both optionals
}

public static Optional<String> primary(String value) {
    return "empty".equals(value) ? Optional.empty() : Optional.of(value);
}

public static Optional<String> fallback(String value) {
    return "empty".equals(value) ? Optional.empty() : Optional.of(value);
}
```

**Important**: Column name matching works here. The factory method name should match the parameter name, and TableTest will use the appropriate factory for each column.
