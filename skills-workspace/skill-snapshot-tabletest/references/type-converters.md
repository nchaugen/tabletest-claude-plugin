# Custom Type Converter Methods

## Prefer Built-in Conversion Before Writing a Converter

JUnit can convert strings to `Class<?>` when the value is a fully-qualified class name. Write `java.lang.RuntimeException` in the table instead of `RuntimeException` plus a custom `@TypeConverter`. This also works for collection elements: `[java.lang.RuntimeException, java.lang.NullPointerException]` → `List<Class<?>>`.

Only write a converter method when built-in conversion does not cover the type.

### Non-obvious built-in conversions

The common types (primitives, `String`, `Path`, `URI`, `URL`, `UUID`, `LocalDate`, `LocalTime`, `LocalDateTime`) are supported as expected. Less obvious conversions worth knowing:

| Table value | Parameter type | Notes |
|---|---|---|
| `"SECONDS"` | `TimeUnit` (any enum) | Enum name, case-sensitive |
| `"0xF"`, `"017"` | `int`/`long` (and boxed) | Hex and octal literals work |
| `"java.lang.Integer"` | `Class<?>` | Fully-qualified name required |
| `"java.lang.Thread$State"` | `Class<?>` | `$` for nested classes |
| `"byte"` | `Class<?>` | Primitive type names work |
| `"char[]"` | `Class<?>` | Array type names work |
| `"PT3S"`, `"PT1H30M"` | `Duration` | ISO 8601 duration format |
| `"P2M6D"` | `Period` | ISO 8601 period format |
| `"JPY"` | `Currency` | ISO 4217 currency code |
| `"en-US"` | `Locale` | IETF BCP 47 language tag |

**Enums**: Write the enum constant name — `"SECONDS"` not `"TimeUnit.SECONDS"`. The parameter type tells JUnit which enum to use.

```java
@TableTest("""
    Scenario       | Unit    | Duration?
    In seconds     | SECONDS | PT3S
    In minutes     | MINUTES | PT5M
    """)
void formats_duration(TimeUnit unit, Duration duration) { ... }
```

---

## Writing Custom Converter Methods

When JUnit's built-in converters don't support your parameter type, add custom type converter methods annotated with `@TypeConverter`.

## Java

Place custom converter methods as `@TypeConverter`-annotated `public static` methods in **a public test class** or a class listed in `@TypeConverterSources`.

**IMPORTANT:** The test class must be declared `public` for TableTest to discover converter methods:
```java
public class MyTest {  // Must be public, not package-private
    // Converter methods here will be found
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

@TypeConverter
public static LocalDate parseLocalDate(String input) {
    return switch (input) {
        case "today" -> LocalDate.now();
        case "tomorrow" -> LocalDate.now().plusDays(1);
        default -> LocalDate.parse(input);
    };
}
```

## Kotlin

For Kotlin tests, there are two ways to declare converter methods:

### Option 1: Package-Level Functions

Declare converter methods at package level in the same file as the test class:

```kotlin
// At package level (top of file, outside class)
@TypeConverter
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

Declare converter methods in the companion object with `@JvmStatic` and `@TypeConverter`:

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
        @TypeConverter
        fun parseLocalDate(input: String): LocalDate = when (input) {
            "today" -> LocalDate.now()
            "tomorrow" -> LocalDate.now().plusDays(1)
            else -> LocalDate.parse(input)
        }
    }
}
```

**Note**: `@Nested` inner classes in Kotlin cannot have companion objects. Use package-level functions or outer class companion object instead.

## Using @TypeConverterSources

For shared converter methods across multiple test classes:

**Java:**
```java
@TypeConverterSources(DateConverters.class)
class DateTest {
    @TableTest("""
        ...
        """)
    void testWithSharedConverters(LocalDate date, Duration duration) { ... }
}
```

**Kotlin** — use an `object` declaration with `@JvmStatic` and `@TypeConverter`:
```kotlin
object DateConverters {
    @JvmStatic
    @TypeConverter
    fun parseLocalDate(input: String): LocalDate = when (input) {
        "today" -> LocalDate.now()
        "tomorrow" -> LocalDate.now().plusDays(1)
        else -> LocalDate.parse(input)
    }
}

@TypeConverterSources(DateConverters::class)
class DateTest {
    @TableTest("""
        ...
        """)
    fun testWithSharedConverters(date: LocalDate, duration: Duration) { ... }
}
```

## Converter Method Requirements

A converter method will be used when it:
1. Is annotated with `@TypeConverter`
2. Is defined as a `public static` method in a `public class`
3. Accepts exactly one parameter
4. Returns an object of the target parameter type
5. **Is the only `@TypeConverter` method matching the above criteria in the class**

There is no specific naming pattern required, but `parse<TypeName>` (e.g., `parseLocalDate`, `parseMoney`) is conventional.

### One Converter Method Per Target Type

You cannot have multiple converter methods with the same target type (same return type), even with different names:

```java
// WRONG - TableTest cannot determine which to use
@TypeConverter
public static Map<String, String> parseRequest(String value) { ... }
@TypeConverter
public static Map<String, String> parseResponse(String value) { ... }

// CORRECT - Single converter handles both formats
@TypeConverter
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
// BEST - No converter method needed
@TableTest("""
    Request Status | Request ms | Response Status? | Response ms?
    OK             | 10         | OK               | <50
    """)
void test(String reqStatus, Integer reqMs, String respStatus, Long respMs) {
    // Use parseResponseTime converter for respMs to handle "<50" format
}

@TypeConverter
public static Long parseResponseTime(String value) {
    if (value.startsWith("<")) {
        return Long.valueOf(value.substring(1));
    }
    return Long.parseLong(value);
}
```

## Search Strategy

TableTest searches for converter methods in this order (stops at first match):

**Java:**
1. Current test class (including inherited methods)
2. Enclosing classes (for `@Nested` tests, starting with direct outer class)
3. Classes listed in `@TypeConverterSources` (in order listed)
4. `@TypeConverterSources` of enclosing classes (for `@Nested` tests)

**Kotlin:**
1. Current file (package-level functions and outer class companion object)
2. Classes listed in `@TypeConverterSources` (in order listed)
3. `@TypeConverterSources` of enclosing classes (for `@Nested` tests)

If a converter method isn't being found, check that it meets the requirements above and is in a location that matches the search order.

## Handling Null Values

When table cells can be blank (representing null), use appropriate return types:

### Primitive vs Boxed Types

```java
// WRONG - primitives cannot be null
@TableTest("""
    Value | Time
    OK    | 10
          |        <- Blank cell causes error: "null cannot be assigned to primitive type long"
    """)
void test(String value, long time) { ... }

// CORRECT - use boxed type
@TableTest("""
    Value | Time
    OK    | 10
          |        <- Blank cell becomes null
    """)
void test(String value, Long time) { ... }
```

### Converter Methods with Null Input

Converter methods receive `null` for blank cells. Handle this appropriately:

```java
// Return null for blank input
@TypeConverter
public static Long parseResponseTime(String value) {
    if (value == null || value.isBlank()) {
        return null;  // Blank cell -> null value
    }
    if (value.startsWith("<")) {
        return Long.valueOf(value.substring(1));
    }
    return Long.parseLong(value);
}

// Return empty collection for blank input
@TypeConverter
public static List<String> parseItems(String value) {
    if (value == null || value.isBlank()) {
        return List.of();  // Blank cell -> empty list
    }
    return Arrays.asList(value.split(","));
}
```

**Common pattern:** Blank cells in the table map to null/empty in the domain.

## Domain-Specific Formatting

Converter methods enable domain-specific formatting conventions that improve table readability:

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

@TypeConverter
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
- Converter handles the formatting detail
- Table is self-documenting without code comments

**Other examples:**
- Duration: `5m`, `30s` -> milliseconds
- Currency: `$100`, `EUR50` -> numeric values
- Percentage: `50%` -> 0.5
- File size: `10KB`, `5MB` -> bytes

## Converter Methods with Defaults

Converter methods can provide sensible defaults for missing map entries, making tables clearer when properties vary by scenario:

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

@TypeConverter
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
- Defaults are documented in one place (converter method)
- No sea of blank columns

**When to use:**
- Different scenarios need different subsets of properties
- Most properties have sensible default values
- Alternative is many blank cells in separate columns

**When not to use:**
- All scenarios use the same properties (use separate columns instead)
- No sensible defaults exist (every property is required)

## Common Wrapper Types

JUnit's built-in converters don't handle wrapper types like `Optional`, `Result`, or `Either`. Converter methods are needed for these common patterns.

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

@TypeConverter
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

@TypeConverter
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

@TypeConverter
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

This avoids the converter method entirely and makes the table clearer.

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

@TypeConverter
public static Optional<String> primary(String value) {
    return "empty".equals(value) ? Optional.empty() : Optional.of(value);
}

@TypeConverter
public static Optional<String> fallback(String value) {
    return "empty".equals(value) ? Optional.empty() : Optional.of(value);
}
```

**Important**: Column name matching works here. The converter method name should match the parameter name, and TableTest will use the appropriate converter for each column.
