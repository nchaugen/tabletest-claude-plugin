No `pom.xml` found. I'll note the missing dependency and write the test. The design consideration here: the empty-string row throws while all others return a value — the skill requires uniform assertions per table, so these split into two tables.

The input column is `String` (no conversion needed), and the result column only uses ISO dates (`2024-01-15`) which JUnit auto-converts — no `@TypeConverter` required.

Here's the TableTest:

```java
public class DateParserTest {

    private final DateParser dateParser = new DateParser();

    @TableTest("""
        Scenario      | Input       | Result?
        ISO format    | 2024-01-15  | 2024-01-15
        Slash format  | 15/01/2024  | 2024-01-15
        Short year    | 24-01-15    | 2024-01-15
        Null input    |             |
        """)
    @Description("Parses multiple date string formats; null input returns null")
    void parsesValidInputs(String input, LocalDate expected) {
        assertEquals(expected, dateParser.parseDate(input));
    }

    @TableTest("""
        Scenario      | Input
        Empty string  | ''
        """)
    @Description("Empty string is rejected as malformed input — distinct from null, which means 'not provided'")
    void throwsOnEmptyInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> dateParser.parseDate(input));
    }
}
```

A few things to note:

**Why two tables**: The empty-string row throws while the others return a value. The skill requires all rows in a table to share the same assertion logic, so the exception case lives in its own table. This also documents intent clearly — `null` and `""` have distinct contracts.

**No `@TypeConverter` needed**: The `Input` column is `String` — non-ISO values like `15/01/2024` pass through as raw strings. The `Result?` column only contains `2024-01-15` (ISO) or blank (null), both of which JUnit converts natively.

**Dependency**: If `org.tabletest:tabletest-junit` isn't in your `pom.xml`/`build.gradle` yet, check `skills/tabletest/references/dependency-setup.md` for the exact coordinates — the groupId/artifactId are easy to get wrong from memory.