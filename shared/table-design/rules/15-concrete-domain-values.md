### Use Concrete Domain Values

Cell values are concrete, meaningful domain data — not abstract flags, codes, or placeholders. An
expectation value is traceable to the input values in its own {{row}}.

**An expectation naming something that appears nowhere in the {{row}} is a value hardcoded in the
test, not a value the table states.** The reader then cannot understand the table without reading the
code, which is the one thing the table exists to prevent.

When a value is derived from an input, include the source column so the derivation is visible.

**Prefer the value the system really produces.** Where a sentinel, enum constant or error string is
part of the observable contract, put that in the cell rather than a tidier test-only label — the
{{row}} then states what a reader would actually see. Shorten a value only when it is too long to
scan, and shorten the **value**, never the vocabulary: `acme:search:v2` scans as well as a
placeholder and still says what each part is. Single letters cost more than they save, because the
legend that decodes them lives outside the table.

Write literal values even when they repeat across {{rows}}. Extracting them into named constants
forces the reader to look up every number, which is exactly the indirection the {{rows}} exist to
remove.

{{example}}

**Check:** **Concrete values**: expectation values are literal domain values traceable to the input columns of their own {{row}} — not abstract codes, and not hidden behind named constants
