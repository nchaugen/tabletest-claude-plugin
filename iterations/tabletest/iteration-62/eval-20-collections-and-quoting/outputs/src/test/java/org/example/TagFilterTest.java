package org.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter tagFilter = new TagFilterImpl();

    @DisplayName("Keeps tags matching the category's resolved prefix")
    @Description("""
        Tech resolves to two prefixes (tech: and dev:), business resolves to biz:,
        and any other category resolves to itself followed by a colon. The optional
        category set is held absent throughout; its effect is covered separately in
        keepsTagsWhosePrefixIsInTheOptionalSet. Tag content that looks like "\\n" in
        this table stands for a real newline character, restored by withRealNewlines
        before the assertion.
        """)
    @TableTest("""
        Scenario                                    | Category | Tags                                              | Filtered?
        Tech keeps tech and dev prefixed tags       | tech     | ["tech:java", "dev:kotlin", "biz:sales", "ops:x"] | ["tech:java", "dev:kotlin"]
        Business keeps only biz prefixed tags       | business | ["tech:java", "biz:sales", "dev:kotlin"]          | ["biz:sales"]
        Custom category uses its own name as prefix | urgent   | ["urgent:fixbug", "tech:java", "biz:sales"]       | ["urgent:fixbug"]
        No tags match the category                  | tech     | ["biz:sales", "ops:x"]                            | []
        Empty tag list                              | tech     | []                                                | []
        Empty tag string is dropped among matches   | tech     | ["tech:a", '', "dev:b"]                           | ["tech:a", "dev:b"]
        Special characters are preserved as-is      | tech     | ["tech:a|b", "tech:[c]", "dev:{d}"]               | ["tech:a|b", "tech:[c]", "dev:{d}"]
        Newline in tag content is preserved         | tech     | ["tech:java\\nEnterprise Edition"]                | ["tech:java\\nEnterprise Edition"]
        """)
    void keepsTagsMatchingTheCategorysResolvedPrefix(String category, List<String> tags, List<String> filtered) {
        assertEquals(withRealNewlines(filtered), tagFilter.filterTags(withRealNewlines(tags), category, null));
    }

    @DisplayName("Extends the kept tags with prefixes listed in the optional set")
    @Description("""
        Category resolution itself is covered by keepsTagsMatchingTheCategorysResolvedPrefix;
        here category varies only to show the optional set works the same way alongside any
        of them. Each optional entry is matched the same way a custom category is: the entry
        followed by a colon.
        """)
    @TableTest("""
        Scenario                                        | Category | Tags                                     | Optional      | Filtered?
        Empty optional adds nothing                     | tech     | ["tech:a", "dev:b", "urgent:c"]          | {}            | ["tech:a", "dev:b"]
        Null optional adds nothing                      | tech     | ["tech:a", "urgent:c"]                   |               | ["tech:a"]
        Single optional prefix is added                 | tech     | ["tech:a", "urgent:c", "biz:d"]          | {urgent}      | ["tech:a", "urgent:c"]
        Multiple optional prefixes are added            | business | ["biz:a", "urgent:b", "dev:c", "tech:d"] | {urgent, dev} | ["biz:a", "urgent:b", "dev:c"]
        Optional prefix combined with a custom category | ops      | ["ops:a", "urgent:b", "tech:c"]          | {urgent}      | ["ops:a", "urgent:b"]
        """)
    void keepsTagsWhosePrefixIsInTheOptionalSet(String category, List<String> tags, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, optional));
    }

    @DisplayName("Returns all tags unchanged when category is null")
    @Description("""
        Assumption: a null category is a full bypass of prefix filtering, including the
        optional set, since there is no category to resolve a prefix set from. The
        empty-tag rule still applies, since it holds "whatever the category".
        """)
    @TableTest("""
        Scenario                                    | Tags                          | Optional | Filtered?
        Tags of every prefix are returned unchanged | ["tech:a", "biz:b", "misc:c"] |          | ["tech:a", "biz:b", "misc:c"]
        Empty tag is still dropped                  | ["tech:a", '', "biz:b"]       |          | ["tech:a", "biz:b"]
        Optional set has no effect                  | ["tech:a", "biz:b"]           | {ops}    | ["tech:a", "biz:b"]
        """)
    void returnsAllTagsUnchangedWhenCategoryIsNull(List<String> tags, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, null, optional));
    }

    private static List<String> withRealNewlines(List<String> tags) {
        return tags.stream().map(tag -> tag.replace("\\n", "\n")).toList();
    }
}
