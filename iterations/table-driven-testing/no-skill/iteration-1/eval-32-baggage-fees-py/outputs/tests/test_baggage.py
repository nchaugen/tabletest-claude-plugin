import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    "weight_kg, expected_fee",
    [
        (0, 0),
        (1, 0),
        (22, 0),
        (23, 0),
        (24, 75),
        (25, 75),
        (31, 75),
        (32, 75),
        (33, 150),
        (34, 150),
        (44, 150),
        (45, 150),
    ],
)
def test_fee_for_checked_bag(weight_kg, expected_fee):
    assert fee_for_checked_bag(weight_kg) == expected_fee


@pytest.mark.parametrize(
    "weight_kg",
    [46, 50, 100],
)
def test_fee_for_checked_bag_rejects_oversize(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
