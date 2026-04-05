I have this Kotest data-driven test for our shipping cost calculator and I'd like to convert it to use TableTest:

```kotlin
class ShippingCostCalculatorTest : FunSpec({

    val calculator = ShippingCostCalculator()

    context("shipping cost calculation") {
        withData(
            row(
                "EU standard light",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("0.5"), listOf(20, 15, 10),
                false, null, null,
                Carrier.DHL, BigDecimal("5.00")
            ),
            row(
                "EU standard medium",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                false, null, null,
                Carrier.DHL, BigDecimal("7.50")
            ),
            row(
                "EU standard heavy",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("10.0"), listOf(40, 30, 20),
                false, null, null,
                Carrier.DHL, BigDecimal("12.50")
            ),
            row(
                "EU standard very heavy",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("25.0"), listOf(50, 40, 30),
                false, null, null,
                Carrier.UPS, BigDecimal("20.00")
            ),
            row(
                "EU express light",
                ShippingZone(region = "EU", speed = "express"),
                BigDecimal("0.5"), listOf(20, 15, 10),
                false, null, null,
                Carrier.DHL, BigDecimal("8.00")
            ),
            row(
                "EU express medium",
                ShippingZone(region = "EU", speed = "express"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                false, null, null,
                Carrier.UPS, BigDecimal("12.00")
            ),
            row(
                "US standard light",
                ShippingZone(region = "US", speed = "standard"),
                BigDecimal("0.5"), listOf(20, 15, 10),
                false, null, null,
                Carrier.FEDEX, BigDecimal("7.00")
            ),
            row(
                "US express heavy",
                ShippingZone(region = "US", speed = "express"),
                BigDecimal("10.0"), listOf(40, 30, 20),
                false, null, null,
                Carrier.DHL, BigDecimal("30.00")
            ),
            row(
                "dimensional weight override",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("1.0"), listOf(70, 50, 10),
                false, null, null,
                Carrier.DHL, BigDecimal("12.50")
            ),
            row(
                "fragile surcharge",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                true, null, null,
                Carrier.DHL, BigDecimal("8.625")
            ),
            row(
                "insured surcharge",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                false, 500, null,
                Carrier.DHL, BigDecimal("10.50")
            ),
            row(
                "fragile and insured",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                true, 200, null,
                Carrier.DHL, BigDecimal("11.625")
            ),
            row(
                "hazmat handling",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                false, null, "hazmat",
                Carrier.DHL, BigDecimal("15.50")
            ),
            row(
                "oversize surcharge",
                ShippingZone(region = "EU", speed = "standard"),
                BigDecimal("3.0"), listOf(120, 5, 5),
                false, null, null,
                Carrier.DHL, BigDecimal("17.50")
            ),
            row(
                "same rate DHL",
                ShippingZone(region = "EU", speed = "express"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                false, null, null,
                Carrier.DHL, BigDecimal("12.00")
            ),
            row(
                "same rate UPS",
                ShippingZone(region = "EU", speed = "express"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                false, null, null,
                Carrier.UPS, BigDecimal("12.00")
            ),
            row(
                "same rate FEDEX",
                ShippingZone(region = "EU", speed = "express"),
                BigDecimal("3.0"), listOf(30, 20, 15),
                false, null, null,
                Carrier.FEDEX, BigDecimal("12.00")
            )
        ) { (scenario, zone, weight, dims, fragile, insuredValue, handling, carrier, expected) ->
            val opts = PackageOptions()
            if (fragile as Boolean) opts.isFragile = true
            if (insuredValue != null) opts.insuredValue = insuredValue as Int
            if (handling != null) opts.handling = handling as String
            calculator.calculateShippingCost(zone, weight, dims, opts, carrier) shouldBe expected
        }
    }
})
```

Can you convert this to TableTest?
