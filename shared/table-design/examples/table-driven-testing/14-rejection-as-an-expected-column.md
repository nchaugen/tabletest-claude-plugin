```python
# One rule: the whole test asks whether the dose is accepted.
@pytest.mark.parametrize(("dose_mg", "expected_error"), [
    pytest.param(0,     None,       id="at the minimum dose"),
    pytest.param(-0.01, ValueError, id="just below the minimum dose"),
])
def test_rejects_dose_below_minimum(dose_mg, expected_error):
    assert thrown_by(lambda: validate_dose(dose_mg)) == expected_error
```

Go's `wantErr` field is this same shape and is already idiomatic there. Where rejection is one
outcome among several, split instead — two concerns, two tests.
