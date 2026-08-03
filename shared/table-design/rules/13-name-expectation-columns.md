### Name Expectation Columns Clearly

End every expectation column with a `?` **suffix**, so a reader can tell at a glance which columns are
outputs being verified and which are inputs being provided. Input columns never take `?` — including
yes/no columns that describe the state a scenario starts in.

**Prefer the rule's direct output.** `Fee?` beats `Total?`: the fee is what the rule decides, while
verifying the total also requires knowing the base amount. If you do expect a derived value, include
its inputs as columns so a reader can trace it.

**One exception, and it is narrow: identity and status varying together in the same output
position.** Where a column answers *which* one and *how it went* at once — the winner of a pair and
whether it succeeded — the two are one value and the cell names it as one, `Primary OK` against
`Secondary ERROR`. Splitting that into "which?" and "did it succeed?" columns doubles them and makes
the reader join the halves back up. This is a domain value with its own type, not an encoding: it
holds only where **both** parts vary in the same position. Where identity is fixed for the {{row}} and
only the status varies, the ordinary column design applies.

**A compound result stays a collection.** When the value under test is several items — or items
grouped under a key — the expectation is a native list, set or map, nested where needed, compared
against what the system returns. Do not flatten it into a string assembled by a formatting helper:
that tests the formatter rather than the rule, hides the structure from the reader, and puts
formatting logic back into the test body. Use a set where order is not part of the rule, and a list
with a canonical sort where it is.

{{example}}

**Check:** **Expectation columns marked**: at least one column uses the `?` suffix (never a prefix), no input column does, and a compound result stays a native collection rather than a flattened string
