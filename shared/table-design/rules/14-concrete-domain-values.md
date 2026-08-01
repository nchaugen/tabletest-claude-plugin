### Use Concrete Domain Values

Cell values are concrete, meaningful domain data — not abstract flags, codes, or placeholders. An
expectation value is traceable to the input values in its own {{row}}.

**An expectation naming something that appears nowhere in the {{row}} is a value hardcoded in the
test, not a value the table states.** The reader then cannot understand the table without reading the
code, which is the one thing the table exists to prevent.

When a value is derived from an input, include the source column so the derivation is visible.

Write literal values even when they repeat across {{rows}}. Extracting them into named constants
forces the reader to look up every number, which is exactly the indirection the {{rows}} exist to
remove.

{{example}}
