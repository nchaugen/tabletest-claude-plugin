```python
pytest.param("solvent", "sealed drum", Route.HAZARDOUS, id="solvent in a sealed drum"),   # condition
pytest.param("solvent", "sealed drum", Route.HAZARDOUS, id="goes to hazardous"),          # outcome — no
pytest.param("solvent", "sealed drum", Route.HAZARDOUS, id="sealed_drum-hazardous"),      # both — still no
```
