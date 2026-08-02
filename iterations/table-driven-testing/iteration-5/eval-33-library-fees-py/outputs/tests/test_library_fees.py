"""Tests for late_fee_cents: the fee for a single returned book.

Assumptions:
- days_late is non-negative; 0 means returned on or before the due date.
  Early returns (negative days_late) are out of scope for this rule.
- The 20 euro (2000 cent) cap applies to the final per-book fee, i.e. after
  the children's-section half rate has been applied, not to the standard
  rate before the discount.
"""

import pytest

from library_fees import late_fee_cents


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "fee_cents"),
    [
        pytest.param(0, False, 0, id="due date met, standard rate"),
        pytest.param(0, True, 0, id="due date met, children's section"),
        pytest.param(1, False, 50, id="one day late, standard rate"),
        pytest.param(10, False, 500, id="several days late, standard rate"),
        pytest.param(1, True, 25, id="one day late, children's section"),
        pytest.param(10, True, 250, id="several days late, children's section"),
        pytest.param(40, False, 2000, id="fee reaches the cap exactly, standard rate"),
        pytest.param(41, False, 2000, id="fee exceeds the cap, standard rate"),
        pytest.param(80, True, 2000, id="fee reaches the cap exactly, children's section"),
        pytest.param(81, True, 2000, id="fee exceeds the cap, children's section"),
    ],
)
def test_computes_late_fee_from_days_late_and_section(days_late, childrens_section, fee_cents):
    assert late_fee_cents(days_late, childrens_section) == fee_cents
