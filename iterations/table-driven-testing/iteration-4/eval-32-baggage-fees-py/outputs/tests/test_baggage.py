import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    ("weight_kg", "fee"),
    [
        pytest.param(10, 0, id="within ticket allowance"),
        pytest.param(23, 0, id="at ticket weight limit"),
        pytest.param(24, 75, id="just over ticket weight limit"),
        pytest.param(28, 75, id="within heavy-bag range"),
        pytest.param(32, 75, id="at heavy-bag fee limit"),
        pytest.param(33, 150, id="just over heavy-bag fee limit"),
        pytest.param(40, 150, id="within oversize range"),
        pytest.param(45, 150, id="at oversize fee limit"),
    ],
)
def test_fee_for_checked_bag_by_weight(weight_kg, fee):
    assert fee_for_checked_bag(weight_kg) == fee


@pytest.mark.parametrize(
    "weight_kg",
    [
        pytest.param(46, id="just over oversize limit"),
        pytest.param(60, id="well over oversize limit"),
    ],
)
def test_fee_for_checked_bag_rejects_over_oversize_limit(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
