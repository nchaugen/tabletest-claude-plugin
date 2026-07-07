"""Table-driven tests for late_fee_cents.

Assumptions:
- days_late is a non-negative count of days past the due date (0 means
  returned on or before the due date); returning early is not modelled
  as a negative value.
- The 20-euro cap (2000 cents) applies to the final fee for both the
  standard and children's sections.
"""

import pytest

from library_fees import late_fee_cents

FEE_CAP_CENTS = 2000  # 20 euros


@pytest.mark.parametrize(
    "childrens_section",
    [
        pytest.param(False, id="standard section"),
        pytest.param(True, id="childrens section"),
    ],
)
def test_no_fee_when_returned_by_due_date(childrens_section):
    assert late_fee_cents(days_late=0, childrens_section=childrens_section) == 0


@pytest.mark.parametrize(
    ("days_late", "expected_cents"),
    [
        pytest.param(1, 50, id="one day late"),
        pytest.param(2, 100, id="two days late"),
        pytest.param(39, 1950, id="just under the 2000c cap"),
        pytest.param(40, FEE_CAP_CENTS, id="at the 2000c cap"),
        pytest.param(41, FEE_CAP_CENTS, id="just over the 2000c cap"),
        pytest.param(100, FEE_CAP_CENTS, id="well past the 2000c cap"),
    ],
)
def test_late_fee_standard_section(days_late, expected_cents):
    assert late_fee_cents(days_late=days_late, childrens_section=False) == expected_cents


@pytest.mark.parametrize(
    ("days_late", "expected_cents"),
    [
        pytest.param(1, 25, id="one day late"),
        pytest.param(2, 50, id="two days late"),
        pytest.param(79, 1975, id="just under the 2000c cap"),
        pytest.param(80, FEE_CAP_CENTS, id="at the 2000c cap"),
        pytest.param(81, FEE_CAP_CENTS, id="just over the 2000c cap"),
        pytest.param(200, FEE_CAP_CENTS, id="well past the 2000c cap"),
    ],
)
def test_late_fee_childrens_section(days_late, expected_cents):
    assert late_fee_cents(days_late=days_late, childrens_section=True) == expected_cents
