```python
@pytest.mark.parametrize(("humidity", "override_setpoint", "vent"), [
    pytest.param(80, None, Vent.OPEN,   id="no override configured"),
    pytest.param(80, 90,   Vent.CLOSED, id="override supplied"),
])
def test_vent_position(humidity, override_setpoint, vent):
    assert resolve_vent(humidity, override_setpoint) == vent
```

`None` states the absence. A `0` would state a different scenario, and defaulting it inside the test
body would state nothing.
