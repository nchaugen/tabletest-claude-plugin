import pytest

from baggage import BagNotAccepted, fee_for_checked_bag

NO_FEE = 0
HEAVY_BAG_FEE = 75
OVERSIZE_FEE = 150


@pytest.mark.parametrize(
    ("weight_kg", "expected_fee"),
    [
        pytest.param(0, NO_FEE, id="zero_weight-no_fee"),
        pytest.param(1, NO_FEE, id="well_under_included_limit-no_fee"),
        pytest.param(23, NO_FEE, id="at_included_limit-no_fee"),
        pytest.param(24, HEAVY_BAG_FEE, id="just_over_included_limit-heavy_bag_fee"),
        pytest.param(28, HEAVY_BAG_FEE, id="well_within_heavy_bag_range-heavy_bag_fee"),
        pytest.param(32, HEAVY_BAG_FEE, id="at_heavy_bag_limit-heavy_bag_fee"),
        pytest.param(33, OVERSIZE_FEE, id="just_over_heavy_bag_limit-oversize_fee"),
        pytest.param(40, OVERSIZE_FEE, id="well_within_oversize_range-oversize_fee"),
        pytest.param(45, OVERSIZE_FEE, id="at_oversize_limit-oversize_fee"),
    ],
)
def test_fee_for_checked_bag_within_limits(weight_kg, expected_fee):
    assert fee_for_checked_bag(weight_kg) == expected_fee


@pytest.mark.parametrize(
    "weight_kg",
    [
        pytest.param(46, id="just_over_oversize_limit"),
        pytest.param(60, id="well_over_oversize_limit"),
    ],
)
def test_fee_for_checked_bag_rejects_bags_over_limit(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
