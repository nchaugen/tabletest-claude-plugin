Working through the analysis before writing code:

**Concerns:** Single — "Filter tags by category." No "and" needed.

**Variations:**
- `tech` → keep `tech:` and `dev:` prefixes
- `business` → keep `biz:` prefix only
- null category → all tags through (regardless of what the tags are)
- empty input → empty output (regardless of category — value set opportunity)
- pipe `|` in tag value preserved — already in user's inputs (`biz:hr|recruiting`)
- bracket `[` in tag value preserved — needs quoting in the table since it appears mid-value

**Quoting:** `biz:hr|recruiting` contains `|` → quote the element inside the list. `tech:[stable]` has `[` embedded mid-string (not at start, so technically fine, but quoting it defensively is correct). Expected list `["biz:hr|recruiting"]` also needs its element quoted.

**Value sets:** Empty list returns `[]` regardless of category → `{tech, business}` on that row.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilterTagsTest {

    private final TagFilter subject = new TagFilter();

    @DisplayName("Filter tags by category")
    @Description("""
        "tech" keeps tags prefixed with "tech:" or "dev:".
        "business" keeps tags prefixed with "biz:" only.
        Null category returns all tags unfiltered.
        Special characters in tag values (pipes, brackets) are preserved as-is.
        """)
    @TableTest("""
        Scenario                             | Tags                               | Category         | Filtered Tags?
        tech keeps tech: and dev: prefixes   | [tech:java, biz:sales, dev:ci]     | tech             | [tech:java, dev:ci]
        business keeps only biz: prefix      | [tech:java, biz:sales, dev:ci]     | business         | [biz:sales]
        tech filters out pipe-containing tag | ["biz:hr|recruiting", tech:go]     | tech             | [tech:go]
        business preserves pipe in tag value | ["biz:hr|recruiting", tech:go]     | business         | ["biz:hr|recruiting"]
        tech preserves brackets in tag value | ["tech:[stable]", biz:sales]       | tech             | ["tech:[stable]"]
        empty list regardless of category   | []                                 | {tech, business} | []
        null category returns all tags      | [tech:java, biz:sales, dev:ci]     |                  | [tech:java, biz:sales, dev:ci]
        """)
    void filtersByCategory(List<String> tags, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, category));
    }
}
```

A few notes on the design choices:

- **`"biz:hr|recruiting"` is quoted inside the list** because pipe is a table delimiter — the quotes wrap the element, not the whole list.
- **`"tech:[stable]"` is quoted** because embedded `[` could confuse the list parser.
- **Blank cell for `Category`** on the last row maps to `null` (reference type).
- **`{tech, business}` value set** on the empty-list row asserts the result is `[]` regardless of which concrete category is passed — two rows generated, one assertion.
- The bracket-preservation row (`tech:[stable]`) was added beyond your stated inputs since you listed that rule explicitly; remove it if you'd prefer only the inputs you specified.