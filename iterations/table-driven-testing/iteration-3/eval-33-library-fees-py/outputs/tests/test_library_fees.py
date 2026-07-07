"""Tests for late_fee_cents.

Assumptions (the feature description leaves these open):
- `days_late` is non-negative; 0 means returned on or before the due date.
  Computing days-late from a due date and a return date is the caller's
  concern, not this function's.
- The 2000-cent (20 euro) cap is an absolute ceiling on the fee charged,
  applied the same way regardless of section - it is not itself halved
  for the children's section.
- "Half rate" means the children's section is charged 25 cents/day
  against the same 2000-cent cap, so it takes twice as many late days
  to reach the cap.
"""

import pytest

from library_fees import late_fee_cents


@pytest.mark.parametrize(
    ("days_late", "fee_cents"),
    [
        pytest.param(0, 0, id="on due date"),
        pytest.param(1, 50, id="one day late"),
        pytest.param(5, 250, id="five days late"),
    ],
)
def test_late_fee_by_days_late(days_late, fee_cents):
    assert late_fee_cents(days_late, childrens_section=False) == fee_cents


@pytest.mark.parametrize(
    ("days_late", "fee_cents"),
    [
        pytest.param(39, 1950, id="just under the cap"),
        pytest.param(40, 2000, id="at the cap"),
        pytest.param(41, 2000, id="just over the cap"),
        pytest.param(100, 2000, id="well past the cap"),
    ],
)
def test_late_fee_capped_at_20_euros(days_late, fee_cents):
    assert late_fee_cents(days_late, childrens_section=False) == fee_cents


@pytest.mark.parametrize(
    ("days_late", "fee_cents"),
    [
        pytest.param(0, 0, id="on due date, childrens section"),
        pytest.param(1, 25, id="one day late, childrens section"),
        pytest.param(5, 125, id="five days late, childrens section"),
    ],
)
def test_late_fee_childrens_section_half_rate(days_late, fee_cents):
    assert late_fee_cents(days_late, childrens_section=True) == fee_cents


@pytest.mark.parametrize(
    ("days_late", "fee_cents"),
    [
        pytest.param(79, 1975, id="childrens section just under the cap"),
        pytest.param(80, 2000, id="childrens section at the cap"),
        pytest.param(100, 2000, id="childrens section past the cap"),
    ],
)
def test_late_fee_cap_applies_regardless_of_section(days_late, fee_cents):
    assert late_fee_cents(days_late, childrens_section=True) == fee_cents
