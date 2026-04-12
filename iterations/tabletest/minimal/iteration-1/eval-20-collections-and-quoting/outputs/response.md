Working through the design before writing code.

**Step 1 — Name the concerns**

Two distinct filtering rules operate independently:
- *Filter by category alias* — "tech" and "business" map to known prefixes
- *Filter by required categories set* — any set of prefix names acts as an allowlist

Null category and null requiredCategories each mean "no filter applied" — these are edge cases of the same concern, not separate ones.

**Steps 2-4 — Probe for variations**

| Concern | Key variations |
|---|---|
| Category alias | tech→{tech:,dev:}, business→{biz:}, null→all, empty list |
| RequiredCategories | {tech,dev}, {biz}, null→all, empty list |
| Both | Interaction when both are non-null — open question |
| Values | pipe in element (`biz:hr\|recruiting`), bracket mid-tag (`tech:java[8]`), newline (`tech:java\nEnterprise Edition`) |

**Step 5 — Newline handling**

`\n` inside a tag value can't be a literal newline in the table row (it would split the row). Per TableTest syntax, write `\\n` in the text block — compiled to `\n` (literal backslash-n) — then replace in the test method. Both `tags` and `filteredTags` lists need the replacement.

**Step 9 — Code**

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTagsTest {

    private final TagFilter sut = new TagFilter();

    @DisplayName("Category-based tag filtering")
    @Description("""
            requiredCategories is null for all rows — only the category alias filter is exercised.
            "tech" maps to prefixes tech: and dev:; "business" maps to biz:.
            Open question: when both category and requiredCategories are non-null, do both
            filters apply as an intersection, or does one take precedence?
            """)
    @TableTest("""
            Scenario                       | tags                                             | category | Filtered tags?
            Tech keeps tech: and dev:      | ["tech:java", "biz:sales", "dev:ci"]             | tech     | ["tech:java", "dev:ci"]
            Business keeps biz: only       | ["tech:java", "biz:sales", "dev:ci"]             | business | ["biz:sales"]
            Null category keeps all        | ["tech:java", "biz:sales", "dev:ci"]             |          | ["tech:java", "biz:sales", "dev:ci"]
            Empty list returns empty       | []                                               | tech     | []
            Pipe in tag value preserved    | ["biz:hr|recruiting", "tech:go"]                 | business | ["biz:hr|recruiting"]
            Bracket in tag value preserved | ["tech:java[8]", "biz:sales"]                    | tech     | ["tech:java[8]"]
            Newline in tag value preserved | ["tech:java\\nEnterprise Edition", "biz:sales"]  | tech     | ["tech:java\\nEnterprise Edition"]
            """)
    void filtersByCategory(List<String> tags, String category, List<String> filteredTags) {
        List<String> input = tags.stream().map(t -> t.replace("\\n", "\n")).toList();
        List<String> expected = filteredTags.stream().map(t -> t.replace("\\n", "\n")).toList();
        assertEquals(expected, sut.filterTags(input, null, category));
    }

    @DisplayName("RequiredCategories set filtering")
    @Description("""
            category is null for all rows — only the requiredCategories set filter is exercised.
            A tag matches when the substring before its first colon is in requiredCategories.
            """)
    @TableTest("""
            Scenario                          | tags                                  | requiredCategories | Filtered tags?
            Tech and dev required             | ["tech:java", "biz:sales", "dev:ci"]  | {tech, dev}        | ["tech:java", "dev:ci"]
            Biz only required                 | ["tech:java", "biz:sales", "dev:ci"]  | {biz}              | ["biz:sales"]
            Null requiredCategories keeps all | ["tech:java", "biz:sales", "dev:ci"]  |                    | ["tech:java", "biz:sales", "dev:ci"]
            Empty list returns empty          | []                                    | {tech, dev}        | []
            Pipe in tag value preserved       | ["biz:hr|recruiting", "tech:go"]      | {biz}              | ["biz:hr|recruiting"]
            """)
    void filtersByRequiredCategories(List<String> tags, Set<String> requiredCategories, List<String> filteredTags) {
        assertEquals(filteredTags, sut.filterTags(tags, requiredCategories, null));
    }
}
```

**Key decisions:**

- **Two tables** — one fixes `requiredCategories=null` to isolate the category alias rule; the other fixes `category=null` to isolate the set filter. This avoids a combinatorial explosion and makes failures easier to diagnose.
- **Pipe and bracket elements** are quoted within the list (`"biz:hr|recruiting"`) per the "quote inside collection, not the whole collection" rule. The `[8]` bracket is mid-element so needs no extra quoting beyond the colon-quoting already applied.
- **Newlines** use `\\n` in the text block (compiles to literal `\n`) with `replace("\\n", "\n")` in the method — the documented approach when tag values span logical lines.
- **Null cells** represent null inputs for `category` and `requiredCategories` (blank = null reference, not empty string).
- **Open question** about both-non-null interaction is captured in `@Description` rather than silently resolved with a guess.