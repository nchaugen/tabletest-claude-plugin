There's no existing source tree here. Here's the complete `TagFilterTest.java`:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter subject = new TagFilter();

    @DisplayName("Filter tags by category name")
    @Description("""
            "tech" maps to prefixes tech: and dev:; "business" maps to biz:.
            Null category returns all tags unfiltered.
            In the table, \\n represents a newline character within a tag's display text;
            the test method replaces this escape before invoking filterTags.
            Open: what should happen when both category and requiredCategories are non-null?
            """)
    @TableTest("""
            Scenario                          | Tags                                        | Category | Filtered Tags?
            Tech keeps tech: and dev: tags    | [tech:java, biz:sales, dev:ci]              | tech     | [tech:java, dev:ci]
            Business keeps only biz: tags     | [tech:java, biz:sales, dev:ci]              | business | [biz:sales]
            Null category returns all tags    | [tech:java, biz:sales, dev:ci]              |          | [tech:java, biz:sales, dev:ci]
            Empty list stays empty            | []                                          | tech     | []
            Pipe tag preserved, biz category  | ["biz:hr|recruiting", tech:go]              | business | ["biz:hr|recruiting"]
            Pipe tag excluded, tech category  | ["biz:hr|recruiting", tech:go]              | tech     | [tech:go]
            Newline in tag text preserved     | [tech:java\\nEnterprise Edition, biz:sales] | tech     | [tech:java\\nEnterprise Edition]
            """)
    void shouldFilterTagsByCategory(List<String> tags, String category, List<String> filteredTags) {
        List<String> processedTags = tags.stream()
                .map(t -> t.replace("\\n", "\n"))
                .collect(Collectors.toList());
        List<String> processedExpected = filteredTags.stream()
                .map(t -> t.replace("\\n", "\n"))
                .collect(Collectors.toList());
        assertEquals(processedExpected, subject.filterTags(processedTags, null, category));
    }

    @DisplayName("Filter tags by required categories set")
    @Description("""
            requiredCategories is a set of category-prefix names without the colon,
            e.g. {"tech", "dev"}. A tag qualifies when its prefix — the text before ':'
            — is in the set. Null requiredCategories means no category filter is applied.
            Open: what should an empty set {} produce?
            """)
    @TableTest("""
            Scenario                              | Tags                            | Required Categories | Filtered Tags?
            {tech,dev} keeps tech: and dev: tags  | [tech:java, biz:sales, dev:ci]  | {tech, dev}         | [tech:java, dev:ci]
            {biz} keeps only biz: tags            | [tech:java, biz:sales, dev:ci]  | {biz}               | [biz:sales]
            Null set returns all tags             | [tech:java, biz:sales, dev:ci]  |                     | [tech:java, biz:sales, dev:ci]
            Pipe tag preserved with {biz} filter  | ["biz:hr|recruiting", tech:go]  | {biz}               | ["biz:hr|recruiting"]
            Bracket tag preserved, no set filter  | ["[archived]:sales", tech:java] |                     | ["[archived]:sales", tech:java]
            Empty list stays empty                | []                              | {tech, dev}         | []
            """)
    void shouldFilterTagsByRequiredCategories(List<String> tags, Set<String> requiredCategories,
                                              List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, null));
    }
}
```

---

**Two tables, not one** — `category` and `requiredCategories` are separate filtering mechanisms with different rules, so each gets its own `@TableTest`. Each table calls the method with the other parameter fixed to `null` to test the concern in isolation.

**Key decisions:**

- **Pipes** — `"biz:hr|recruiting"` is quoted inside the list: `["biz:hr|recruiting", ...]`. The outer `[...]` is the list; the inner quotes protect the `|` from being parsed as a column separator.
- **Brackets** — `"[archived]:sales"` is quoted inside the list to prevent `[` from triggering nested-list syntax.
- **Newlines** — written as `\\n` in the text block (Java text blocks would turn a single `\n` into a real newline, splitting the row). The test method calls `.replace("\\n", "\n")` on both inputs and expected values before asserting.
- **Null inputs** — blank cells for `null` category and `null` requiredCategories.

**Two open questions captured in `@Description`:**
1. What happens when both `category` and `requiredCategories` are non-null simultaneously? (The requirements don't specify interaction.)
2. What should an empty `requiredCategories` set `{}` return?