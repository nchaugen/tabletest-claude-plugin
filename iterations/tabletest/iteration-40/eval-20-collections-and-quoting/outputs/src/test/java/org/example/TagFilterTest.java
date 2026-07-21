package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagFilterTest {

    private final TagFilter filter = new TagFilterImpl();

    @Description("""
        tech keeps tags starting with "tech:" or "dev:"; business keeps tags starting
        with "biz:"; any other category keeps tags starting with that category followed
        by ":". A prefix match requires the colon to immediately follow the category
        name, so a tag merely containing the category name is not enough.
        """)
    @TableTest("""
        Scenario                                     | Tags                                    | Category | Kept?
        Tech category keeps tech and dev tags         | ["tech:java", "dev:python", "biz:sales", "ops:deploy"] | tech     | ["tech:java", "dev:python"]
        Business category keeps only biz tags         | ["biz:sales", "tech:java", "dev:python"] | business | ["biz:sales"]
        Custom category keeps tags with matching prefix | ["ops:deploy", "tech:java", "biz:sales"] | ops      | ["ops:deploy"]
        Similar but non-matching prefix is not kept   | ["techdebt:old", "tech:java"]           | tech     | ["tech:java"]
        No tags match the category                    | ["tech:java", "biz:sales"]              | ops      | []
        Empty tag list returns empty list              | []                                      | tech     | []
        Null category returns all tags unfiltered      | ["tech:java", "biz:sales", "random"]    |          | ["tech:java", "biz:sales", "random"]
        """)
    void filtersTagsByCategory(List<String> tags, String category, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, category, null));
    }

    @Description("""
        The optional set names extra categories whose prefixed tags are kept in
        addition to the tags the category itself already keeps.
        """)
    @TableTest("""
        Scenario                                | Category | Optional    | Tags                                                    | Kept?
        Optional adds one extra category's tags | tech     | {biz}       | ["tech:java", "biz:sales", "dev:python", "ops:deploy"] | ["tech:java", "biz:sales", "dev:python"]
        Optional adds multiple extra categories  | business | {tech, ops} | ["biz:sales", "tech:java", "ops:deploy", "dev:python"] | ["biz:sales", "tech:java", "ops:deploy"]
        Null optional applies no extra filter    | business |             | ["biz:sales", "tech:java"]                             | ["biz:sales"]
        Empty optional applies no extra filter   | business | {}          | ["biz:sales", "tech:java"]                             | ["biz:sales"]
        """)
    void optionalCategoriesSupplementTheKeptSet(String category, Set<String> optional, List<String> tags, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, category, optional));
    }

    @Description("""
        Individual tag strings are preserved verbatim when kept, including special
        characters, and an empty tag string is always dropped, even when category
        filtering itself is bypassed by a null category.
        Newlines cannot appear literally in a table row, so "\\n" stands in for a
        real newline and is restored with String.replace before use.
        """)
    @TableTest("""
        Scenario                                                        | Tag                                   | Category | Kept?
        Tag with pipe character preserved as-is                        | "tech:java|enterprise"                | tech     | ["tech:java|enterprise"]
        Tag with brackets preserved as-is                               | "tech:report[Q1]"                     | tech     | ["tech:report[Q1]"]
        Tag with embedded newline preserved as-is                       | "tech:java\\nEnterprise Edition"     | tech     | ["tech:java\\nEnterprise Edition"]
        Empty tag string is never kept                                  | ''                                    | tech     | []
        Empty tag string is never kept even when category is bypassed  | ''                                    |          | []
        """)
    void preservesTagFormatting(String tag, String category, List<String> kept) {
        String resolvedTag = tag.replace("\\n", "\n");
        List<String> expectedKept = kept.stream().map(t -> t.replace("\\n", "\n")).toList();

        assertEquals(expectedKept, filter.filterTags(List.of(resolvedTag), category, null));
    }
}
