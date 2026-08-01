### Include All Outputs of a Concern

When an operation produces several observable outputs, include them all as expectation columns in one
table. Each {{row}} then gives the complete picture of what happens for that scenario. Splitting the
outputs of one concern forces the reader to cross-reference several tables to understand one
behaviour.

Separate tables are for separate **concerns**, never for separate outputs of the same concern.

**Every expectation column must be exercised by the {{rows}} the table varies.** A column that is
constant down every {{row}}, or that changes only as a side effect of another column, is not being
tested. Two repairs, and which is right depends on the rule:

- **Give it {{rows}} that vary it**, when the column does belong to this table's axis and the {{rows}}
  were missing.
- **Move it to the table whose axis varies it**, and drop it here rather than keeping it "for
  completeness".

{{example}}
