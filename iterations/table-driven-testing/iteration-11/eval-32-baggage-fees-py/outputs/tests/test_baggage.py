"""Weight is a whole number of kilograms; each tier's upper limit is inclusive
("up to X kg" bears the fee of that tier, not the next one).
"""

import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    ("weight_kg", "fee_eur"),
    [
        pytest.param(23, 0, id="at the no-fee limit"),
        pytest.param(24, 75, id="heavy-bag fee begins"),
        pytest.param(32, 75, id="at the heavy-bag limit"),
        pytest.param(33, 150, id="oversize fee begins"),
        pytest.param(45, 150, id="at the oversize limit"),
    ],
)
def test_charges_the_fee_for_the_bags_weight_tier(weight_kg, fee_eur):
    assert fee_for_checked_bag(weight_kg) == fee_eur


@pytest.mark.parametrize(
    "weight_kg",
    [
        pytest.param(46, id="just over the oversize limit"),
    ],
)
def test_rejects_a_bag_over_the_oversize_limit(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
