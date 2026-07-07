import pytest

from library_fees import late_fee_cents

# days_late, childrens_section, expected_fee_cents
CASES = [
    (0, False, 0),  # returned on the due date: no fee
    (-3, False, 0),  # returned early: no fee
    (1, False, 50),  # one day late, standard rate: 50 cents/day
    (4, False, 200),  # several days late, standard rate
    (40, False, 2000),  # exactly at the 20 euro cap
    (41, False, 2000),  # one day past the cap: still capped at 20 euros
    (1000, False, 2000),  # far past the cap: still capped at 20 euros
    (1, True, 25),  # one day late, children's section: half rate (25 cents/day)
    (4, True, 100),  # several days late, children's section: half rate
    (0, True, 0),  # children's section, returned on time: no fee
    (80, True, 2000),  # children's section: cap still applies at 20 euros
    (81, True, 2000),  # children's section: still capped past the threshold
]


@pytest.mark.parametrize("days_late, childrens_section, expected_fee_cents", CASES)
def test_late_fee_cents(days_late, childrens_section, expected_fee_cents):
    assert late_fee_cents(days_late, childrens_section) == expected_fee_cents
