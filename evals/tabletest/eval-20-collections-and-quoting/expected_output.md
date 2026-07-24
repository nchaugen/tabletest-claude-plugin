One or more @TableTest methods whose cells exercise TableTest's collection and quoting syntax
correctly: `[]` for `List` values (and `[]` for an empty list, never a blank cell), `{}` for the
`Set` optional argument, quoting for any tag containing a colon, pipe, or bracket, `\n` escaping (or
a joined list of lines) for newlines, a quoted empty string for an empty tag, and blank cells for
null `category`/`optional`. The category rules are shown by example; the quoting is the substance.

## What this eval is really testing

The filtering rules are simple. The reason this eval exists is **representation**: a tag is a string
that routinely contains the very characters TableTest's table syntax reserves — the colon that
separates map keys, the pipe that separates columns, the brackets that open collections. A cell
written naively either fails to parse or is silently reparsed as the wrong type. The headline trap:

> `[tech:java, biz:sales]` is not a list of two strings — the colons make TableTest read each
> element as a **map** entry. The tags must be quoted: `["tech:java", "biz:sales"]`.

So the score is dominated by whether the author reached for quoting and the right bracket per type,
not by whether every category rule has a row.

## The concerns

| Concern | Rule | Notes |
|---|---|---|
| Named category `tech` | keep `tech:` **and** `dev:` | two prefixes map to one filter category |
| Named category `business` | keep `biz:` | the filter name (`business`) differs from the prefix (`biz`) — don't conflate them |
| Any other category `X` | keep `X:` | literal prefix + colon |
| Optional set | additively keep tags whose category prefix ∈ set | applies *on top of* the base category, not instead of it |
| Null category | return all tags unfiltered | blank cell = null |
| Null / empty optional | no optional filter applied | blank cell = null; distinct from an empty `Set` `{}` |
| Empty tag `""` | never kept, whatever the category | a quoted empty string, not a blank element |
| Empty list input | returns empty list | `[]`, never a blank cell (blank = null) |

## What must be exercised in rows, not narrated

These are the cells that prove the author understands the notation. Each should appear as a value in
a table, not as prose in an `@Description`:

- **List vs Set brackets** — `tags` and the result use `[…]`; the `optional` argument uses `{…}`. A
  solution that writes the `Set` with `[…]` has lost the type distinction.
- **Empty list vs null** — `[]` is an empty list input; a blank cell is a null. Both must appear and
  mean different things (the empty-list-input row and the null-category / null-optional rows).
- **Colon quoting** — every tag carrying a `:` is quoted, or the row is a map, not a list.
- **Pipe quoting** — a tag like `"biz:hr|recruiting"` is quoted so the `|` is not read as a column
  separator.
- **Bracket preservation** — a tag like `"tech:array[]"` survives as-is.
- **Newline** — `"tech:java\nEnterprise Edition"` as an escaped `\n` inside the quoted cell, or the
  input expressed as a joined list of lines. A literal line break in the row is a parse error.
- **Empty tag** — a quoted empty string `""` inside the list, filtered out; never a blank element
  (`[a, , c]` is a parse error, not a list with a null).

## Reference decomposition

Two `@TableTest` methods read better than one — the second is where the notation is stressed — but a
single well-quoted table is not wrong. `tags` and `Kept?` are `List<String>` (`[…]`); `optional` is
`Set<String>` (`{…}`); a blank cell is null.

**Table 1 — category filtering rules**

| Scenario | Tags | Category | Optional | Kept? |
|---|---|---|---|---|
| tech keeps tech: and dev: | `["tech:java", "dev:ci", "biz:sales"]` | tech | | `["tech:java", "dev:ci"]` |
| business keeps biz: | `["biz:sales", "tech:java"]` | business | | `["biz:sales"]` |
| other category keeps its own prefix | `["sales:lead", "biz:sales"]` | sales | | `["sales:lead"]` |
| optional set adds a category | `["tech:java", "biz:sales"]` | tech | `{biz}` | `["tech:java", "biz:sales"]` |
| null category returns all unfiltered | `["tech:java", "biz:sales"]` | | | `["tech:java", "biz:sales"]` |
| bare category, no colon, excluded | `["dev", "dev:ci"]` | tech | | `["dev:ci"]` |
| colon only, no category, excluded | `[":python", "tech:py"]` | tech | | `["tech:py"]` |

**Table 2 — value quoting and collection syntax**

| Scenario | Tags | Category | Kept? |
|---|---|---|---|
| empty list stays empty list | `[]` | tech | `[]` |
| empty tag never kept | `["tech:java", ""]` | tech | `["tech:java"]` |
| pipe preserved, must be quoted | `["biz:hr\|recruiting"]` | business | `["biz:hr\|recruiting"]` |
| brackets preserved | `["tech:array[]"]` | tech | `["tech:array[]"]` |
| newline escaped in cell | `["tech:java\nEnterprise Edition"]` | tech | `["tech:java\nEnterprise Edition"]` |

Table 2 fixes `optional` to null (blank) throughout because quoting, not the optional set, is its
subject; that is a deliberate focusing choice, not a coverage gap.

## Underspecified → make it an explicit row

The prompt does not spell these out; a good solution picks the reading the literal rules imply and
commits to it as a row a reviewer can challenge, rather than leaving it untested:

- **A bare category token with no colon** (`dev`, `biz`) is **excluded** — it does not start with
  `dev:` / `biz:`, and a category prefix exists only when a colon follows. Shown by the
  `["dev", "dev:ci"]` row.
- **A colon with no category** (`:python`) is **excluded** — it has no category prefix to match
  `X:`. Shown by the `[":python", "tech:py"]` row.

Either reading could in principle be argued the other way; the point is that the row states the
choice, so a disagreeing reviewer sees exactly what to change.

## Out of scope, but foreseeable

- **Whitespace-only or `" "` tags, trimming, case sensitivity of prefixes** — the prompt says
  nothing; omitting them is not a gap, and inventing a trim/normalise rule that the rules don't imply
  is a defect.
- **Duplicate tags / order preservation** — not specified; a solution may keep input order (the
  natural reading) without claiming a rule.

## Judging

Score the notation first: correct `[]`/`{}` per type, colon/pipe/bracket quoting, `\n` handling,
empty-list-vs-null and empty-string-vs-blank distinctions. These are the assertions unique to this
eval. The category rules are the vehicle; a solution that filters correctly but writes an unquoted
`[tech:java, biz:sales]` has failed the thing being measured. Choices *within* that — one table or
two, scenario wording, column order — are latitude.
