# Custom Type Converters: Advanced Topics

Basics — writing `@TypeConverter` methods (Java and Kotlin), `@TypeConverterSources`, method requirements, null handling, and domain-specific formatting — are covered in the main skill file. This reference covers converter search order, defaults for map-based construction, and wrapper types.

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

If a converter method isn't being found, check that it meets the requirements in the main skill file and is in a location that matches the search order.

## Converter Methods with Defaults

Converter methods can provide sensible defaults for missing map entries, making tables clearer when properties vary by scenario:

```java
@TableTest("""
    Scenario          | Config                                  | Expected?
    All defaults      | [:]                                     | OK
    Basic request     | [method: GET]                           | OK
    With timeout      | [method: POST, timeout: 5000]           | OK
    Full config       | [method: POST, timeout: 5000, retry: 3] | OK
    """)
void testRequest(RequestConfig config, String expected) {
    assertEquals(expected, process(config));
}

@TypeConverter
public static RequestConfig buildRequestConfig(Map<String, String> config) {
    return new RequestConfig(
        config.getOrDefault("method", "GET"),           // Default: GET
        parseInt(config.getOrDefault("timeout", "3000")), // Default: 3000ms
        config.get("auth"),                             // Default: null (no auth)
        parseInt(config.getOrDefault("retry", "1")),     // Default: 1 retry
        parseBoolean(config.getOrDefault("cache", "false")) // Default: no cache
    );
}
```

The parameter is the **domain type**, so the converter runs and the method body stays
arrange-act-assert. The all-defaults row must be `[:]` — a blank cell short-circuits to `null`
without ever calling the converter.

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
