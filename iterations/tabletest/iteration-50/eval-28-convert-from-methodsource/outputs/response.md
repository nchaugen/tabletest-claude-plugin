This looks solid. The build passed cleanly (47 tests, 0 failures). Summary of the conversion:

The old test packed 17 hand-written `Arguments` rows testing every concern (base rate tiers, dimensional weight, oversize, hazmat, fragile, insurance, carrier) through one flat method. I decomposed it into 5 `@TableTest` methods, each owning one rule:

- **`resolvesBaseRateByZoneAndWeight`** — the 4-zone × 4-weight-tier rate ladder (16 rows, full tier coverage vs. the original's partial sampling)
- **`selectsGreaterOfActualOrDimensionalWeight`** — actual vs. dimensional weight, both directions
- **`addsOversizeSurchargeForLongDimensions`** — boundary pair at the 100mm threshold
- **`appliesPackageOptionSurcharges`** — fragile/insurance/hazmat and their interactions (including the insurance-minimum boundary at 500/501, and the fragile-multiplies-hazmat-but-not-insurance ordering)
- **`computesCostIndependentOfCarrier`** — one row with a `{DHL, UPS, FEDEX}` value set, replacing 3 near-duplicate original cases

Domain object construction (`ShippingZone`, `PackageOptions`) moved into `@TypeConverter` methods so table rows stay declarative. I also added the missing `tabletest-junit` dependency to `pom.xml` since it wasn't there yet.