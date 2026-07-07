A Python project has been created for this feature, with pytest configured.

Under `src/`, the project already contains a stub `library_fees` module with an unimplemented `late_fee_cents` function.

We want to write the tests before implementing the fee logic.

Write table-driven pytest tests for `late_fee_cents(...)`.

The rules are:
- Books returned by the due date incur no fee
- Late returns cost 50 cents per day late
- Fees are capped at 20 euros
- Books from the children's section are charged at half rate

Keep the stub unimplemented — the tests are expected to fail until the logic is written, but they must import and collect cleanly.
