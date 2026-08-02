Wrote `tests/test_baggage.py` with two table-driven pytest tests:

- **`test_charges_fee_by_checked_bag_weight_tier`** — 5 cases covering the three fee tiers and both sides of each internal boundary (23/24 kg, 32/33 kg), plus the oversize upper bound (45 kg).
- **`test_rejects_a_checked_bag_just_over_the_oversize_limit`** — separate `pytest.raises(BagNotAccepted)` test for 46 kg.

**Assumption/design note:** I split rejection into its own test rather than adding it as a row to the tier table. Per the table-design rule ("strike the rejection case — does the rest still state a rule?"), removing the 46 kg case from the tier ladder would still leave a complete, meaningful fee table, so acceptance and rejection are treated as two concerns rather than one boundary. I didn't add a "well within the included tier" case (e.g. 5 kg) since 23 kg already witnesses that tier and adding a second same-tier case would be redundant per "further past the same boundary."

Verified against the stub with a temp venv: all 6 cases collect cleanly and fail with `NotImplementedError` (not a collection error), confirming the tests are ready for implementation.