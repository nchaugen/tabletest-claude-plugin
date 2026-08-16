Hand-authored reference answer for eval-8, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Two `@TableTest` methods — the amounts that parse, and the input that is rejected. Compiled and run
against the real scaffolding: 9 cases, all failing against the unimplemented stub, which is what the
prompt asks for.

**It goes past the prompt's example values, which is the thing eval-8 says it is measuring.** Its
ground truth asks for "credit for using BigDecimal's scale/format questions to generate explicit
boundary rows rather than copying the two example values and stopping". All seven stored solutions
carry the same six rows — `10.00`, `0.01`, null, `''`, `abc`, `-5.00` — and score 16/16.

Three rows here answer a question the requirement leaves open, and each is one cell for a reviewer
to argue with:

- **`5 -> 5`** says the parser adds no fraction. A parser normalising to two places returns `5.00`,
  which is a different `BigDecimal` — `equals` compares scale as well as value — so this row is what
  separates the two readings.
- **`0.00 -> 0.00`** places zero on the accepted side. The requirement rejects below zero and
  accepts above it, and never says where zero itself falls.
- **`$10.00` rejected** says a well-formed amount carrying anything the format does not mention is
  refused rather than quietly stripped.

`0.01` is kept although `0.00` is lower and accepted, because it is the requirement's own example
and does not rest on the zero row's contested reading. One decimal place (`10.5`) is left out: with
`5` and `10.00` present it re-shows a rule already stated, which is what the excess test cuts.
