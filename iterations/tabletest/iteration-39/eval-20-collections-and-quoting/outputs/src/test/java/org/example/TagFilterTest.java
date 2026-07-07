package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagFilterTest {

    private final TagFilter filter = new TagFilterImpl();

    @TableTest("""
        Scenario                          | Tags                                                          | Category | Filtered?
        Tech category keeps tech and dev  | ["tech:java", "dev:api", "biz:sale", "other:misc"]            | tech     | ["tech:java", "dev:api"]
        Business category keeps biz       | ["tech:java", "dev:api", "biz:sale", "other:misc"]            | business | ["biz:sale"]
        Other category keeps own prefix   | ["tech:java", "dev:api", "biz:sale", "custom:misc"]           | custom   | ["custom:misc"]
        No tags match category            | ["tech:java", "biz:sale"]                                     | legal    | []
        Prefix without colon excluded     | ["tech:java", "techstuff", "dev:api"]                         | tech     | ["tech:java", "dev:api"]
        Near-miss prefix excluded         | ["biz:sale", "bizarre:thing"]                                 | business | ["biz:sale"]
        """)
    void filtersTagsByCategory(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, Set.of()));
    }

    @Description("""
        Optional entries are literal category prefixes: an entry does not go through
        the tech/business aliasing that applies to the main category parameter.
        """)
    @TableTest("""
        Scenario                            | Tags                                                    | Category | Optional     | Filtered?
        Optional adds another prefix        | ["tech:java", "biz:sale", "legal:doc"]                  | tech     | {legal}      | ["tech:java", "legal:doc"]
        Optional adds several prefixes      | ["tech:java", "biz:sale", "legal:doc", "hr:memo"]       | tech     | {legal, hr}  | ["tech:java", "legal:doc", "hr:memo"]
        Optional prefix is not aliased      | ["biz:sale", "tech:code", "dev:api"]                    | business | {tech}       | ["biz:sale", "tech:code"]
        Optional overlapping base category  | ["tech:java", "dev:api"]                                | tech     | {tech}       | ["tech:java", "dev:api"]
        """)
    void appliesOptionalCategoryPrefixes(List<String> tags, String category, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, optional));
    }

    @TableTest("""
        Scenario                     | Tags                                       | Category                 | Optional | Filtered?
        Empty tag list               | []                                         | {tech, business, legal}  |          | []
        Null category is unfiltered  | ["tech:java", "biz:sale", "misc"]          |                          |          | ["tech:java", "biz:sale", "misc"]
        Null optional applies none   | ["tech:java", "biz:sale"]                  | tech                     |          | ["tech:java"]
        Empty optional applies none  | ["tech:java", "biz:sale"]                  | tech                     | {}       | ["tech:java"]
        """)
    void handlesEmptyAndNullInputs(List<String> tags, String category, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, optional));
    }

    @Description("""
        \\n in the table stands for a literal newline within tag content; withNewlines
        converts it before calling the filter and before comparing the expected result.
        """)
    @TableTest("""
        Scenario               | Tags                                          | Category | Filtered?
        Pipe preserved as-is   | ["tech:a|b", "biz:c"]                        | tech     | ["tech:a|b"]
        Brackets preserved     | ["tech:[x]", "biz:y"]                        | tech     | ["tech:[x]"]
        Newline preserved      | ["tech:java\\nEnterprise Edition", "biz:x"] | tech     | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesSpecialCharactersInTagContent(List<String> tags, String category, List<String> filtered) {
        assertEquals(withNewlines(filtered), filter.filterTags(withNewlines(tags), category, Set.of()));
    }

    private static List<String> withNewlines(List<String> tags) {
        return tags.stream()
            .map(tag -> tag.replace("\\n", "\n"))
            .collect(Collectors.toList());
    }
}
