import pytest

from library_fees import late_fee_cents

# Assumptions (ambiguous in the feature description):
# - `days_late` <= 0 means returned on or before the due date; the boundary
#   tested is between 0 (on time) and 1 (one day late).
# - The 20-euro cap is a flat ceiling on the fee actually charged, applied
#   after the children's-section half rate, not a separate halved cap.
FEE_CAP_CENTS = 2000  # 20 euros


@pytest.mark.parametrize(
    ("days_late", "childrens_section", "fee_cap_cents", "fee_cents"),
    [
        pytest.param(0, False, FEE_CAP_CENTS, 0, id="returned on the due date, general section"),
        pytest.param(0, True, FEE_CAP_CENTS, 0, id="returned on the due date, children's section"),
        pytest.param(1, False, FEE_CAP_CENTS, 50, id="one day late, general section"),
        pytest.param(1, True, FEE_CAP_CENTS, 25, id="one day late, children's section"),
        pytest.param(39, False, FEE_CAP_CENTS, 1950, id="just below the fee cap, general section"),
        pytest.param(40, False, FEE_CAP_CENTS, 2000, id="at the fee cap boundary, general section"),
        pytest.param(41, False, FEE_CAP_CENTS, 2000, id="just past the fee cap, general section"),
        pytest.param(79, True, FEE_CAP_CENTS, 1975, id="just below the fee cap, children's section"),
        pytest.param(80, True, FEE_CAP_CENTS, 2000, id="at the fee cap boundary, children's section"),
        pytest.param(81, True, FEE_CAP_CENTS, 2000, id="just past the fee cap, children's section"),
    ],
)
def test_computes_late_fee_from_days_late_and_section(days_late, childrens_section, fee_cap_cents, fee_cents):
    assert late_fee_cents(days_late, childrens_section) == fee_cents
