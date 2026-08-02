import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    ("weight_kg", "fee_euros"),
    [
        pytest.param(23, 0, id="at the included-weight limit"),
        pytest.param(24, 75, id="just over the included-weight limit"),
        pytest.param(32, 75, id="at the heavy-bag limit"),
        pytest.param(33, 150, id="just over the heavy-bag limit"),
        pytest.param(45, 150, id="at the oversize limit"),
    ],
)
def test_computes_checked_bag_fee_from_weight(weight_kg, fee_euros):
    assert fee_for_checked_bag(weight_kg) == fee_euros


@pytest.mark.parametrize(
    "weight_kg",
    [
        pytest.param(46, id="just over the oversize limit"),
    ],
)
def test_rejects_bags_over_the_oversize_limit(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
