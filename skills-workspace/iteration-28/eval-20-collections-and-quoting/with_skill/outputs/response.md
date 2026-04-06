Working through the concerns before writing code:

**Concerns:**
1. *Filter by named category* — what "tech" and "business" select (including null passthrough)
2. *Filter by required categories set* — what explicit `requiredCategories` sets select (including null passthrough)

Each is independently testable; each becomes its own `@TableTest`. Edge cases (empty list, special chars, newlines) become rows inside whichever table exercises them most naturally.

**Open questions to note in `@Description`:**
- What happens when *both* `category` and `requiredCategories` are non-null simultaneously?
- What does an unrecognised `category` value (not "tech" or "business") return?
- Does an empty `requiredCategories` set `{}` block all tags, or behave like `null`?

**Quoting checks:**
- `biz:hr|recruiting` (contains `|`) → quoted inside list: `"biz:hr|recruiting"`
- `biz:sales[top]` (contains `]`) → quoted inside list to avoid list-close ambiguity: `"biz:sales[top]"`
- `tech:java\\nEnterprise Edition` → `\\n` in text block yields literal `\n` chars; replaced manually in test body per the skill's endorsed pattern

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTagsTest {

    private final YourClass subject = new YourClass(); // replace with actual class

    @DisplayName("Filter by named category")
    @Description("""
            requiredCategories is null for all rows — only the category parameter is exercised here.
            Open: what is the result when both category and requiredCategories are non-null?
            Open: what does an unrecognised category value (not "tech" or "business") produce?
            """)
    @TableTest("""
            Scenario                           | Tags                                           | Category | Filtered?
            Tech keeps tech: and dev:          | [tech:java, biz:sales, dev:ci]                | tech     | [tech:java, dev:ci]
            Business keeps biz: only          | [tech:java, biz:sales, dev:ci]                | business | [biz:sales]
            Null category passes all through  | [tech:java, biz:sales]                        |          | [tech:java, biz:sales]
            Empty input list                  | []                                            | tech     | []
            Pipe tag preserved                | ["biz:hr|recruiting", tech:go]                | business | ["biz:hr|recruiting"]
            Newline in display text preserved | ["tech:java\\nEnterprise Edition", biz:sales] | tech     | ["tech:java\\nEnterprise Edition"]
            """)
    void filtersByNamedCategory(List<String> tags, String category, List<String> filtered) {
        List<String> input = tags.stream().map(t -> t.replace("\\n", "\n")).toList();
        List<String> expected = filtered.stream().map(t -> t.replace("\\n", "\n")).toList();
        assertEquals(expected, subject.filterTags(input, null, category));
    }

    @DisplayName("Filter by required categories set")
    @Description("""
            category is null for all rows — only the requiredCategories parameter is exercised here.
            Open: does an empty set {} block all tags, or behave like null (pass all through)?
            """)
    @TableTest("""
            Scenario                     | Tags                                | Required categories | Filtered?
            Tech and dev required        | [tech:java, biz:sales, dev:ci]     | {tech, dev}         | [tech:java, dev:ci]
            Biz only required            | [tech:java, biz:sales, dev:ci]     | {biz}               | [biz:sales]
            Null set passes all through  | [tech:java, biz:sales]             |                     | [tech:java, biz:sales]
            Empty input list             | []                                 | {biz}               | []
            Brackets in tag preserved    | ["biz:sales[top]", tech:java]      | {biz}               | ["biz:sales[top]"]
            """)
    void filtersByRequiredCategories(List<String> tags, Set<String> requiredCategories, List<String> filtered) {
        assertEquals(filtered, subject.filterTags(tags, requiredCategories, null));
    }
}
```

A few things to note when you wire this up:

- **`YourClass`** → replace with the actual class containing `filterTags`.
- **Newline row**: `\\n` in the text block reaches TableTest as literal `\n` (two chars). The `replace("\\n", "\n")` in the method body converts to a real newline before calling the method — same for both input and expected sides.
- **`"biz:sales[top]"`** in the brackets row: the element is quoted because the `]` would otherwise look like the end of the outer list to the parser.
- The three open questions in the `@Description` annotations should be resolved with whoever owns the spec before relying on the corresponding rows.