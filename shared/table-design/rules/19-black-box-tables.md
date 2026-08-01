### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags and setup-only columns unless they are part
of the public contract.

Anything the test does beyond arranging, acting and asserting is a rule the table cannot show.
Construction belongs in a conversion helper, the expected error in a column, defaulting and
normalisation outside the body entirely. When you find yourself writing logic in the test, ask which
column or helper it should have been.

{{example}}
