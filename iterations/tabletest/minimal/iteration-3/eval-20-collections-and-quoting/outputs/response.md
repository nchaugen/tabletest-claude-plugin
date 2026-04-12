Here's the complete TableTest:

```java
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTagsTest {

    private final TagService subject = new TagService();

    @TableTest("""
        Scenario                   | Tags                                          | Category | Filtered?
        tech keeps tech and dev    | ["tech:java", "biz:sales", "dev:ci"]         | tech     | ["tech:java", "dev:ci"]
        business keeps only biz    | ["tech:java", "biz:sales", "dev:ci"]         | business | ["biz:sales"]
        null category, no filter   | ["tech:java", "biz:sales", "dev:ci"]         |          | ["tech:java", "biz:sales", "dev:ci"]
        empty input                | []                                            | tech     | []
        """)
    void filtersByCategory(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, subject.filterTags(tags, null, category));
    }

    @TableTest("""
        Scenario                   | Tags                                          | Required Categories | Filtered?
        tech and dev required      | ["tech:java", "biz:sales", "dev:ci"]         | {tech, dev}         | ["tech:java", "dev:ci"]
        biz only required          | ["tech:java", "biz:sales", "dev:ci"]         | {biz}               | ["biz:sales"]
        null required, no filter   | ["tech:java", "biz:sales", "dev:ci"]         |                     | ["tech:java", "biz:sales", "dev:ci"]
        """)
    void filtersByRequiredCategories(List<String> tags, Set<String> requiredCategories, List<String> filtered) {
        assertEquals(filtered, subject.filterTags(tags, requiredCategories, null));
    }

    @TableTest("""
        Scenario                          | Tags                                          | Category | Required Categories | Filtered?
        category and required intersect   | ["tech:java", "biz:sales", "dev:ci"]         | tech     | {tech}              | ["tech:java"]
        conflicting filters yield nothing | ["tech:java", "biz:sales", "dev:ci"]         | tech     | {biz}               | []
        """)
    void combinesFilters(List<String> tags, String category, Set<String> requiredCategories, List<String> filtered) {
        assertEquals(filtered, subject.filterTags(tags, requiredCategories, category));
    }

    @Description("""
        Tags containing pipes, brackets, or newlines must pass through unchanged when kept.
        Newlines are written as \\n in the table and substituted before calling filterTags.
        """)
    @TableTest("""
        Scenario                 | Tag                               | Category | Filtered?
        pipe in tag value        | "biz:hr|recruiting"               | business | "biz:hr|recruiting"
        bracket in tag value     | "tech:[java]"                     | tech     | "tech:[java]"
        newline in display text  | "tech:java\\nEnterprise Edition"  | tech     | "tech:java\\nEnterprise Edition"
        """)
    void preservesSpecialCharacters(String tag, String category, String expected) {
        String input = tag.replace("\\n", "\n");
        String expectedTag = expected.replace("\\n", "\n");
        assertEquals(List.of(expectedTag), subject.filterTags(List.of(input), null, category));
    }
}
```

A few design notes:

**Why four tables instead of one big one:** Each table isolates one concern. `filtersByCategory` holds `requiredCategories` fixed at null; `filtersByRequiredCategories` holds `category` fixed at null. The combination table specifically tests their interaction. This prevents a combinatorial explosion and makes failures point directly at which rule broke.

**Quoting:** List elements with colons (`"tech:java"`) are quoted inside the collection to prevent TableTest from treating them as map `key: value` syntax. The pipe tag (`"biz:hr|recruiting"`) is quoted at the cell level for the same reason.

**Newlines:** The `\\n` in the text block stays as the literal two-character sequence `\n` — Java text blocks don't collapse it because of the double backslash. The test method swaps it for a real newline before calling `filterTags`. The `@Description` documents this convention so it's not a surprise.

**`combinesFilters`** tests the case where both filters narrow the result (`category=tech` + `requiredCategories={tech}` excludes `dev:ci`), and the edge case where they contradict each other and produce an empty result.