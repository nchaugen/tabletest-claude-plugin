import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


def _fee_or_error(weight_kg):
    try:
        return fee_for_checked_bag(weight_kg), None
    except Exception as error:
        return None, type(error)


@pytest.mark.parametrize(
    ("weight_kg", "fee_eur", "error"),
    [
        pytest.param(23, 0, None, id="at the free allowance limit"),
        pytest.param(24, 75, None, id="just over the free allowance limit"),
        pytest.param(32, 75, None, id="at the heavy-bag limit"),
        pytest.param(33, 150, None, id="just over the heavy-bag limit"),
        pytest.param(45, 150, None, id="at the oversize limit"),
        pytest.param(46, None, BagNotAccepted, id="just over the oversize limit"),
    ],
)
def test_charges_checked_bag_fee_by_weight(weight_kg, fee_eur, error):
    """Weight in kilograms is the only input; bags of 0 kg or less are out of
    scope since the rules do not define a lower bound."""
    assert _fee_or_error(weight_kg) == (fee_eur, error)
