import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    ("weight_kg", "fee"),
    [
        pytest.param(5, 0, id="well within the included weight limit"),
        pytest.param(23, 0, id="at the included weight limit"),
        pytest.param(24, 75, id="just over the included weight limit"),
        pytest.param(32, 75, id="at the heavy-bag weight limit"),
        pytest.param(33, 150, id="just over the heavy-bag weight limit"),
        pytest.param(45, 150, id="at the oversize weight limit"),
    ],
)
def test_fee_for_checked_bag_by_weight(weight_kg, fee):
    assert fee_for_checked_bag(weight_kg) == fee


@pytest.mark.parametrize(
    "weight_kg",
    [
        pytest.param(46, id="just over the oversize weight limit"),
        pytest.param(100, id="far over the oversize weight limit"),
    ],
)
def test_fee_for_checked_bag_rejects_bags_over_the_oversize_limit(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
