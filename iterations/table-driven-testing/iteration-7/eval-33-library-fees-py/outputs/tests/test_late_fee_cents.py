import pytest

from library_fees import late_fee_cents


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "fee_cents"),
    [
        pytest.param(0, False, 0, id="returned on the due date"),
        pytest.param(0, True, 0, id="returned on the due date, children's section"),
        pytest.param(1, False, 50, id="one day late"),
        pytest.param(1, True, 25, id="one day late, children's section"),
        pytest.param(10, False, 500, id="several days late, under the cap"),
        pytest.param(40, False, 2000, id="just reaches the fee cap"),
        pytest.param(41, False, 2000, id="just past the fee cap"),
        pytest.param(80, True, 2000, id="just reaches the fee cap, children's section"),
        pytest.param(81, True, 2000, id="just past the fee cap, children's section"),
    ],
)
def test_computes_late_fee_from_days_late_and_section(days_late, childrens_section, fee_cents):
    """
    Standard rate is 50 cents per day late; children's section books are
    charged half that (25 cents per day). Fees cap at 2000 cents (20 euros)
    regardless of section. days_late of 0 represents a book returned by its
    due date; the function is assumed never to be called with a negative
    value.
    """
    assert late_fee_cents(days_late, childrens_section) == fee_cents
