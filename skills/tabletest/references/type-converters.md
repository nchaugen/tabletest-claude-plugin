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
    Scenario          | Config                                  | Timeout Used? | Retries Used?
    All defaults      | [:]                                     | 3000          | 1
    Basic request     | [method: GET]                           | 3000          | 1
    With timeout      | [method: POST, timeout: 5000]           | 5000          | 1
    Full config       | [method: POST, timeout: 5000, retry: 3] | 5000          | 3
    """)
void appliesConfiguredDefaults(RequestConfig config, int timeoutUsed, int retriesUsed) {
    assertEquals(timeoutUsed, gateway.timeoutFor(config));
    assertEquals(retriesUsed, gateway.retriesFor(config));
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
void findsRegisteredToggle(Map<String, Boolean> toggles, Optional<Boolean> result) {
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
void looksUpEntryByName(String input, Optional<String> result) {
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
void validatesNumericInput(String input, Result<Integer, String> result) {
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
void validatesNumericInput(String input, Integer success, String error) {
    Result<Integer, String> actual = validator.validate(input);

    assertEquals(success, actual.valueOrNull());
    assertEquals(error, actual.errorOrNull());
}
```

**Assert the parts, do not rebuild the whole** — reconstructing a `Result` from the two columns needs
a branch in the body, and the blank cell already says which half applies.

There is nothing to convert here because the columns *are* the result's parts, compared against the
parts the system returns. That is a different case from an **input** built out of a cell, which
always belongs in a converter — see *Putting a Composite Value in a Cell* in the main skill file.

### Multiple Optional Parameters

Several parameters of the same wrapper type share **one** converter. Converter selection is by
return type alone — the method name plays no part, and neither does the column or parameter it
feeds:

```java
@TableTest("""
    Scenario          | Primary | Fallback | Resolved?
    Both present      | primary | fallback | primary
    Primary only      | primary | empty    | primary
    Fallback only     | empty   | fallback | fallback
    Neither present   | empty   | empty    | empty
    """)
void resolvesPrimaryBeforeFallback(Optional<String> primary, Optional<String> fallback, Optional<String> resolved) {
    assertEquals(resolved, resolver.resolve(primary, fallback));
}

@TypeConverter
public static Optional<String> toOptional(String value) {
    return "empty".equals(value) ? Optional.empty() : Optional.of(value);
}
```

**A second `@TypeConverter` returning the same type is a runtime error, not a second choice.** Give
this class a `primary()` and a `fallback()` converter both returning `Optional<String>` and every row
fails with:

```
TableTestException: Multiple type converters found for type java.util.Optional in class …
```

**The match is on the erased type**, so it is stronger than it looks: `Optional<String>` and
`Optional<Boolean>` are both `java.util.Optional` and collide with each other too. One converter per
class per wrapper type.

When two columns of the same type genuinely need different parsing, the fix is not two converters —
it is one converter that accepts both spellings, or two distinct parameter types (see *One Converter
Per Target Type* in the main skill file).

## An Expected Value the Table Cannot Show

An expectation that is encoding-specific — an ANSI escape, Base64, raw bytes — destroys the table if
you put it in a cell. Give the column a **type** whose constants carry the raw value. The table then
names the constant and nothing is translated in the test body:

```java
@TableTest("""
    Scenario      | Input     | Colour?
    XML tag       | <root>    | CYAN
    XML attribute | id="x"    | GREEN
    XML value     | some text | YELLOW
    """)
void coloursEachXmlTokenType(String input, AnsiColour colour) {
    assertTrue(colouriser.colourise(input).contains(colour.escape()));
}

enum AnsiColour {
    CYAN("\u001B[36m"), GREEN("\u001B[32m"), YELLOW("\u001B[33m");

    private final String escape;

    AnsiColour(String escape) { this.escape = escape; }

    String escape() { return escape; }
}
```

JUnit converts an enum constant by name, so this needs no `@TypeConverter` at all.

**Do not translate in the method body.** `String expected = COLOURS.get(colourName)` puts the mapping
where no reader of the published table can follow it — the cell then names something only the test
code can resolve, which *Use Concrete Domain Values* rules out.

**A `String` column with a converter does not work either.** Converters are chosen by parameter type,
so a `String → String` converter would rewrite the `Input` column too (main skill file,
*Domain-Specific Formatting*). The distinct type is what confines the conversion to one column.
