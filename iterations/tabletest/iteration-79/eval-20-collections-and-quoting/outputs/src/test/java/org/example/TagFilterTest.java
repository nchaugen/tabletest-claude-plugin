package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter filter = new TagFilterImpl();

    @Description("""
        Optional is held at null throughout, since which categories it adds is a
        separate concern covered below. Filtered tags are expected to keep their
        original relative order.
        """)
    @TableTest("""
        Scenario                        | Category                 | Tags                                                         | Filtered?
        Tech keeps tech and dev tags    | tech                     | ["tech:java", "dev:backend", "technology:node", "biz:sales"] | ["tech:java", "dev:backend"]
        Business keeps only biz tags    | business                 | ["biz:sales", "bizarre:x", "tech:java"]                      | ["biz:sales"]
        Other category keeps its prefix | sports                   | ["sports:football", "sportscar:x", "tech:java"]              | ["sports:football"]
        Null category keeps every tag   |                          | ["tech:java", "biz:sales", "sports:football", '']            | ["tech:java", "biz:sales", "sports:football"]
        Empty tag list                  | {tech, business, sports} | []                                                           | []
        """)
    void filtersTagsByCategoryPrefix(String category, List<String> tags, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, null));
    }

    @Description("""
        Category is held at "tech" throughout, since which tags a category keeps
        on its own is covered above.
        """)
    @TableTest("""
        Scenario                         | Category | Tags                                          | Optional      | Filtered?
        No optional set                  | tech     | ["tech:java", "biz:sales"]                    |               | ["tech:java"]
        Empty optional set               | tech     | ["tech:java", "biz:sales"]                    | {}            | ["tech:java"]
        Optional prefix matches a tag    | tech     | ["tech:java", "biz:sales", "bizarre:x"]       | {biz}         | ["tech:java", "biz:sales"]
        Multiple optional prefixes match | tech     | ["tech:java", "biz:sales", "sports:football"] | {biz, sports} | ["tech:java", "biz:sales", "sports:football"]
        Optional prefix matches no tag   | tech     | ["tech:java", "biz:sales"]                    | {sports}      | ["tech:java"]
        """)
    void alsoKeepsTagsMatchingAnOptionalPrefix(String category, List<String> tags, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, optional));
    }

    @Description("""
        Tag values containing pipes, brackets or newlines are kept verbatim when
        they match the category prefix. Newlines are written as \\n in the table
        and restored to real newlines before use, per TableTest convention.
        """)
    @TableTest("""
        Scenario                | Category | Tags                               | Filtered?
        Tag with pipe character | tech     | ["tech:java|enterprise"]           | ["tech:java|enterprise"]
        Tag with brackets       | tech     | ["tech:[java]"]                    | ["tech:[java]"]
        Tag with newline        | tech     | ["tech:java\\nEnterprise Edition"] | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesSpecialCharactersInKeptTags(String category, List<String> tags, List<String> filtered) {
        List<String> actualTags = withRealNewlines(tags);
        List<String> expectedFiltered = withRealNewlines(filtered);
        assertEquals(expectedFiltered, filter.filterTags(actualTags, category, null));
    }

    private static List<String> withRealNewlines(List<String> values) {
        return values.stream().map(value -> value.replace("\\n", "\n")).toList();
    }
}
