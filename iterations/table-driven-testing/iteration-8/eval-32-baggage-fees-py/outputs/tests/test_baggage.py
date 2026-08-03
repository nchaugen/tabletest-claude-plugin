import pytest

from baggage import BagNotAccepted, fee_for_checked_bag


@pytest.mark.parametrize(
    ("weight_kg", "fee_eur"),
    [
        pytest.param(23, 0, id="23 kg, top of the included allowance"),
        pytest.param(24, 75, id="24 kg, bottom of the heavy-bag tier"),
        pytest.param(32, 75, id="32 kg, top of the heavy-bag tier"),
        pytest.param(33, 150, id="33 kg, bottom of the oversize tier"),
        pytest.param(45, 150, id="45 kg, top of the oversize tier"),
    ],
)
def test_charges_the_tier_fee_matching_bag_weight(weight_kg, fee_eur):
    assert fee_for_checked_bag(weight_kg) == fee_eur


def test_rejects_a_bag_over_the_oversize_limit():
    with pytest.raises(BagNotAccepted):
        fee_for_checked_bag(46)
