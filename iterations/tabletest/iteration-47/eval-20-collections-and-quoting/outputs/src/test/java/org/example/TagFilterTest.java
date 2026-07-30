package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter tagFilter = new TagFilterImpl();

    @Description("""
        optional is always null in this table; widening the kept tags with an
        optional category set is covered separately in widensKeptTagsWithOptionalCategories.
        """)
    @TableTest("""
        Scenario                                | Category | Tags                                                          | Kept?
        Tech category, mixed tag prefixes       | tech     | ["tech:java", "dev:kotlin", "biz:sales", "sports:football"]    | ["tech:java", "dev:kotlin"]
        Business category, mixed tag prefixes   | business | ["biz:sales", "tech:java", "dev:kotlin"]                        | ["biz:sales"]
        Custom category matches its own prefix  | sports   | ["sports:football", "sports:basketball", "biz:sales"]          | ["sports:football", "sports:basketball"]
        No tag matches the category              | tech     | ["biz:sales", "sports:football"]                                | []
        Empty tag list                           | tech     | []                                                               | []
        Null category                            |          | ["tech:java", "biz:sales", "random"]                            | ["tech:java", "biz:sales", "random"]
        Empty tag string, null category          |          | ['', "tech:java"]                                                | ["tech:java"]
        Tags containing special characters       | tech     | ["tech:java|8", "tech:[array]", "biz:sales"]                    | ["tech:java|8", "tech:[array]"]
        """)
    void keepsTagsMatchingTheCategoryPrefix(String category, List<String> tags, List<String> kept) {
        assertEquals(kept, tagFilter.filterTags(tags, category, null));
    }

    @Description("""
        optional is always null. Kept? true asserts the returned list equals the input
        tag with its embedded newline intact; false asserts an empty list.
        """)
    @TableTest("""
        Scenario                            | Category | Tag                                    | Kept?
        Newline tag matches category        | tech     | "tech:java\\nEnterprise Edition"       | true
        Newline tag does not match category | business | "tech:java\\nEnterprise Edition"       | false
        """)
    void preservesNewlinesInTagContent(String category, String rawTag, boolean kept) {
        String tag = rawTag.replace("\\n", "\n");
        List<String> expected = kept ? List.of(tag) : List.of();
        assertEquals(expected, tagFilter.filterTags(List.of(tag), category, null));
    }

    @Description("""
        Assumes an optional entry matches a tag by literal prefix equality only:
        "tech" in optional keeps "tech:" tags but not "dev:" tags, and the tech/dev
        and business/biz expansions apply only to the primary category parameter.
        Open question: should an optional entry of "tech" also admit "dev:" tags?
        """)
    @TableTest("""
        Scenario                              | Category | Optional       | Tags                                                               | Kept?
        Optional category adds an extra prefix | tech     | {sports}       | ["tech:java", "sports:football", "biz:sales"]                     | ["tech:java", "sports:football"]
        Multiple optional categories widen     | business | {tech, sports} | ["biz:sales", "tech:java", "sports:football", "other:misc"]        | ["biz:sales", "tech:java", "sports:football"]
        Null optional                          | tech     |                | ["tech:java", "biz:sales"]                                         | ["tech:java"]
        Empty optional set                     | tech     | {}             | ["tech:java", "biz:sales"]                                         | ["tech:java"]
        """)
    void widensKeptTagsWithOptionalCategories(String category, Set<String> optional, List<String> tags, List<String> kept) {
        assertEquals(kept, tagFilter.filterTags(tags, category, optional));
    }
}
