# Assumptions (rules didn't spell these out):
# - days_late is never negative; 0 is the earliest value and means "returned on
#   the due date", so early returns aren't a separate case.
# - The 20-euro cap (2000 cents) is a flat ceiling that applies after the rate
#   (standard or children's half rate) has been charged per day -- the
#   children's discount does not also apply to the cap itself.
import pytest

from library_fees import late_fee_cents


@pytest.mark.parametrize("childrens_section", [False, True])
def test_returned_on_the_due_date_incurs_no_fee(childrens_section):
    assert late_fee_cents(days_late=0, childrens_section=childrens_section) == 0


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "expected_fee_cents"),
    [
        pytest.param(1, False, 50, id="one day late, standard section"),
        pytest.param(1, True, 25, id="one day late, children's section"),
        pytest.param(10, False, 500, id="ten days late, standard section"),
        pytest.param(10, True, 250, id="ten days late, children's section"),
    ],
)
def test_computes_late_fee_from_days_late_and_section(
    days_late, childrens_section, expected_fee_cents
):
    assert (
        late_fee_cents(days_late=days_late, childrens_section=childrens_section)
        == expected_fee_cents
    )


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "expected_fee_cents"),
    [
        pytest.param(39, False, 1950, id="just below the cap, standard section"),
        pytest.param(40, False, 2000, id="at the cap boundary, standard section"),
        pytest.param(41, False, 2000, id="just past the cap boundary, standard section"),
        pytest.param(79, True, 1975, id="just below the cap, children's section"),
        pytest.param(80, True, 2000, id="at the cap boundary, children's section"),
        pytest.param(81, True, 2000, id="just past the cap boundary, children's section"),
    ],
)
def test_caps_late_fee_at_twenty_euros(days_late, childrens_section, expected_fee_cents):
    assert (
        late_fee_cents(days_late=days_late, childrens_section=childrens_section)
        == expected_fee_cents
    )
