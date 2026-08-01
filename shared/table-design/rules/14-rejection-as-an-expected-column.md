### Model Rejection as an Expected Column

When a table covers cases the system rejects, the rejection is an **expectation column** — the error
type, or the reason — never a decision taken in the test body. Each {{row}} then states its own
outcome where the reader can see it.

**Whether accepted and rejected {{rows}} share a table is decided by what the table is about.**

- **The table's whole expectation is whether the call is rejected** — a boundary straddling a
  validation limit, the last accepted value beside the first rejected one. That is *one rule*, and
  splitting it puts the two halves of a single boundary where no reader sees them together. Keep one
  table, leave the rejection column blank where nothing is rejected, and compare the outcome as a
  value.
- **Rejection is one outcome among several** — a parser returning values for good input and rejecting
  malformed input. Those are two concerns and belong in two tables.

**Never branch in the body to choose how to assert.** Picking between a rejection assertion and a
value assertion per {{row}} puts the rule back where the table cannot show it, and it is the failure
both shapes above exist to avoid.

{{example}}

**Check:** **Rejection expressed as data**: rejected {{rows}} carry the error type or reason in an expectation column, never a hardcoded outcome in the body; accepted and rejected {{rows}} share a table only where the whole expectation is acceptance, and no {{row}} branches the assertion
