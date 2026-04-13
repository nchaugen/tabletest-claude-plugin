package com.example

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

data class ShippingScenario(
    val name: String,
    val zone: ShippingZone,
    val weight: Double,
    val dimensions: List<Int>,
    val fragile: Boolean,
    val insuredValue: BigDecimal?,
    val handling: String?,
    val carrier: Carrier,
    val expected: BigDecimal
) {
    override fun toString() = name
}

class ShippingCostCalculatorTest : FunSpec({

    val calculator = ShippingCostCalculator()

    context("shipping cost calculation") {
        withData(
            ShippingScenario("EU standard light", ShippingZone("EU", "standard"), 0.5, listOf(20, 15, 10), false, null, null, Carrier.DHL, BigDecimal("5.00")),
            ShippingScenario("EU standard medium", ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), false, null, null, Carrier.DHL, BigDecimal("7.50")),
            ShippingScenario("EU standard heavy", ShippingZone("EU", "standard"), 10.0, listOf(40, 30, 20), false, null, null, Carrier.DHL, BigDecimal("12.50")),
            ShippingScenario("EU standard very heavy", ShippingZone("EU", "standard"), 25.0, listOf(50, 40, 30), false, null, null, Carrier.UPS, BigDecimal("20.00")),
            ShippingScenario("EU express light", ShippingZone("EU", "express"), 0.5, listOf(20, 15, 10), false, null, null, Carrier.DHL, BigDecimal("8.00")),
            ShippingScenario("EU express medium", ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), false, null, null, Carrier.UPS, BigDecimal("12.00")),
            ShippingScenario("US standard light", ShippingZone("US", "standard"), 0.5, listOf(20, 15, 10), false, null, null, Carrier.FEDEX, BigDecimal("7.00")),
            ShippingScenario("US express heavy", ShippingZone("US", "express"), 10.0, listOf(40, 30, 20), false, null, null, Carrier.DHL, BigDecimal("30.00")),
            ShippingScenario("dimensional weight override", ShippingZone("EU", "standard"), 1.0, listOf(70, 50, 10), false, null, null, Carrier.DHL, BigDecimal("12.50")),
            ShippingScenario("fragile surcharge", ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), true, null, null, Carrier.DHL, BigDecimal("8.625")),
            ShippingScenario("insured surcharge", ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), false, BigDecimal("500"), null, Carrier.DHL, BigDecimal("10.50")),
            ShippingScenario("fragile and insured", ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), true, BigDecimal("200"), null, Carrier.DHL, BigDecimal("11.625")),
            ShippingScenario("hazmat handling", ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), false, null, "hazmat", Carrier.DHL, BigDecimal("15.50")),
            ShippingScenario("oversize surcharge", ShippingZone("EU", "standard"), 3.0, listOf(120, 5, 5), false, null, null, Carrier.DHL, BigDecimal("17.50")),
            ShippingScenario("same rate DHL", ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), false, null, null, Carrier.DHL, BigDecimal("12.00")),
            ShippingScenario("same rate UPS", ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), false, null, null, Carrier.UPS, BigDecimal("12.00")),
            ShippingScenario("same rate FEDEX", ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), false, null, null, Carrier.FEDEX, BigDecimal("12.00"))
        ) { scenario ->
            val opts = PackageOptions()
            if (scenario.fragile) opts.isFragile = true
            if (scenario.insuredValue != null) opts.insuredValue = scenario.insuredValue
            if (scenario.handling != null) opts.handling = scenario.handling
            calculator.calculateShippingCost(scenario.zone, scenario.weight, scenario.dimensions, opts, scenario.carrier).compareTo(scenario.expected) shouldBe 0
        }
    }
})
