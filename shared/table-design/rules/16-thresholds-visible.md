### Make Thresholds Visible

When a rule depends on a threshold or limit, include it as a column — even when the value is constant
across every {{row}}.

Without the threshold column the number is buried in the code: the reader cannot tell from the table
where the boundary is, or whether the rule is strictly greater than. Boundary {{rows}} — at the limit,
just over it — become natural to add once the threshold is visible.

**A constant column often signals configuration.** Ask under what circumstances the value would
differ. The answer may reveal a second axis that belongs as new {{rows}} or as a separate table.

{{example}}
