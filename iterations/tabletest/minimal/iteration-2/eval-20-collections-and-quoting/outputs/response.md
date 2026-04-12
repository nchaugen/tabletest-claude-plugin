Working through the requirements-to-tables design steps before writing the code.

**Step 1 — Concerns:**
1. **Filter by category keyword** — "tech"/"business" string maps to allowed prefixes
2. **Filter by required categories set** — `Set<String>` filters by category prefix
3. **Special characters preserved** — tags with `|`, `[]`, `\n` pass through unchanged

**Step 2-4 — Variations & value sets:**
- Category: tech (→ tech:/dev:), business (→ biz:), null (→ all)
- RequiredCategories: {tech,dev}, {biz}, null (→ all)
- Empty list in category table (covers empty input)
- Special chars table uses \\n per the skill's newlines-in-values guidance

**Step 8 — Open questions:**
- How do `category` and `requiredCategories` interact when both non-null?
- Which prefixes apply for `category` values other than "tech" and "business"?

```java
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTagsTest {

    @DisplayName("Category keyword filtering")
    @Description("""
        requiredCategories is null for all rows — only the category keyword filter applies.
        "tech" maps to prefixes "tech:" and "dev:"; "business" maps to "biz:" only.
        Open: which prefixes apply for category values other than "tech" and "business"?
        """)
    @TableTest("""
        Scenario                         | tags                                          | category | Filtered tags?
        tech keeps tech and dev prefixes | ["tech:java", "biz:sales", "dev:ci"]         | tech     | ["tech:java", "dev:ci"]
        business keeps only biz prefix   | ["tech:java", "biz:sales", "dev:ci"]         | business | ["biz:sales"]
        null category returns all        | ["tech:java", "biz:sales", "dev:ci"]         |          | ["tech:java", "biz:sales", "dev:ci"]
        no tags match category           | ["tech:java", "dev:ci"]                      | business | []
        empty list returns empty         | []                                            | tech     | []
        """)
    void filtersByCategory(List<String> tags, String category, List<String> filteredTags) {
        assertEquals(filteredTags, filterTags(tags, null, category));
    }

    @DisplayName("Required categories set filtering")
    @Description("""
        category is null for all rows — only the requiredCategories set filter applies.
        A tag's category prefix is the text before ":" (e.g. "tech" in "tech:java").
        Open: how do category and requiredCategories interact when both are non-null?
        """)
    @TableTest("""
        Scenario                             | tags                                          | requiredCategories | Filtered tags?
        tech and dev required, both kept     | ["tech:java", "biz:sales", "dev:ci"]         | {tech, dev}        | ["tech:java", "dev:ci"]
        only biz required                    | ["tech:java", "biz:sales", "dev:ci"]         | {biz}              | ["biz:sales"]
        null required categories returns all | ["tech:java", "biz:sales", "dev:ci"]         |                    | ["tech:java", "biz:sales", "dev:ci"]
        no tags match required categories    | ["tech:java", "dev:ci"]                      | {biz}              | []
        """)
    void filtersByRequiredCategories(List<String> tags, Set<String> requiredCategories, List<String> filteredTags) {
        assertEquals(filteredTags, filterTags(tags, requiredCategories, null));
    }

    @DisplayName("Special characters preserved through filtering")
    @Description("""
        The filter includes or excludes tags normally; tag content is returned byte-for-byte unchanged.
        \\n in the table represents a real newline character inside the tag string.
        requiredCategories is null for all rows.
        """)
    @TableTest("""
        Scenario                  | tags                                              | category | Filtered tags?
        pipe in tag preserved     | ["biz:hr|recruiting", "tech:go"]                 | business | ["biz:hr|recruiting"]
        bracket in tag preserved  | ["tech:[core]", "biz:sales"]                     | tech     | ["tech:[core]"]
        newline in tag preserved  | ["tech:java\\nEnterprise Edition", "biz:sales"]   | tech     | ["tech:java\\nEnterprise Edition"]
        pipe tag excluded         | ["biz:hr|recruiting", "tech:go"]                 | tech     | ["tech:go"]
        """)
    void preservesTagsWithSpecialCharacters(List<String> tags, String category, List<String> filteredTags) {
        List<String> input = tags.stream().map(t -> t.replace("\\n", "\n")).toList();
        List<String> expected = filteredTags.stream().map(t -> t.replace("\\n", "\n")).toList();
        assertEquals(expected, filterTags(input, null, category));
    }
}
```

A few things worth noting:

- **`"tech:go"` is quoted** in the list — colons inside list elements need quoting to avoid map key:value parsing
- **`["biz:hr|recruiting"]`** — the pipe is inside a quoted element so it's safe; the outer `[...]` is list syntax, not something to escape
- **`\\n` in the text block** → becomes literal `\n` (backslash-n) that TableTest sees → the test method restores it to a real newline before calling the method and comparing results
- The two open questions (category vs requiredCategories interaction, and unknown category strings) are flagged in `@Description` rather than silently picked a behavior