```python
# One case, three candidate ids — only the first names a condition.
pytest.param("solvent", "sealed drum", Route.HAZARDOUS, id="solvent in a sealed drum")
#                                                       id="goes to hazardous"     <- names the outcome
#                                                       id="sealed_drum-hazardous" <- names both
```
