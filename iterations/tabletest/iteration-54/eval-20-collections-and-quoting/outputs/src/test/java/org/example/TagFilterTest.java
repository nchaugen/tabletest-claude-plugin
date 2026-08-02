package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagFilterTest {

    private final TagFilter filter = new TagFilterImpl();

    @Description("""
        Optional is omitted (always null) in every row here; see addsOptionalCategoryPrefixesToKeptSet
        for how the optional set affects the result.
        """)
    @TableTest("""
        Scenario                              | Tags                                                           | Category | Kept?
        Tech category keeps tech and dev tags | ["tech:java", "dev:kotlin", "biz:sales", "sports:football"]   | tech     | ["tech:java", "dev:kotlin"]
        Business category keeps biz tags      | ["biz:sales", "tech:java", "sports:football"]                  | business | ["biz:sales"]
        Custom category keeps its own prefix  | ["sports:football", "tech:java", "biz:sales"]                  | sports   | ["sports:football"]
        No tags match the category            | ["tech:java", "biz:sales"]                                     | sports   | []
        Empty tag list                        | []                                                              | tech     | []
        """)
    void filtersTagsByCategoryPrefix(List<String> tags, String category, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, category, null));
    }

    @Description("""
        Category is fixed at 'tech' in every row; this table is about how the optional set adds
        prefixes, not about category selection (see filtersTagsByCategoryPrefix for that concern).
        Optional set members are bare category names (e.g. "sports"), matched the same way the
        category parameter is: as a prefix followed by ':'.
        """)
    @TableTest("""
        Scenario                        | Tags                                                            | Category | Optional      | Kept?
        No optional supplied            | ["tech:java", "dev:kotlin", "sports:football"]                 | tech     |               | ["tech:java", "dev:kotlin"]
        Empty optional set              | ["tech:java", "dev:kotlin", "sports:football"]                 | tech     | {}            | ["tech:java", "dev:kotlin"]
        Optional adds one prefix        | ["tech:java", "dev:kotlin", "sports:football"]                 | tech     | {sports}      | ["tech:java", "dev:kotlin", "sports:football"]
        Optional adds multiple prefixes | ["tech:java", "sports:football", "biz:sales", "art:painting"] | tech     | {sports, biz} | ["tech:java", "sports:football", "biz:sales"]
        """)
    void addsOptionalCategoryPrefixesToKeptSet(List<String> tags, String category, Set<String> optional, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, category, optional));
    }

    @Description("""
        Assumption: a null category bypasses filtering entirely, including the empty-tag exclusion
        rule tested in excludesEmptyTagStrings — "unfiltered" means the input list is returned as-is,
        with no rule (category, optional or empty-tag) applied.
        """)
    @TableTest("""
        Scenario                                       | Tags                                          | Category | Optional      | Kept?
        No optional supplied, category null            | ["tech:java", "biz:sales", random text, '']  |          |               | ["tech:java", "biz:sales", random text, '']
        Optional supplied, category null still ignored | ["tech:java", "biz:sales", random text, '']  |          | {sports, biz} | ["tech:java", "biz:sales", random text, '']
        """)
    void returnsTagsUnfilteredWhenCategoryIsNull(List<String> tags, String category, Set<String> optional, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, category, optional));
    }

    @Description("""
        Optional is omitted (always null) in every row here; the empty-tag rule is independent of it.
        """)
    @TableTest("""
        Scenario                                  | Tags               | Category                 | Kept?
        Excludes empty tag while keeping a match  | ["tech:java", ''] | tech                     | ["tech:java"]
        Excludes empty tag regardless of category | ['']               | {tech, business, sports} | []
        """)
    void excludesEmptyTagStrings(List<String> tags, String category, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, category, null));
    }

    @Description("""
        Category is fixed at 'tech' and optional is omitted (always null) in every row; this table is
        about preserving tag content, not about category selection or optional prefixes. Newlines are
        written as \\n and unescaped below, since a table cell cannot hold a literal newline.
        """)
    @TableTest("""
        Scenario                     | Tags                                               | Category | Kept?
        Preserves pipe characters    | ["tech:java|Enterprise", "biz:ignored"]           | tech     | ["tech:java|Enterprise"]
        Preserves bracket characters | ["tech:[legacy]", "biz:ignored"]                  | tech     | ["tech:[legacy]"]
        Preserves embedded newlines  | ["tech:java\\nEnterprise Edition", "biz:ignored"] | tech     | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesTagContentWhenKept(List<String> tags, String category, List<String> kept) {
        assertEquals(unescapeNewlines(kept), filter.filterTags(unescapeNewlines(tags), category, null));
    }

    private static List<String> unescapeNewlines(List<String> values) {
        return values.stream().map(value -> value.replace("\\n", "\n")).toList();
    }
}
