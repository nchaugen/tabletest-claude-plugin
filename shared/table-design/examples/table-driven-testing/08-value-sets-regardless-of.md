```python
# One case per ignored value, varying together — not stacked generators, which cross them.
@pytest.mark.parametrize(("haemoglobin", "recent_travel"), [(125, True), (140, False)])
def test_under_age_donor_is_ineligible_regardless(haemoglobin, recent_travel):
    assert not is_eligible(age=16, haemoglobin=haemoglobin, recent_travel=recent_travel)
```

Age alone decides it, and the two inputs it ignores each take both their values, so a case could
contradict the claim.
