import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    ("weight_kg", "fee"),
    [
        pytest.param(22, 0, id="just under the included limit"),
        pytest.param(23, 0, id="at the included limit"),
        pytest.param(24, 75, id="just over the included limit"),
        pytest.param(32, 75, id="at the heavy-bag limit"),
        pytest.param(33, 150, id="just over the heavy-bag limit"),
        pytest.param(45, 150, id="at the oversize limit"),
    ],
)
def test_fee_for_checked_bag_by_weight(weight_kg, fee):
    assert fee_for_checked_bag(weight_kg) == fee


@pytest.mark.parametrize(
    "weight_kg",
    [
        pytest.param(46, id="just over the oversize limit"),
        pytest.param(80, id="far beyond the oversize limit"),
    ],
)
def test_fee_for_checked_bag_rejects_over_oversize_limit(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
