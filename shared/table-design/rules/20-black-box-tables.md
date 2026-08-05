### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags and setup-only columns unless they are part
of the public contract.

**This is not a reason to fuse two rules into one table.** An intermediate value being internal rules
out a column for it, not a table for the rule that produces it — and the published call will often
report that value already, given the right inputs; see *Separate Rules from Arithmetic*, route 1.

Anything the test does beyond arranging, acting and asserting is a rule the table cannot show.
Construction belongs in a conversion helper, the expected error in a column, defaulting and
normalisation outside the body entirely. When you find yourself writing logic in the test, ask which
column or helper it should have been.

{{example}}

**Check:** **Black-box design**: columns represent observable inputs and outputs, not internal flags or implementation details
