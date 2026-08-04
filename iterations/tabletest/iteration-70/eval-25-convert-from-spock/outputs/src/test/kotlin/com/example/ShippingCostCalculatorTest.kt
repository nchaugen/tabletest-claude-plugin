package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Weight tiers are the same across every zone (light <=1, medium <=5, heavy <=15,
        very heavy >15) — only the rate per tier differs by region and speed. Carrier is
        accepted by the API but never affects the price, shown by the last row.
        """)
    @TableTest("""
        Scenario                            | Zone                          | Weight | Dimensions   | Options                            | Carrier           | Cost?
        EU standard, light                  | [region: EU, speed: standard] | 0.5    | [20, 15, 10] | [:]                                | DHL               | 5.00
        EU standard, medium                 | [region: EU, speed: standard] | 3.0    | [30, 20, 15] | [:]                                | DHL               | 7.50
        EU standard, heavy                  | [region: EU, speed: standard] | 10.0   | [40, 30, 20] | [:]                                | DHL               | 12.50
        EU standard, very heavy             | [region: EU, speed: standard] | 25.0   | [50, 40, 30] | [:]                                | DHL               | 20.00
        EU express, light                   | [region: EU, speed: express]  | 0.5    | [20, 15, 10] | [:]                                | DHL               | 8.00
        EU express, medium                  | [region: EU, speed: express]  | 3.0    | [30, 20, 15] | [:]                                | DHL               | 12.00
        US standard, light                  | [region: US, speed: standard] | 0.5    | [20, 15, 10] | [:]                                | DHL               | 7.00
        US express, heavy                   | [region: US, speed: express]  | 10.0   | [40, 30, 20] | [:]                                | DHL               | 30.00
        Dimensional weight overrides actual | [region: EU, speed: standard] | 1.0    | [70, 50, 10] | [:]                                | DHL               | 12.50
        Oversize dimension adds a flat fee  | [region: EU, speed: standard] | 3.0    | [120, 5, 5]  | [:]                                | DHL               | 17.50
        Fragile item adds a surcharge       | [region: EU, speed: standard] | 3.0    | [30, 20, 15] | [fragile: true]                    | DHL               | 8.625
        Insured item adds a premium         | [region: EU, speed: standard] | 3.0    | [30, 20, 15] | [insuredValue: 500]                | DHL               | 10.50
        Fragile and insured combine         | [region: EU, speed: standard] | 3.0    | [30, 20, 15] | [fragile: true, insuredValue: 200] | DHL               | 11.625
        Hazmat handling adds a fee          | [region: EU, speed: standard] | 3.0    | [30, 20, 15] | [handling: hazmat]                 | DHL               | 15.50
        Carrier does not affect the cost    | [region: EU, speed: express]  | 3.0    | [30, 20, 15] | [:]                                | {DHL, UPS, FEDEX} | 12.00
        """)
    fun calculatesShippingCost(
        zone: ShippingZone,
        weight: Double,
        dimensions: List<Int>,
        options: PackageOptions,
        carrier: Carrier,
        cost: BigDecimal
    ) {
        val actual = calculator.calculateShippingCost(zone, weight, dimensions, options, carrier)
        assertEquals(0, cost.compareTo(actual)) { "expected $cost but was $actual" }
    }
}

@TypeConverter
fun parseShippingZone(fields: Map<String, String>): ShippingZone =
    ShippingZone(region = fields.getValue("region"), speed = fields.getValue("speed"))

@TypeConverter
fun parsePackageOptions(fields: Map<String, String>): PackageOptions {
    val options = PackageOptions()
    options.isFragile = fields["fragile"]?.toBoolean() ?: false
    options.insuredValue = fields["insuredValue"]?.let { BigDecimal(it) }
    options.handling = fields["handling"]
    return options
}
