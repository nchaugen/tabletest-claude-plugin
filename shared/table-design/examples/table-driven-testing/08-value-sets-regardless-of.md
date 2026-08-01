```python
# Stacked parametrize generates the combinations the rule ignores.
@pytest.mark.parametrize("recent_travel", [True, False])
@pytest.mark.parametrize("haemoglobin", [125, 140])
def test_under_age_donor_is_ineligible_regardless(haemoglobin, recent_travel):
    assert not is_eligible(age=16, haemoglobin=haemoglobin, recent_travel=recent_travel)
```
