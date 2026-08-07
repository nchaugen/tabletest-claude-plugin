import pytest

from library_fees import late_fee_cents

"""
Fee rules exercised below: 50 cents/day late at the standard rate, half that
for children's-section books, with every fee capped at 2000 cents (20 euros).
The cap is reached at 40 days for standard-rate books and at 80 days for
children's-section books, since the discount halves the daily accrual.

Assumptions (the feature description leaves these implicit):
- days_late is always >= 0; 0 means the book was returned on or before the
  due date.
- The 20-euro cap applies to the final fee after any children's-section
  discount.
"""


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "fee_cents"),
    [
        pytest.param(0, False, 0, id="returned on the due date"),
        pytest.param(1, False, 50, id="one day late"),
        pytest.param(10, False, 500, id="several days late"),
        pytest.param(40, False, 2000, id="at the fee cap boundary"),
        pytest.param(41, False, 2000, id="one day past the fee cap boundary"),
        pytest.param(0, True, 0, id="children's book returned on the due date"),
        pytest.param(10, True, 250, id="children's book several days late"),
        pytest.param(80, True, 2000, id="children's book at the fee cap boundary"),
        pytest.param(81, True, 2000, id="children's book one day past the fee cap boundary"),
    ],
)
def test_calculates_late_fee_from_days_late_and_section(days_late, childrens_section, fee_cents):
    assert late_fee_cents(days_late, childrens_section) == fee_cents
