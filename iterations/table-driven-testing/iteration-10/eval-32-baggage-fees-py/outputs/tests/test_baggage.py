import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    ("weight_kg", "fee_eur"),
    [
        pytest.param(23, 0, id="top of no-fee tier"),
        pytest.param(24, 75, id="just above no-fee tier"),
        pytest.param(32, 75, id="top of heavy-bag tier"),
        pytest.param(33, 150, id="just above heavy-bag tier"),
        pytest.param(45, 150, id="top of oversize tier"),
    ],
)
def test_charges_fee_by_checked_bag_weight(weight_kg, fee_eur):
    assert fee_for_checked_bag(weight_kg) == fee_eur


@pytest.mark.parametrize(
    "weight_kg",
    [
        pytest.param(46, id="just above oversize tier"),
    ],
)
def test_rejects_bags_over_the_oversize_limit(weight_kg):
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(weight_kg)
