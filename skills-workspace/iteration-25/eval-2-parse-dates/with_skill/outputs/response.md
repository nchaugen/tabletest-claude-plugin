No project files found — this is from natural-language requirements, so I'll skip the dependency check per the skill's Pre-Check guidance.

**Design decisions:**

- Cases 1–4 (ISO, slash, short year, null) all return a value → same assertion logic → one table
- Case 5 (empty string throws) → different assertion logic → separate table
- The non-ISO strings (`15/01/2024`, `24-01-15`) appear in the **`String` input** column — no type converter needed. The `Date?` column uses ISO values that JUnit converts automatically.

Here's the TableTest:

```java
class ParseDateTest {

    @TableTest("""
        Scenario     | Input      | Date?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void parsesDateString(String input, LocalDate expected) {
        assertEquals(expected, parseDate(input));
    }

    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | IllegalArgumentException
        """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parseDate(input));
    }
}
```

A few things to note:

- **Blank cells** for both the `Input` and `Date?` columns on the null row → `null` String in, `null` LocalDate expected.
- **`''`** in the Throws table → empty string (not null).
- The two tables split cleanly because the assertion logic differs — `assertEquals` vs `assertThrows`.
- The single-row `rejectsInvalidInput` table is intentional: it's easy to add more `Throws?` rows later (e.g. malformed strings like `not-a-date`).