package org.example;

import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagFilterTest {

    private final TagFilter filter = new TagFilterImpl();

    @TableTest("""
        Scenario                                   | Tags                                                        | Category | Filtered?
        Tech category keeps tech and dev prefixes  | ["tech:java", "dev:kotlin", "biz:sales", "ops:deploy"]     | tech     | ["tech:java", "dev:kotlin"]
        Business category keeps biz prefix only    | ["tech:java", "biz:sales", "dev:kotlin"]                   | business | ["biz:sales"]
        Other category keeps matching prefix       | ["ops:deploy", "tech:java", "ops:monitor"]                 | ops      | ["ops:deploy", "ops:monitor"]
        Prefix without colon is not a match        | ["ops:deploy", "opsx:new", "ops:monitor"]                  | ops      | ["ops:deploy", "ops:monitor"]
        No tags match category                     | ["tech:java", "dev:kotlin"]                                | business | []
        Null category returns all tags unfiltered  | ["tech:java", "biz:sales", "ops:deploy"]                   |          | ["tech:java", "biz:sales", "ops:deploy"]
        Empty tags list                             | []                                                          | tech     | []
        """)
    void filtersTagsByCategory(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, null));
    }

    @TableTest("""
        Scenario                                   | Tags                                                        | Category | Optional   | Filtered?
        Optional adds one extra prefix             | ["tech:java", "dev:kotlin", "biz:sales", "ops:deploy"]     | tech     | {biz}      | ["tech:java", "dev:kotlin", "biz:sales"]
        Optional adds multiple prefixes            | ["tech:java", "biz:sales", "ops:deploy", "legal:contract"] | tech     | {biz, ops} | ["tech:java", "biz:sales", "ops:deploy"]
        Optional prefix with no matching tags      | ["tech:java", "dev:kotlin"]                                | tech     | {biz}      | ["tech:java", "dev:kotlin"]
        Null optional applies no extra filter      | ["tech:java", "biz:sales"]                                 | tech     |            | ["tech:java"]
        Empty optional applies no extra filter     | ["tech:java", "biz:sales"]                                 | tech     | {}         | ["tech:java"]
        """)
    void appliesOptionalCategoryPrefixes(List<String> tags, String category, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, optional));
    }

    @TableTest("""
        Scenario                 | Tags                                              | Category | Filtered?
        Tag containing pipe     | ["tech:java | core", "dev:kotlin"]               | tech     | ["tech:java | core", "dev:kotlin"]
        Tag containing brackets | ["tech:[java] module", "biz:sales"]              | tech     | ["tech:[java] module"]
        Tag containing newline  | ["tech:java\\nEnterprise Edition", "biz:sales"]  | tech     | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesTagContentAsIs(List<String> tags, String category, List<String> filtered) {
        assertEquals(unescapeNewlines(filtered), filter.filterTags(unescapeNewlines(tags), category, null));
    }

    private static List<String> unescapeNewlines(List<String> tags) {
        return tags.stream().map(tag -> tag.replace("\\n", "\n")).collect(Collectors.toList());
    }
}
