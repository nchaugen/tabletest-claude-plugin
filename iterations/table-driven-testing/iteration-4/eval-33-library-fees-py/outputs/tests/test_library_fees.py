"""Table-driven tests for late_fee_cents.

Assumptions (spec doesn't state these explicitly):
- days_late is always >= 0; on time is represented by days_late == 0.
  Early returns (negative days_late) are out of scope.
- The 20-euro cap is a single fixed ceiling that applies the same way
  regardless of section; the children's discount is not applied to the cap
  itself, only to the per-day rate.
"""

import pytest

from library_fees import late_fee_cents


@pytest.mark.parametrize(
    ("days_late", "childrens_section"),
    [
        pytest.param(0, False, id="returned on the due date, standard section"),
        pytest.param(0, True, id="returned on the due date, children's section"),
    ],
)
def test_on_time_return_has_no_fee(days_late, childrens_section):
    assert late_fee_cents(days_late, childrens_section) == 0


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "fee_cents"),
    [
        pytest.param(1, False, 50, id="one day late, standard section"),
        pytest.param(3, False, 150, id="three days late, standard section"),
        pytest.param(10, False, 500, id="ten days late, standard section"),
        pytest.param(1, True, 25, id="one day late, children's section"),
        pytest.param(3, True, 75, id="three days late, children's section"),
        pytest.param(10, True, 250, id="ten days late, children's section"),
    ],
)
def test_late_fee_accrues_per_day_late(days_late, childrens_section, fee_cents):
    assert late_fee_cents(days_late, childrens_section) == fee_cents


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "fee_cap_cents", "fee_cents"),
    [
        pytest.param(39, False, 2000, 1950, id="just under the cap, standard rate"),
        pytest.param(40, False, 2000, 2000, id="at the cap, standard rate"),
        pytest.param(41, False, 2000, 2000, id="just over the cap, standard rate"),
        pytest.param(79, True, 2000, 1975, id="just under the cap, children's rate"),
        pytest.param(80, True, 2000, 2000, id="at the cap, children's rate"),
        pytest.param(81, True, 2000, 2000, id="just over the cap, children's rate"),
    ],
)
def test_fee_is_capped_at_20_euros(days_late, childrens_section, fee_cap_cents, fee_cents):
    assert late_fee_cents(days_late, childrens_section) == fee_cents
    assert fee_cents <= fee_cap_cents
