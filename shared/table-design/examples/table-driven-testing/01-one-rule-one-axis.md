```python
# Two concerns, two tests. "Duty eligibility AND rest credit" fails the "and" test.
@pytest.mark.parametrize(("hours_since_rest", "max_duty_hours", "fit_to_fly"), [
    pytest.param(6,  13, True,  id="well inside the limit"),
    pytest.param(13, 13, True,  id="at the limit"),
    pytest.param(14, 13, False, id="past the limit"),
])
def test_fitness_to_fly(hours_since_rest, max_duty_hours, fit_to_fly):
    assert is_fit_to_fly(hours_since_rest, max_duty_hours) == fit_to_fly
```
